/*
 * 클래스 기능 : 사용자에게 메일을 보내기 위한 컨트롤러
 * 최근 수정 일자 : 2024.01.09(화)
 */
package com.pathfind.system.controller;

import com.pathfind.system.dto.EmailCheckDto;
import com.pathfind.system.dto.EmailRequestDto;
import com.pathfind.system.service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);
    //로그를 info 레벨로 출력

    private final MailSendService mailService;

    @PostMapping("/mailSend")
    public String mailSend(@RequestBody @Valid EmailRequestDto emailDto){
        System.out.println("이메일 인증 이메일 :"+emailDto.getEmail());
        return mailService.joinEmail(emailDto.getEmail());
    }

    @PostMapping("/mailauthCheck")
    public String AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        logger.info("이메일 인증 확인 : " + emailCheckDto.getEmail() + " " + emailCheckDto.getAuthNum());
        Boolean Checked = mailService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return "ok";
        }
        else{
            throw new NullPointerException("뭔가 잘못!");
        }
    }
}
