package com.pathfind.system.findPathService2Dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPathRoom {
    private String roomId;
    private String roomName;
    private List<String> memberNickname;
    private List<MemberLatLng> memberLocation;
    private List<Long> closestVertexId;
    private List<Boolean> isRoad;

    public static FindPathRoom createFindPathRoom(String roomName) {
        FindPathRoom newRoom = new FindPathRoom();
        newRoom.createRoomId();
        newRoom.changeRoomName(roomName);
        newRoom.memberNickname = new ArrayList<>();
        newRoom.memberLocation = new ArrayList<>();
        newRoom.closestVertexId = new ArrayList<>();
        newRoom.isRoad = new ArrayList<>();
        return newRoom;
    }

    private void changeRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void createRoomId() {
        StringBuilder randomPassword;
        randomPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int nextType = (int) (Math.random() * 3);
            if (nextType == 0) randomPassword.append((char) (48 + (int) (Math.random() * 10)));
            else if (nextType == 1) randomPassword.append((char) (65 + (int) (Math.random() * 26)));
            else randomPassword.append((char) (97 + (int) (Math.random() * 26)));
        }
        this.roomId =  randomPassword.toString();
    }

    public void pushNewMember(String newMember, MemberLatLng memberLatLng, Long vertexId, Boolean flag) {
        memberNickname.add(newMember);
        memberLocation.add(memberLatLng);
        closestVertexId.add(vertexId);
        isRoad.add(flag);
    }

    public void changeMemberLocation(int memberIdx, MemberLatLng memberLatLng, Long vertexId) {
        memberLocation.set(memberIdx, memberLatLng);
        closestVertexId.set(memberIdx, vertexId);
    }
}
