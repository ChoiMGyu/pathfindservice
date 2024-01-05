/*
 * 클래스 기능 : 건물, 랜드마크 등의 정보를 담는 오브젝트 엔티티
 * 최근 수정 일자 : 2024.01.05(금)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "object_id")
    private Long id; // Object 테이블 아이디

    @Column(length = 45, nullable = false)
    private String name; // 이름

    @Column(length = 1000)
    private String description; //  대상의 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjType objectType; // 대상의 종류(건물, 랜드마크, 벤치 등등)

    /*@OneToOne(fetch = FetchType.LAZY, mappedBy = "object", cascade = CascadeType.ALL)
    private ObjectAddress objectAddress;*/

}
