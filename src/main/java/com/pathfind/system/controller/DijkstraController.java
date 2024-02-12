package com.pathfind.system.controller;

import com.pathfind.system.findPathDto.GraphVCRequest;
import com.pathfind.system.findPathDto.FindPathCSResponse;
import com.pathfind.system.findPathDto.ShortestPathVCResponse;
import com.pathfind.system.repository.TransportationSpeedInfoRepository;
import com.pathfind.system.service.FindPathService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DijkstraController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final FindPathService findPathService;
    private final TransportationSpeedInfoRepository transportationSpeedInfoRepository;

    @GetMapping("/path")
    public ShortestPathVCResponse shortestPath(@ModelAttribute(value = "graphRequest") GraphVCRequest request) {
        int speed = transportationSpeedInfoRepository.findSpeedByName(request.getTransportation()).get(0);
        logger.info("{} 이동 수단의 속도: {}",request.getTransportation(), speed);

        FindPathCSResponse findPathCSResponse;
        if(request.getTransportation().equals("자동차")) {
            logger.info("도로 길찾기");
            findPathCSResponse = findPathService.findRoadPath(request.getStart(), request.getEnd());
        }
        else {
            logger.info("도보 길찾기");
            findPathCSResponse = findPathService.findSidewalkPath(request.getStart(), request.getEnd());
        }

        return new ShortestPathVCResponse(findPathCSResponse.getDistance(), speed, findPathCSResponse.getPath());
    }
}
