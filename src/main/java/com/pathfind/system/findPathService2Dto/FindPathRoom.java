/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2)의 길 찾기 방을 구현한 클래스이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.findPathService2Dto;

import com.pathfind.system.exception.ErrorCode;
import com.pathfind.system.exception.CustomException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPathRoom {
    private String roomId;
    private String roomName;
    private List<RoomMemberInfo> invitedMember;

    public static FindPathRoom createFindPathRoom(String roomName) {
        FindPathRoom newRoom = new FindPathRoom();
        newRoom.createRoomId();
        newRoom.changeRoomName(roomName);
        newRoom.invitedMember = new ArrayList<>(6); // 기본 사이즈가 6인 이유는 리스트의 size가 5가 되었을 때 capacity가 자동으로 늘어나는 것을 방지하기 위함이다.
        return newRoom;
    }

    private void changeRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void createRoomId() {
        StringBuilder randomPassword;
        randomPassword = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < RoomIdLength.ROOM_ID_LENGTH; i++) {
            int nextType = (int) (random.nextFloat() * 3);
            if (nextType == 0) randomPassword.append((char) (48 + random.nextInt(10)));
            else if (nextType == 1) randomPassword.append((char) (65 + random.nextInt(26)));
            else randomPassword.append((char) (97 + random.nextInt(26)));
        }
        this.roomId = randomPassword.toString();
    }

    public void pushNewMember(String newMember, MemberLatLng memberLatLng, Long vertexId, Boolean isRoad) {
        if (getInvitedMember().size() == 5) {
            throw new CustomException(ErrorCode.ROOM_EXCEEDED, "방에 초대 가능한 인원은 최대 5명 입니다.");
        }
        getInvitedMember().add(new RoomMemberInfo(newMember, memberLatLng, vertexId, isRoad));
    }

    /*public void changeMemberLocation(int memberIdx, MemberLatLng memberLatLng, Long vertexId) {
        RoomMemberInfo member = getInvitedMember().get(memberIdx);
        member.setLocation(memberLatLng);
        member.setClosestVertexId(vertexId);
    }*/

    public RoomMemberInfo findMemberByNickname(String nickname) {
        if (getInvitedMember() == null) return null;
        for (RoomMemberInfo roomMemberInfo : getInvitedMember()) {
            if (roomMemberInfo.getNickname().equals(nickname)) return roomMemberInfo;
        }
        return null;
    }

    public boolean isNoOneInRoom() {
        if (getInvitedMember() == null) return true;
        for (RoomMemberInfo roomMemberInfo : getInvitedMember()) {
            if (roomMemberInfo.getWebSocketSessionId() != null) return false;
        }
        return true;
    }

    public void enterRoom(String nickname, String webSocketSessionId) {
        int a = -1, b = -1;
        for (int i = 0; i < getInvitedMember().size(); i++) {
            if (getInvitedMember().get(i).getWebSocketSessionId() == null) {
                a = i;
                break;
            }
        }
        for (int i = 0; i < getInvitedMember().size(); i++) {
            if (getInvitedMember().get(i).getNickname().equals(nickname)) {
                getInvitedMember().get(i).enterRoom(webSocketSessionId);
                b = i;
                break;
            }
        }
        if (a != -1 && b != -1 && a < b) {
            Collections.swap(getInvitedMember(), a, b);
        }
    }

    public void leaveRoom(String nickname) {
        RoomMemberInfo leaveMember = findMemberByNickname(nickname);
        if (leaveMember != null) {
            for (int i = 0; i < getInvitedMember().size(); i++) {
                if (getInvitedMember().get(i).getNickname().equals(nickname) && i + 1 < getInvitedMember().size() && getInvitedMember().get(i + 1).getWebSocketSessionId() != null) {
                    Collections.swap(getInvitedMember(), i, i + 1);
                }
            }
            leaveMember.leaveRoom();
        }
    }

    public void leaveRoomByWebSocketSessionId(String webSocketSessionId) {
        RoomMemberInfo leaveMember = findMemberByWebSocketSessionId(webSocketSessionId);
        if (leaveMember != null) {
            for (int i = 0; i < getInvitedMember().size(); i++) {
                String sessionId = getInvitedMember().get(i).getWebSocketSessionId();

                if (sessionId != null && sessionId.equals(webSocketSessionId) && i + 1 < getInvitedMember().size() && getInvitedMember().get(i + 1).getWebSocketSessionId() != null) {
                    Collections.swap(getInvitedMember(), i, i + 1);
                }
            }
            leaveMember.leaveRoom();
        }
    }

    public RoomMemberInfo findMemberByWebSocketSessionId(String webSocketSessionId) {
        for (RoomMemberInfo roomMemberInfo : invitedMember) {
            if (roomMemberInfo.getWebSocketSessionId() != null && roomMemberInfo.getWebSocketSessionId().equals(webSocketSessionId))
                return roomMemberInfo;
        }
        return null;
    }

    /*public RoomMemberInfo setWebSocketSessionId(String nickname, String webSocketSessionId) {
        for (RoomMemberInfo roomMemberInfo : invitedMember) {
            if (roomMemberInfo.getNickname().equals(nickname)) {
                roomMemberInfo.enterRoom(webSocketSessionId);
                return roomMemberInfo;
            }
        }
        return null;
    }*/
}
