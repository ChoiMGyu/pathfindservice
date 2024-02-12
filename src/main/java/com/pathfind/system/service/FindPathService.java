package com.pathfind.system.service;

import com.pathfind.system.findPathDto.FindPathCSResponse;

public interface FindPathService {

    public FindPathCSResponse findSidewalkPath(Long start, Long end);

    public FindPathCSResponse findRoadPath(Long start, Long end);
}
