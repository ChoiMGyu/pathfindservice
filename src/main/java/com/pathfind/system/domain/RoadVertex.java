package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RoadVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "road_vertex_id")
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "is_destination")
    private Object object;

    /*@OneToMany(mappedBy = "roadVertex1", cascade = CascadeType.ALL)
    private List<RoadEdge> roadEdges = new ArrayList<>();*/
}
