package com.pathfind.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortestPathRoute {
    private Long id;
    private double Latitude;
    private double Longitude;
}
