package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TransportationSpeedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transportation_speed_info_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int speed;
}
