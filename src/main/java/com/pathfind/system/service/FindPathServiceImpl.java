package com.pathfind.system.service;

import com.pathfind.system.algorithm.Edge;
import com.pathfind.system.domain.RoadEdge;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.repository.FindPathRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindPathServiceImpl implements FindPathService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final FindPathRepository findPathRepository;

    @Override
    public List<Edge> findEdgeAll() {
        List<RoadEdge> edgeList = findPathRepository.findRoadEdgeAll();
        List<Edge> edges = new LinkedList<>();
        for (RoadEdge roadEdge : edgeList) {
            RoadVertex start = roadEdge.getRoadVertex1();
            RoadVertex end = roadEdge.getRoadVertex2();
            edges.add(new Edge(start.getId() - 1, end.getId() - 1, (double) roadEdge.getLength()));
        }
        return edges;
    }
}
