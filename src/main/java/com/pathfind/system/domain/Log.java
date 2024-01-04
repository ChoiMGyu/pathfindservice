/*
 * 클래스 기능 : 로그정보 엔티티
 * 최근 수정 일자 : 2024.01.03(수)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id; //로그테이블 PK

    @ManyToOne(fetch = FetchType.LAZY) //ManyToOne은 기본 Eager
    @JoinColumn(name = "member_id")
    private Member member; //다대일 연관관계 매핑

    @Column(nullable = false)
    private LocalDateTime logTime; //로그 기록 시간

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private LogLevel logLevel; //로그 레벨

    @Column(length = 100, nullable = false)
    private String content; //로그 내용

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType; //로그를 발생시킨 디바이스 종류
}
