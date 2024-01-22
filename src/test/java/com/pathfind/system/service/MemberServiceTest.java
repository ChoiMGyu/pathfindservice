/*
 * 클래스 기능 : 회원 서비스 테스트
 * 최근 수정 일자 : 2024.01.09(화)
 */
package com.pathfind.system.service;

import com.pathfind.system.domain.Check;
import com.pathfind.system.domain.Member;
import com.pathfind.system.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());

        //when
        Long result = memberService.register(member);

        //then
        Assert.assertEquals(member, memberRepository.findByUserID("userID1").get(0));
    }

    /*@Test(expected = IllegalStateException.class)
    public void 아이디_중복_확인_예외() throws Exception {
        //given
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());
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
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());
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
        Member member1 = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());
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
    }*/

    @Test
    public void 비밀번호_변경() throws Exception
    {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());
        String oldPassword = "1234";
        String newPassword = "5678";

        //when
        memberRepository.register(member);
        memberService.updatePassword(member.getId(), oldPassword, newPassword, newPassword);

        //then
        Assert.assertEquals(member.getPassword(), "5678");
    }

    @Test(expected = IllegalStateException.class)
    public void 옛비밀번호불일치_변경() throws Exception
    {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", Check.createCheck());
        String oldPassword = "1111";
        String newPassword = "5678";

        //when
        try {
            memberRepository.register(member);
            memberService.updatePassword(member.getId(), oldPassword, newPassword, newPassword);
        } catch (IllegalStateException e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }

    @Test
    public void 휴면계정복구() throws Exception
    {
        //given
        Check check = Check.createCheck();
        check.changeEmailAuth(true);
        check.changeDormant(true);
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", check);

        //when
        em.persist(check);
        em.persist(member);
        memberService.recoverMember(member.getId());

        //then
        Assert.assertFalse(member.getCheck().isDormant());
    }

    /*@Test(expected = IllegalStateException.class)
    public void 이메일인증X_휴면계정복구() throws Exception
    {
        //given
        Check check = Check.createCheck();
        check.changeEmailAuth(false);
        check.changeDormant(true);
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", check);

        //when
        try {
            em.persist(check);
            em.persist(member);
            memberService.recoverMember(member.getId());
        } catch (IllegalStateException e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }*/

    @Test
    public void 로그인_성공() throws Exception {
        //given
        Check check = Check.createCheck();
        em.persist(check);
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", check);
        String id = member.getUserId(), password = member.getPassword();
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Member result = memberService.login(id, password);

        //then
        Assert.assertTrue(member.equals(result));
    }

    @Test(expected = IllegalStateException.class)
    public void 로그인_실패() throws Exception {
        //given
        Check check = Check.createCheck();
        em.persist(check);
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", check);
        String id = "fakeUser", password = "fakePassword";
        em.persist(member);
        em.flush();
        em.clear();

        //when
        try {
            Member result = memberService.login(id, password);
        } catch (Exception e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }

    /*@Test
    public void 이메일로_아이디_찾기_성공() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        String id = member.getUserId(), email = member.getEmail();
        em.persist(member);
        em.flush();
        em.clear();

        //when
        String result = memberService.findUserIdByEmail(email);

        //then
        Assert.assertEquals(id, result);
    }

    @Test(expected = IllegalStateException.class)
    public void 이메일로_아이디_찾기_실패() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        String id = "fakeUser", email = "fakeEmail@hello.net";
        em.persist(member);
        em.flush();
        em.clear();

        //when
        try {
            String result = memberService.findUserIdByEmail(email);
        } catch (Exception e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }*/

    @Test
    public void 아이디_이메일_일치여부_확인_성공() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        String id = member.getUserId(), email = member.getEmail();
        em.persist(member);
        em.flush();
        em.clear();

        //when
        memberService.idEmailChk(id, email);

        //then
        // 예외가 발생하지 않아야 한다.
    }

    @Test(expected = IllegalStateException.class)
    public void 아이디_이메일_일치여부_확인_실패() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        String id = "fakeUser", email = "fakeEmail@hello.net";
        em.persist(member);
        em.flush();
        em.clear();

        //when
        try {
            memberService.idEmailChk(id, email);
        } catch (Exception e) {
            System.out.println("===============================");
            System.out.println(e.getMessage());
            System.out.println("===============================");
            throw e;
        }

        //then
        fail("예외가 발생해야 한다.");
    }
    @Test
    public void 임시_비밀번호_발급및저장_AND_임시_비밀번호_이메일_전송() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "imsiyujeo99@gmail.com", null);
        String id = member.getUserId(), email = member.getEmail(), password = member.getPassword();
        em.persist(member);
        em.flush();
        em.clear();

        //when
        memberService.findPassword(id, email);

        //then
        Assert.assertNotEquals(password, memberService.findByUserID(member).get(0).getPassword());
/*        System.out.println("==========================");
        System.out.println(memberService.findByUserID(member).get(0).getPassword());
        System.out.println("==========================");*/
    }

}