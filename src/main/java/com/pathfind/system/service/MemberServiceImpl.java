/*
 * 클래스 기능 : 회원 서비스 클래스
 * 최근 수정 일자 : 2024.01.22(월)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Member;
import com.pathfind.system.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailSendService mailSendService;

    @Override
    public List<Member> findByUserID(Member member) {
        return memberRepository.findByUserID(member.getUserId());
    }

    @Override
    public List<Member> findByNickname(Member member) {
        return memberRepository.findByNickname(member.getNickname());
    }

    @Override
    public List<Member> findByEmail(Member member) {
        return memberRepository.findByEmail(member.getEmail());
    }

    @Override
    @Transactional
    public Long register(Member member) {
        memberRepository.register(member);
        return member.getId();
    }

    @Override
    @Transactional
    public int updatePassword(Long id, String oldPassword, String newPassword1, String newPassword2) {
        Member findMember = memberRepository.findByID(id);
        //패스워드 변경을 하는 시점에는 로그인되어 있는 상태이므로 findMember는 무조건 값을 가짐
        if (findMember.getPassword().equals(oldPassword)) {
            if (newPassword1.equals(newPassword2)) {
                findMember.changePassword(newPassword1);
                return 1;
            } else {
                //throw new IllegalStateException("새 비밀번호 확인이 올바르지 않습니다.");
                return -1;
            }
        } else {
            //throw new IllegalStateException("기존 비밀번호가 일치하지 않습니다.");
            return 0;
        }
    }

    @Override
    @Transactional
    public void recoverMember(Long id) {
        Member findMember = memberRepository.findByID(id);
        findMember.getCheck().changeDormant(false);
    }

    @Override
    public Member login(String userId, String password) {
        List<Member> result = memberRepository.login(userId, password);

        if (result.isEmpty())
        {
            //throw new IllegalStateException("아이디 혹은 비밀번호가 틀렸습니다.");
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<String> findUserIdByEmail(String email) {
        return memberRepository.findUserIdByEmail(email);
    }

    @Override
    public boolean idEmailChk(String userId, String email) {
        List<Member> findByUserID = memberRepository.findByUserID(userId);
        List<Member> findByEmail = memberRepository.findByEmail(email);

        return !findByUserID.isEmpty() && !findByEmail.isEmpty() && findByUserID.get(0).equals(findByEmail.get(0));
            //throw new IllegalStateException("회원 정보가 일치하지 않습니다.");
    }

    @Override
    @Transactional
    public void findPassword(String userId, String email) {
        String temporaryPassword = updateToTemporaryPassword(userId);
        mailSendService.findPasswordEmail(email, temporaryPassword);
    }

    private String updateToTemporaryPassword(String userId) {
        Member result = memberRepository.findByUserID(userId).get(0);
        return result.updateToTemporaryPassword();
    }

    @Override
    @Transactional
    public Optional<Member> updateNickname(Long id, String nickname) {
        Member result = memberRepository.findByID(id);
        List<Member> isDuplicated = memberRepository.findByNickname(nickname);
        if(!isDuplicated.isEmpty()) return Optional.empty();
        result.changeNickname(nickname);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Member updateEmail(Long id, String email) {
        Member result = memberRepository.findByID(id);
        result.changeEmail(email);
        return result;
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member result = memberRepository.findByID(id);
        memberRepository.deleteMember(result);
    }
}
