/*
 * 클래스 기능 : 회원 리포지토리 인터페이스
 * 최근 수정 일자 : 2024.01.13(토)
 */
package com.pathfind.system.repository;

import com.pathfind.system.domain.Member;

import java.util.List;

public interface MemberRepository {
    public List<Member> idChk(String userId);

    public List<Member> nicknameChk(String nickname);

    public List<Member> emailChk(String email);

    public void register(Member member);

    public Member findByID(Long id); //Long id로 교체


}
