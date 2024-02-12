package com.pathfind.system.findPathDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShortestPathVCResponse {
    private double distance;
    private int speed;
    private List<ShortestPathRoute> path;
}
