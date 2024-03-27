/*
 * 클래스 기능 : 실시간 상대방 길 찾기 서비스(서비스2)에서 길 찾기 방에 초대된 인원의 속성을 정의한 클래스이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.findPathService2Domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomMemberInfo {
    private String nickname;
    private MemberLatLng location;
    private Long closestVertexId;
    private TransportationType transportationType;
    /**
    * 웹 소켓 세션 아이디가 존재하면 현재 방에 접속한 인원이고, null이면 현재 접속하지 않은 인원으로 판단한다.
    */
    private String webSocketSessionId;
    private LocalDateTime roomExitTime;

    public RoomMemberInfo(String nickname, MemberLatLng location, Long closestVertexId, TransportationType transportationType) {
        this.nickname = nickname;
        this.location = location;
        this.closestVertexId = closestVertexId;
        this.transportationType = transportationType;
        this.roomExitTime = RoomMemberValue.ROOM_EXIT_CANCEL;
    }

    public void leaveRoom() {
        setLocation(null);
        setClosestVertexId(null);
        setTransportationType(null);
        setWebSocketSessionId(null);
        setRoomExitTime(RoomMemberValue.ROOM_EXIT_CANCEL);
    }

    public void enterRoom(String webSocketSessionId) {
        setWebSocketSessionId(webSocketSessionId);
    }

    public void changeLocation(MemberLatLng loc) {
        if(location==null) {
            setLocation(loc);
        }
        if(location.equals(loc)) {
            if(roomExitTime.isEqual(RoomMemberValue.ROOM_EXIT_CANCEL)) {
                setRoomExitTime(LocalDateTime.now().plusMinutes(RoomMemberValue.ROOM_EXIT_TIME));
            }
        }
        else {
            setLocation(loc);
            if(!roomExitTime.isEqual(RoomMemberValue.ROOM_EXIT_CANCEL)) {
                setRoomExitTime(RoomMemberValue.ROOM_EXIT_CANCEL);
            }
        }
    }
}
