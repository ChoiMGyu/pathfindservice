/*
 * 클래스 기능 : 뷰에서 컨트롤러로의 데이터 전달을 위한 DTO
 * 최근 수정 일자 : 2024.02.05(월)
 */
package com.pathfind.system.findPathService2Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageVCRequest {
    private String roomId;
    private String sender;
    private String message;
}
