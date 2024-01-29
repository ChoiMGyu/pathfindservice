package com.pathfind.system.service;

import com.pathfind.system.algorithm.Edge;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.domain.SidewalkVertex;

import java.util.List;

public interface FindPathService {

    public List<Edge> findEdgeAll();
/*    public List<RoadVertex> findRoadPath(Long start, Long end);
    public List<SidewalkVertex> findSidewalkPath(Long start, Long end);*/
}
