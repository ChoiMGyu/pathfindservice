/*
 * 클래스 기능 : 도보 정점 정보 엔티티
 * 최근 수정 일자 : 2024.01.05(금)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class SidewalkVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sidewalk_vertex_id")
    private Long id; // SidewalkVertex 테이블 아이디

    @Column(nullable = false)
    private double latitude; // 정점의 위도

    @Column(nullable = false)
    private double longitude; // 정점의 경도

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "is_destination")
    private Object object; // 일대일 연관관계 매핑

    /*@OneToMany(mappedBy = "sidewalkVertex1", cascade = CascadeType.ALL)
    private List<SidewalkEdge> sidewalkEdges = new ArrayList<>();*/
}
