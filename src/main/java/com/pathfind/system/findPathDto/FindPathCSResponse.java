package com.pathfind.system.findPathDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FindPathCSResponse {
    private double distance;
    private List<ShortestPathRoute> path;
}
