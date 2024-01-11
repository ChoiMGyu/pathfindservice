/*
 * 클래스 기능 : 회원 서비스 테스트
 * 최근 수정 일자 : 2024.01.09(화)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Member;
import com.pathfind.system.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 회원_등록() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);

        //when
        Long result = memberService.register(member);

        //then
        Assert.assertEquals(member, memberRepository.idChk("userID1").get(0));
    }

    @Test(expected = IllegalStateException.class)
    public void 아이디_중복_확인_예외() throws Exception {
        //given
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        Member member2 = Member.createMember("userID1", "5678", "userB", "bye@hello.net", null);

        //when
        try {
            memberService.register(member1);
            //em.flush();
            memberService.validateDuplicateInfo(member2);
        } catch (IllegalStateException e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }

    @Test(expected = IllegalStateException.class)
    public void 닉네임_중복_확인_예외() throws Exception {
        //given
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        Member member2 = Member.createMember("userID2", "5678", "userA", "bye@hello.net", null);


        //when
        try {
            memberService.register(member1);
            memberService.validateDuplicateInfo(member2);
        } catch (IllegalStateException e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }

    @Test(expected = IllegalStateException.class)
    public void 이메일_중복_확인_예외() throws Exception {
        //given
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        Member member2 = Member.createMember("userID2", "5678", "userB", "hello@hello.net", null);

        //when
        try {
            memberService.register(member1);
            memberService.validateDuplicateInfo(member2);
        } catch (IllegalStateException e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }
}