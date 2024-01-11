/*
 * 클래스 기능 : 이메일 서비스 인터페이스
 * 최근 수정 일자 : 2024.01.09(화)
 */
package com.pathfind.system.service;

public interface MailSendService {

    public void makeRandomNumber();

    public String joinEmail(String email);

    public void mailSend(String setFrom, String toMail, String title, String content);

    public boolean CheckAuthNum(String email,String authNum);
}
