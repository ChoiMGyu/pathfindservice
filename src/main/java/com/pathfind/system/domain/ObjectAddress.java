/*
 * 클래스 기능 : 건물주소정보 엔티티
 * 최근 수정 일자 : 2024.01.05(금)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ObjectAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id; // ObjectAddress 테이블 아이디

    @Column(nullable = false)
    private String address; // 주소

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", nullable = false)
    private Objects object; // 일대일 연관관계 매핑
}
