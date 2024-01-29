package com.pathfind.system.dto;

import com.pathfind.system.algorithm.Node;
import com.pathfind.system.domain.RoadVertex;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShortestPathResponse {
    private List<Node> nodes;
    private List<ShortestPathRoute> path;
}
