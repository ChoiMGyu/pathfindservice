/*
 * 클래스 기능 : 회원 서비스 클래스
 * 최근 수정 일자 : 2024.01.13(토)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Member;
import com.pathfind.system.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public List<Member> idChk(Member member) {
        return memberRepository.idChk(member.getUserId());
    }

    @Override
    public List<Member> nicknameChk(Member member) {
        return memberRepository.nicknameChk(member.getNickname());
    }

    @Override
    public List<Member> emailChk(Member member) {
        return memberRepository.emailChk(member.getEmail());
    }

    @Override
    @Transactional
    public Long register(Member member) {
        memberRepository.register(member);
        return member.getId();
    }

    @Override
    public void validateDuplicateInfo(Member member) {
            if(!this.idChk(member).isEmpty())
                throw new IllegalStateException("이미 존재하는 아이디입니다.");
            if(!this.nicknameChk(member).isEmpty())
                throw new IllegalStateException("이미 존재하는 닉네임입니다.");
            if(!this.emailChk(member).isEmpty())
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Member findMember = memberRepository.findByID(id);
        //패스워드 변경을 하는 시점에는 로그인되어 있는 상태이므로 findMember는 무조건 값을 가짐
        if(findMember.getPassword().equals(oldPassword)) {
            findMember.changePassword(newPassword);
        }
        else {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void recoverMember(Long id) {
        Member findMember = memberRepository.findByID(id);
        if(findMember.getCheck().isEmailAuth()) {
            if(findMember.getCheck().isDormant()) {
                findMember.getCheck().changeDormant(false);
            }
        }
        else {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }
    }
}
