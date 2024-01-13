/*
 * 클래스 기능 : 회원 리포지토리 테스트
 * 최근 수정 일자 : 2024.01.09(화)
 */
package com.pathfind.system.repository;

import com.pathfind.system.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 등록_확인() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);

        //when
        memberRepository.register(member);
        Long id = member.getId();
        Member result = em.createQuery("select m from Member m where id = :id", Member.class)
                .setParameter("id", id)
                .getSingleResult();

        //then
        Assert.assertEquals(member, result);
    }

    @Test
    public void 아이디_확인() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        em.persist(member);

        //when
        Member findMember = em.find(Member.class, member.getId());

        //then
        Assert.assertEquals(member, findMember);
    }

    @Test
    public void 닉네임_확인() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        em.persist(member);

        //when
        List<Member> result = memberRepository.findByNickname(member.getNickname());


        //then
        Assert.assertEquals(member, result.get(0));
    }

    @Test
    public void 이메일_확인() throws Exception {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);
        em.persist(member);

        //when
        List<Member> result = memberRepository.findByEmail(member.getEmail());

        //then
        Assert.assertEquals(member, result.get(0));
    }

    @Test
    public void 아이디_회원찾기() throws Exception
    {
        //given
        Member member = Member.createMember("userID1", "1234", "userA", "hello@hello.net", null);

        //when
        memberRepository.register(member);
        Member findMember = memberRepository.findByID(member.getId());

        //then
        Assert.assertEquals(findMember, member);
    }

}