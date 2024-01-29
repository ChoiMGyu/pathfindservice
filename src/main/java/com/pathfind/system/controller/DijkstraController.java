package com.pathfind.system.controller;

import com.pathfind.system.algorithm.*;
import com.pathfind.system.repository.FindPathRepository;
import com.pathfind.system.service.FindPathService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DijkstraController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final FindPathRepository findPathRepository;

    private final FindPathService findPathService;

    @PostMapping("/shortest-path")
    public List<Node> shortestPath(@RequestBody GraphRequest request) {
        int numVertices = findPathRepository.getNumVertex().intValue();
        Graph graph = new Graph(numVertices);
        List<Edge> edges = findPathService.findEdgeAll();
        for (Edge edge : edges) {
            logger.info("edge 정보 : " + edge.getStart() + " " + edge.getEnd() + " " + edge.getWeight());
            graph.addEdge(edge.getStart(), edge.getEnd(), edge.getWeight());
        }
        Dijkstra algorithm = new Dijkstra();
        logger.info("다익스트라 알고리즘 실행");
        return algorithm.shortestPath(graph, request.getStart());
    }
}
