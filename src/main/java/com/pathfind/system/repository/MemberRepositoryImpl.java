/*
 * 클래스 기능 : 회원 리포지토리 구현 클래스
 * 최근 수정 일자 : 2024.01.13(토)
 */
package com.pathfind.system.repository;

import com.pathfind.system.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final EntityManager em;

    @Override
    public List<Member> idChk(String userId) {
        return em.createQuery("select m from Member m where m.userId = :userId", Member.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Member> nicknameChk(String nickname) {
        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }

    @Override
    public List<Member> emailChk(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
    }

    @Override
    public void register(Member member) {
        em.persist(member);
    }

    @Override
    public List<Member> findByUserID(String userId) {
        return em.createQuery("select m from Member m where m.userId = :userId", Member.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Member> login(String userId, String password) {
        return em.createQuery("select m from Member m" +
                        " join fetch Check" +
                        " where m.userId = :userId and m.password = :password", Member.class)
                .setParameter("userId", userId)
                .setParameter("password", password)
                .getResultList();
    }

    @Override
    public boolean idEmailChk(String userId, String email) {
        Long result = em.createQuery("select count(m) from Member m" +
                        " where m.userId = :userId and m.password = :email", Long.class)
                .setParameter("userId", userId)
                .setParameter("email", email)
                .setMaxResults(1)
                .getSingleResult();
        return result > 0;
    }

    @Override
    public List<String> findUserIdByEmail(String email) {
        return em.createQuery("select m.userId from Member m" +
                        " where m.email = :email", String.class)
                .setParameter("email", email)
                .getResultList();
    }
}
