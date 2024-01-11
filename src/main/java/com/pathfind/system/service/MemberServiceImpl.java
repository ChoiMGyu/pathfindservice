/*
 * 클래스 기능 : 회원 서비스 클래스
 * 최근 수정 일자 : 2024.01.09(화)
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
}
