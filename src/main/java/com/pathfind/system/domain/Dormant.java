/*
 * 클래스 기능 : 휴먼계정정보 엔티티
 * 최근 수정 일자 : 2024.01.03(수)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Dormant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dormant_id")
    private Long id; //휴먼계정테이블 PK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDate dormantStartDate; //휴먼계정 시작일 -> member.lastConnect와 연관

    @Column(nullable = false)
    private LocalDate expireDate; //휴먼계정 만료일자 -> member.joinDate와 연관

}
