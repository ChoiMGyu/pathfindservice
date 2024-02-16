/*
 * 클래스 기능 : 장소 검색기능을 수행하는 controller
 * 최근 수정 일자 : 2024.02.13(화)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.ObjectAddress;
import com.pathfind.system.domain.Objects;
import com.pathfind.system.findPathDto.SearchPlaceVCRequest;
import com.pathfind.system.findPathDto.SearchPlaceVCResponse;
import com.pathfind.system.repository.ObjectsRepository;
import com.pathfind.system.service.ObjectsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final ObjectsService objectsService;

    private final ObjectsRepository objectsRepository;

    @GetMapping("/searchPlace")
    public SearchPlaceVCResponse search(@ModelAttribute(value = "searchRequest") @Valid SearchPlaceVCRequest request) {
        //@RequestParam은 필수로 값을 입력 받아야 한다 -> javascript 또는 thymeleaf에서 걸러 주어야 함
        //@RequestBody : 클라이언트에서 서버로 필요한 데이터를 요청하기 위해 JSON 데이터를 요청 본문에 담아서 서버로 보내면,
        //               어노테이션을 사용해 HTTP 요청 본문에 담긴 값들을 자바 객체로 변환시켜, 객체에 저장
        logger.info("장소, 구조물, 벤치를 검색: " + request.getSearchContent());
        Long result = objectsService.search(request.getSearchContent());
        if(result == -1L) {
            logger.info("입력하신 검색 내용이 존재하지 않습니다");
            return new SearchPlaceVCResponse();
        }
        Objects objects = objectsRepository.findById(result);
        ObjectAddress objectAddress = objects.getObjectAddress();
        double longitude = objects.getRoadVertex().getLongitude();
        double latitude = objects.getRoadVertex().getLatitude();
        return new SearchPlaceVCResponse(objects.getName(), objects.getDescription(), objectAddress.getAddress(), objects.getObjectType(), latitude, longitude);
    }

}
