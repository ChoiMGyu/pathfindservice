package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class SidewalkVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sidewalk_vertex_id")
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "is_destination")
    private Object object;

    /*@OneToMany(mappedBy = "sidewalkVertex1", cascade = CascadeType.ALL)
    private List<SidewalkEdge> sidewalkEdges = new ArrayList<>();*/
}
