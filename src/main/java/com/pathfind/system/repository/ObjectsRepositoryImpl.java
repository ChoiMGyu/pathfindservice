/*
 * 클래스 기능 : 오브젝트 리포지토리 구현 클래스
 * 최근 수정 일자 : 2024.02.07(수)
 */
package com.pathfind.system.repository;

import com.pathfind.system.domain.Objects;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ObjectsRepositoryImpl implements ObjectsRepository {

    private static final Logger logger = LoggerFactory.getLogger(FindPathRepositoryImpl.class);

    private final EntityManager em;

    @Override
    public Objects findById(Long id) {
        return em.createQuery("select o from Objects o" +
                " join fetch o.objectAddress" +
                " where o.id = :id", Objects.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Objects> findByName(String name) {
        logger.info("name : {}", name);
        return em.createQuery("select o from Objects o" +
                        " join fetch o.objectAddress" +
                        " where TRIM(o.name) like :name", Objects.class)
                .setParameter("name", "%" + name.trim() + "%")
                .getResultList();
    }

    @Override
    public List<Objects> findByAddress(String address) {
        logger.info("address : {}", address);
        return em.createQuery("select o from Objects o" +
                " join fetch o.objectAddress oa" +
                " where TRIM(oa.address) = :address", Objects.class)
                .setParameter("address", address.trim())
                .getResultList();
    }
}
