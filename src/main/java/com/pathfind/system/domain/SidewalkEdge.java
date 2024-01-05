/*
 * 클래스 기능 : 도보 간선 정보 엔티티
 * 최근 수정 일자 : 2024.01.05(금)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class SidewalkEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sidewalk_edge_id")
    private Long id; // SidewalkEdge 테이블 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sidewalk_vertex_id1", nullable = false)
    private SidewalkVertex sidewalkVertex1; // 보도 정점1(시작)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sidewalk_vertex_id2", nullable = false)
    private SidewalkVertex sidewalkVertex2; // 도보 정점2(끝)

    @Column(nullable = false)
    private double length; // 간선 길이
}
