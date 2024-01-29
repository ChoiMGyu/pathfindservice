package com.pathfind.system.repository;

import com.pathfind.system.domain.Objects;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ObjectsRepositoryImpl implements ObjectsRepository {

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
        return em.createQuery("select o from Objects o" +
                " join fetch o.objectAddress" +
                " where o.name like :name", Objects.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<Objects> findByAddress(String address) {
        return em.createQuery("select o from Objects o" +
                " join fetch o.objectAddress oa" +
                " where oa.address = :address", Objects.class)
                .setParameter("address", address)
                .getResultList();
    }
}
