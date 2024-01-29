package com.pathfind.system.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Graph {
    private int V;
    @Getter
    private List<List<Node>> adjList;

    public Graph(int V) {
        this.V = V;
        adjList = new ArrayList<>();
        for(int i = 0; i < V; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(Long u, Long v, double weight) {
        adjList.get(u.intValue()).add(new Node(v.intValue(), weight));
    }

    public int getNumVertices() {
        return V;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Node {
        int v;
        double weight;
    }
}
