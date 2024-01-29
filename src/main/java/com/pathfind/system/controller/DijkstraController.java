package com.pathfind.system.controller;

import com.pathfind.system.algorithm.*;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.dto.ShortestPathResponse;
import com.pathfind.system.dto.ShortestPathRoute;
import com.pathfind.system.repository.FindPathRepository;
import com.pathfind.system.repository.RoadVertexRepository;
import com.pathfind.system.service.FindPathService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@RestController
@RequiredArgsConstructor
public class DijkstraController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final FindPathRepository findPathRepository;

    private final FindPathService findPathService;
    private final RoadVertexRepository roadVertexRepository;

    @PostMapping("/shortest-path")
    public ShortestPathResponse shortestPath(@RequestBody GraphRequest request) {
        //int numVertices = findPathRepository.getNumVertex().intValue();
        List<RoadVertex> vertices = roadVertexRepository.findAll();
        int numVertices = vertices.size();
        Graph graph = new Graph(numVertices);
        List<Edge> edges = findPathService.findEdgeAll();
        for (Edge edge : edges) {
            logger.info("edge 정보 : " + edge.getStart() + " " + edge.getEnd() + " " + edge.getWeight());
            graph.addEdge(edge.getStart(), edge.getEnd(), edge.getWeight());
        }
        Dijkstra algorithm = new Dijkstra();
        logger.info("다익스트라 알고리즘 실행");
        DijkstraResult dijkstraResult = algorithm.shortestPath(graph, request.getStart());
        List<Integer> result = dijkstraResult.getPath();
        List<Node> nodes = dijkstraResult.getNodes();

        //List<Integer> result = dijkstra.findPath(start.intValue(), end.intValue());
        List<ShortestPathRoute> answer = new ArrayList<>();
        Stack<Integer> path = new Stack<>();
        int endToStart = request.getEnd().intValue();
        path.push(endToStart);
        while (endToStart != request.getStart().intValue()) {
            endToStart = result.get(endToStart);
            path.push(endToStart);
        }
        while (!path.isEmpty()) {
            logger.info("path: {}", path.peek());
            RoadVertex roadVertex = vertices.get(path.pop());

            answer.add(new ShortestPathRoute(roadVertex.getId()-1, roadVertex.getLatitude(), roadVertex.getLongitude()));
        }

        return new ShortestPathResponse(nodes, answer);
    }
}
