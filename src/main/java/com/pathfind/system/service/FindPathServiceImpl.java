package com.pathfind.system.service;

import com.pathfind.system.algorithm.Dijkstra;
import com.pathfind.system.algorithm.DijkstraResult;
import com.pathfind.system.algorithm.Graph;
import com.pathfind.system.domain.RoadEdge;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.domain.SidewalkEdge;
import com.pathfind.system.domain.SidewalkVertex;
import com.pathfind.system.dto.FindPathResponse;
import com.pathfind.system.dto.ShortestPathRoute;
import com.pathfind.system.repository.RoadEdgeRepository;
import com.pathfind.system.repository.RoadVertexRepository;
import com.pathfind.system.repository.SidewalkEdgeRepository;
import com.pathfind.system.repository.SidewalkVertexRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindPathServiceImpl implements FindPathService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final RoadEdgeRepository roadEdgeRepository;
    private final RoadVertexRepository roadVertexRepository;
    private final SidewalkEdgeRepository sidewalkEdgeRepository;
    private final SidewalkVertexRepository sidewalkVertexRepository;

    @Override
    public FindPathResponse findRoadPath(Long start, Long end) {
        List<RoadVertex> vertices = roadVertexRepository.findAll();
        int numVertices = vertices.size();
        logger.info("roadVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<RoadEdge> edges = roadEdgeRepository.findAll();
        logger.info("roadEdges size: {}", edges.size());
        for (RoadEdge edge : edges) {
            logger.info("edge 정보 : " + edge.getRoadVertex1() + " " + edge.getRoadVertex2() + " " + edge.getLength());
            graph.addEdge(edge.getRoadVertex1()-1, edge.getRoadVertex2()-1, edge.getLength());
        }

        logger.info("findPathInfo(출발점과 도착점 사이 경로의 거리, 거쳐가야 하는 경로의 id) {} to {}", start.intValue(), end.intValue());
        DijkstraResult dijkstraResult = Dijkstra.shortestPath(graph, start, end);
        List<ShortestPathRoute> routeInfo = new ArrayList<>();
        for (Integer idx : dijkstraResult.getRoute()) {
            routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
        }

        return new FindPathResponse(dijkstraResult.getNodes().get(end.intValue()).getDistance(), routeInfo);
    }

    @Override
    public FindPathResponse findSidewalkPath(Long start, Long end) {
        List<SidewalkVertex> vertices = sidewalkVertexRepository.findAll();
        int numVertices = vertices.size();
        logger.info("sidewalkVertices size: {}", numVertices);
        Graph graph = new Graph(numVertices);

        List<SidewalkEdge> edges = sidewalkEdgeRepository.findAll();
        logger.info("sidewalkEdges size: {}", edges.size());
        for (SidewalkEdge edge : edges) {
            logger.info("edge 정보 : " + edge.getSidewalkVertex1() + " " + edge.getSidewalkVertex2() + " " + edge.getLength());
            graph.addEdge(edge.getSidewalkVertex1()-1, edge.getSidewalkVertex2()-1, edge.getLength());
        }

        logger.info("findPathInfo(출발점과 도착점 사이 경로의 거리, 거쳐가야 하는 경로의 id) {} to {}", start.intValue(), end.intValue());
        DijkstraResult dijkstraResult = Dijkstra.shortestPath(graph, start, end);
        List<ShortestPathRoute> routeInfo = new ArrayList<>();
        for (Integer idx : dijkstraResult.getRoute()) {
            routeInfo.add(new ShortestPathRoute(idx.longValue(), vertices.get(idx).getLatitude(), vertices.get(idx).getLongitude()));
        }

        return new FindPathResponse(dijkstraResult.getNodes().get(end.intValue()).getDistance(), routeInfo);
    }
}
