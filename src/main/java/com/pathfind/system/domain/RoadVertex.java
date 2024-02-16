/*
 * 클래스 기능 : 도로 정점 정보 엔티티
 * 최근 수정 일자 : 2024.02.13(화)
 */
package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RoadVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "road_vertex_id")
    private Long id; // RoadVertex 테이블 아이디

    @Column(nullable = false)
    private double latitude; // 정점의 위도

    @Column(nullable = false)
    private double longitude; // 정점의 경도

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "is_destination")
    private Objects object; // 일대일 연관관계 매핑

    //==연관관계 메소드==//
    public void changeObjects(Objects objects) {
        this.object = objects;
        objects.changeVertex(this);
    }

    /*@OneToMany(mappedBy = "roadVertex1", cascade = CascadeType.ALL)
    private List<RoadEdge> roadEdges = new ArrayList<>();*/
}
