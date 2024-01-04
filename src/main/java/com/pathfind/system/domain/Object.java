package com.pathfind.system.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "object_id")
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjType objectType;

    /*@OneToOne(fetch = FetchType.LAZY, mappedBy = "object", cascade = CascadeType.ALL)
    private ObjectAddress objectAddress;*/

}
