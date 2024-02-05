package com.pathfind.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FindPathResponse {
    private double distance;
    private List<ShortestPathRoute> path;
}
