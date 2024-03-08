package com.pathfind.system.service;

import com.pathfind.system.findPathDto.FindPathCSResponse;

public interface FindPathService {

    public FindPathCSResponse findSidewalkRoute(Long start, Long end);

    public FindPathCSResponse findRoadRoute(Long start, Long end);
}
