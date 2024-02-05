package com.pathfind.system.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DijkstraResult {
    private List<Node> nodes;
    private List<Integer> route;
}
