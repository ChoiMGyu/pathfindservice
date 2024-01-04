/*
 * 클래스 기능 : 회원정보 엔티티
 * 최근 수정 일자 : 2024.01.03(수)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; //Member 테이블 PK

    @Column(length = 12, nullable = false)
    private String userId; //아이디

    @Column(length = 20, nullable = false)
    private String password; //비밀번호

    @Column(length = 12, nullable = false)
    private String nickname; //닉네임

    @Column(length = 45, nullable = false)
    private String email; //이메일

    @Column(nullable = false)
    private LocalDate joinDate; //가입일자

    @Column(nullable = false)
    private LocalDate lastConnect; //최근접속일

}
