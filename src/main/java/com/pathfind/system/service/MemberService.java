/*
 * 클래스 기능 : 회원 서비스 인터페이스
 * 최근 수정 일자 : 2024.01.13(토)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Member;

import java.util.List;

public interface MemberService {
    public List<Member> findByUserID(Member member);

    public List<Member> findByNickname(Member member);

    public List<Member> findByEmail(Member member);

    public Long register(Member member);

    public void validateDuplicateInfo(Member member);

    public void updatePassword(Long id, String oldPassword, String newPassword1, String newPassword2);

    public Member login(String userId, String password);

    public String findUserIdByEmail(String email);

    public void idEmailChk(String userId, String email);

    public void findPassword(String userId, String email);

    public void recoverMember(Long id);

    Member updateNickname(Long id, String nickname);

    Member updateEmail(Long id, String email);

    void deleteMember(Long id);
}
