/*
 * 클래스 기능 : 동의 여부 체크 엔티티
 * 최근 수정 일자 : 2024.01.03(수)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "checks")
public class Check {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long id; //Check 테이블 PK

    @OneToOne(fetch = FetchType.LAZY) //OneToOne은 기본 Eager
    @JoinColumn(name = "member_id")
    private Member member; //일대일 연관관계 매핑

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean emailAuth; //이메일 인증

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean informationAgree; //개인정보 수집동의

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean dormant; //휴먼계정 여부확인

}
