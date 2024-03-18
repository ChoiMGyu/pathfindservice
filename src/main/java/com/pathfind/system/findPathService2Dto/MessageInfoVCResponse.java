/*
 * 클래스 기능 : stomp websocket 에서 주고 받는 메시지 포멧을 정의한 dto 이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.findPathService2Dto;

import com.pathfind.system.findPathDto.ShortestPathRoute;
import lombok.Data;

import java.util.List;

@Data
public class MessageInfoVCResponse {
    public String manager;
    public boolean expired = false;
    public boolean leave = false;
    public String message = null;
    public List<List<ShortestPathRoute>> route = null;
}
