/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2)의 길 찾기 방을 구현한 클래스이다.
 * 최근 수정 일자 : 2024.04.06(토)
 */
package com.pathfind.system.findPathService2Domain;

import com.pathfind.system.exception.ErrorCode;
import com.pathfind.system.exception.CustomException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPathRoom {
    private String roomId; //방 식별 아이디
    private String roomName; //방 이름
    private String ownerName; //방장 이름
    private List<String> invitedMember; //초대 회원
    private List<RoomMemberInfo> curMember; //현재 방에 존재하는 회원
    private LocalDateTime roomDeletionTime; //방 삭제 시간
    private TransportationType transportationType; // 이동 수단

    public static FindPathRoom createFindPathRoom(String roomName, TransportationType transportationType) {
        FindPathRoom newRoom = new FindPathRoom();
        newRoom.createRoomId();
        newRoom.changeRoomName(roomName);
        newRoom.ownerName = null; //방이 만들어질 때는 방장이 정해져있지 않다
        newRoom.curMember = new ArrayList<>(RoomValue.ROOM_MAX_MEMBER_NUM + 1);
        newRoom.invitedMember = new ArrayList<>(RoomValue.ROOM_MAX_MEMBER_NUM + 1); // 기본 사이즈가 6인 이유는 리스트의 size가 5가 되었을 때 capacity가 자동으로 늘어나는 것을 방지하기 위함이다.
        newRoom.changeRoomDeletionTime(LocalDateTime.now().plusMinutes(RoomValue.ROOM_DELETION_TIME));
        newRoom.transportationType = transportationType;
        return newRoom;
    }

    public void changeOwnerName(String nickname) {
        //방장을 임명하는 메소드
        this.ownerName = nickname;
    }

    private void changeRoomDeletionTime(LocalDateTime time) {
        this.roomDeletionTime = time;
    }

    private void changeRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean chkRoomInviteCnt() {
        //방의 초대 정원을 확인하는 함수
        //방에 들어가지 못하는 상황
        //방에 들어갈 수 있는 상황
        return getInvitedMember().size() + 1 <= RoomValue.ROOM_MAX_MEMBER_NUM;
    }

    public boolean chkRoomCurCnt() {
        //방의 현재 정원을 확인하는 함수
        return getCurMember().size() + 1 <= RoomValue.ROOM_MAX_MEMBER_NUM;
    }

    public void createRoomId() {
        StringBuilder randomPassword;
        randomPassword = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < RoomValue.ROOM_ID_LENGTH; i++) {
            int nextType = (int) (random.nextFloat() * 3);
            if (nextType == 0) randomPassword.append((char) (48 + random.nextInt(10)));
            else if (nextType == 1) randomPassword.append((char) (65 + random.nextInt(26)));
            else randomPassword.append((char) (97 + random.nextInt(26)));
        }
        this.roomId = randomPassword.toString();
    }


    public void pushNewMember(String nickname) {
        //초대 리스트에 회원을 넣는 메소드
        if (!chkRoomInviteCnt()) {
            throw new CustomException(ErrorCode.ROOM_EXCEEDED, "방에 초대 가능한 인원은 최대 5명 입니다.");
        }
        //초대된 인원을 관리하는 리스트는 초대된 인원을 관리하는 리스트일뿐이다 -> 회원의 이름만 저장하면 초대 리스트의 역할은 충족한 것이다
        //초대된 인원이 방장 or 일반 회원인지, 위치와 가까운 정점 아이디, 이동 수단은 방에 입장할 때 결정을 해주면 된다
        getInvitedMember().add(nickname);
    }

    public void enterRoom(String nickname, RoomMemberType roomMemberType, MemberLatLng location, Long closestVertexId) {
        //현재 인원 리스트에 회원을 넣는 메소드
        //roomMemberType의 경우 curMember 리스트를 순회하며 방장이 있는지 확인 후 설정되는 변수
        if (!chkRoomCurCnt()) {
            throw new CustomException(ErrorCode.ROOM_EXCEEDED, "방의 최대 인원은 5명 입니다.");
        }
        RoomMemberInfo roomMemberInfo = new RoomMemberInfo(nickname, roomMemberType, location, closestVertexId);
        getCurMember().add(roomMemberInfo);
        if(checkMemberInvited(nickname)) {
            //초대 멤버 리스트에 포함되어 있을 경우
            invitedMember.remove(nickname);
        }
        if (getCurMember().size() > 1) { // getCurMember().size()가 1보다 크다는 말은 현재 방에 접속한 인원이 적어도 한 명 이상이라는 것이므로 방 삭제 시간을 RoomValue.ROOM_DELETE_CANCEL로 바꾼다.
            changeRoomDeletionTime(RoomValue.ROOM_DELETE_CANCEL);
        }
    }

    public RoomMemberInfo findMemberByNickname(String nickname) {
        //현재 방에 존재하고 있는 인원을 찾을 때
        if(curMember.isEmpty()) return null;
        for(RoomMemberInfo roomMemberInfo : getCurMember()) {
            if(roomMemberInfo.getNickname().equals(nickname)) return roomMemberInfo;
        }
        return null;
    }

    public boolean isNoOneInRoom() {
        //현재 방에 아무도 없을 때
        return getCurMember().isEmpty();
    }


    public void leaveRoomCurMember(String nickname) {
        //nickname에 맞는 회원을 현재 방에서 삭제하는 메소드
        if(curMember.isEmpty()) return;
        if(!curMember.contains(findMemberByNickname(nickname))) {
            //nickname에 맞는 회원이 현재 방에 없을 때
            return;
        }
        if(curMember.size() == 2) {
            changeRoomDeletionTime(LocalDateTime.now().plusMinutes(RoomValue.ROOM_DELETION_TIME));
        }
        curMember.remove(findMemberByNickname(nickname));
        if(!getCurMember().isEmpty()) changeOwnerName(getCurMember().get(0).getNickname());
    }

    public void leaveRoomInviteMember(String nickname) {
        //nickname에 맞는 회원을 초대 회원 리스트에서 삭제하는 메소드
        if(invitedMember.isEmpty()) return;
        if(!invitedMember.contains(nickname)) {
            //nickname에 맞는 회원이 현재 방에 없을 때
            return;
        }
        invitedMember.remove(nickname);
    }

    public RoomMemberInfo getOwner() {
        if (curMember.isEmpty()) return null;
        return findMemberByNickname(ownerName);
    }

    public String getOwnerNickname() {
        //if (curMember.isEmpty()) return null;
        return ownerName;
    }

    public void changeMemberLocation(String nickname, MemberLatLng memberLatLng) {
        RoomMemberInfo member = findMemberByNickname(nickname);
        member.changeLocation(memberLatLng);
    }

    public void changeMemberClosestVertexId(String nickname, long vertexId) {
        findMemberByNickname(nickname).setClosestVertexId(vertexId);
    }

    public boolean checkMemberInvited(String nickname) {
        return invitedMember.contains(nickname);
    }

    public boolean checkMemberCur(String nickname) {
        return curMember.contains(findMemberByNickname(nickname));
    }

    public int getCurMemberNum() {
        return curMember.size();
    }
}
