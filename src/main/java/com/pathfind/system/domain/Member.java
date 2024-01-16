/*
 * 클래스 기능 : 회원정보 엔티티
 * 최근 수정 일자 : 2024.01.15(월)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; //Member 테이블 PK

    @Column(length = 12, nullable = false, unique = true)
    private String userId; //아이디

    @Column(length = 20, nullable = false)
    private String password; //비밀번호 // 변경 가능

    @Column(length = 12, nullable = false, unique = true)
    private String nickname; //닉네임 // 변경 가능

    @Column(length = 45, nullable = false, unique = true)
    private String email; //이메일

    @Column(nullable = false)
    private LocalDate joinDate; //가입일자 // 변경 가능

    @Column(nullable = false)
    private LocalDate lastConnect; //최근접속일 // 변경 가능

    @OneToOne(fetch = FetchType.LAZY) //OneToOne은 기본 Eager
    @JoinColumn(name = "check_id", unique = true)
    private Check check; //일대일 연관관계 매핑

    //==정적 팩토리 메서드==//
    public static Member createMember(String userId, String password, String nickname, String email, Check check) {
        Member member = new Member();
        member.initialUserId(userId);
        member.changePassword(password);
        member.changeNickname(nickname);
        member.initialEmail(email);
        member.changeJoinDate(LocalDate.now());
        member.changeLastConnect(LocalDate.now());
        member.initialCheck(check);

        return member;
    }

    //==setter 방지 메서드==//
    private void initialUserId(String userId) {
        this.userId = userId;
    }

    public void initialEmail(String email) {
        this.email = email;
    }

    private void initialCheck(Check check) {
        this.check = check;
    }

    //==비지니스 로직==//
    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeJoinDate(LocalDate date) {
        this.joinDate = date;
    }

    public void changeLastConnect(LocalDate date) {
        this.lastConnect = date;
    }

    public String updateToTemporaryPassword() {
        String temporaryPassword = getRandomPassword();
        this.changePassword(temporaryPassword);
        return temporaryPassword;
    }

    private String getRandomPassword() {
        StringBuilder randomPassword = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            int nextType = (int) (Math.random() * 3);
            if (nextType == 0) randomPassword.append((char) (48 + (int) (Math.random() * 10)));
            else if (nextType == 1) randomPassword.append((char) (65 + (int) (Math.random() * 26)));
            else randomPassword.append((char) (97 + (int) (Math.random() * 26)));
        }
        return randomPassword.toString();
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
