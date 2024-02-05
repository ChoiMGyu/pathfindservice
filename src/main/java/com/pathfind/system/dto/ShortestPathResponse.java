package com.pathfind.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShortestPathResponse {
    private double distance;
    private int speed;
    private List<ShortestPathRoute> path;
}
