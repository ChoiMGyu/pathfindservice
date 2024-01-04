package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RoadEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "road_edge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_vertex_id1", nullable = false)
    private RoadVertex roadVertex1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_vertex_id2", nullable = false)
    private RoadVertex roadVertex2;

    @Column(nullable = false)
    private double length;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OneWayType oneWayType;
}
