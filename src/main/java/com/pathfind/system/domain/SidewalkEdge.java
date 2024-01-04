package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class SidewalkEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sidewalk_edge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sidewalk_vertex_id1", nullable = false)
    private SidewalkVertex sidewalkVertex1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sidewalk_vertex_id2", nullable = false)
    private SidewalkVertex sidewalkVertex2;

    @Column(nullable = false)
    private double length;
}
