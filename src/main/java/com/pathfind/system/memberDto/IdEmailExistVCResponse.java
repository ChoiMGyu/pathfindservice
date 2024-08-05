/*
 * 클래스 기능 : 아이디, 이메일 일치 여부를 반환하는데 쓰이는 dto 클래스
 * 최근 수정 일자 : 2024.08.04(일)
 */
package com.pathfind.system.memberDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "IdEmailExistVCResponse", description = "유저 아이디, 이메일 일치 여부 반환에 쓰이는 dto")
@Data
@AllArgsConstructor
public class IdEmailExistVCResponse {
    /*@Schema(name = "UserIdCheck", description = "유저 아이디 유효성 검증 여부", example = "true")
    private boolean UserIdCheck; // 아이디 유효성 검증 여부
    @Schema(name = "EmailCheck", description = "유저 이메일 유효성 검증 여부", example = "true")
    private boolean EmailCheck; // 이메일 유효성 검증 여부*/
    /*@Schema(name = "isSame", description = "일치 여부 확인", example = "true")
    private boolean isSame;*/
    @Schema(name = "message", description = "일치 여부에 따라 사용자에게 보여줄 메시지", example = "회원 정보 확인을 통과하였습니다.")
    private String message;
}
