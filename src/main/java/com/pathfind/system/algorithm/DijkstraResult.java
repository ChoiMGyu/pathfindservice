package com.pathfind.system.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class DijkstraResult {
    private List<Integer> path;
    private List<Node> nodes;
}
