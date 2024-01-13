/*
 * 클래스 기능 : 회원 서비스 인터페이스
 * 최근 수정 일자 : 2024.01.13(토)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Member;

import java.util.List;

public interface MemberService {
    public List<Member> idChk(Member member);

    public List<Member> nicknameChk(Member member);

    public List<Member> emailChk(Member member);

    public Long register(Member member);

    public void validateDuplicateInfo(Member member);

    public void updatePassword(Long id, String oldPassword, String newPassword);

    public void recoverMember(Long id);
}
