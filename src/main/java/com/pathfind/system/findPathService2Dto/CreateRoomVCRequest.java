/*
 * 클래스 기능 : 방 생성 요청 시 사용되는 dto이다.
 * 최근 수정 일자 : 2024.03.18(월)
 */
package com.pathfind.system.findPathService2Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateRoomVCRequest {
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,30}$", message = "방 이름은 특수문자를 제외한 2~30자리여야 합니다.")
    public String roomName;

    @NotBlank
    public String transportation;
}
