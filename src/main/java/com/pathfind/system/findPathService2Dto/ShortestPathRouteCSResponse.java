package com.pathfind.system.findPathService2Dto;

import com.pathfind.system.findPathDto.VertexInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ShortestPathRouteCSResponse {
    private String memberNickname;
    private double distance;
    private List<VertexInfo> route;
}
