package com.pathfind.system.service;

import com.pathfind.system.dto.FindPathResponse;

public interface FindPathService {

    public FindPathResponse findSidewalkPath(Long start, Long end);

    public FindPathResponse findRoadPath(Long start, Long end);
}
