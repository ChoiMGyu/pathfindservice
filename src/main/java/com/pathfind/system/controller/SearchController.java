/*
 * 클래스 기능 : 장소 검색기능을 수행하는 controller
 * 최근 수정 일자 : 2024.02.12(월)
 */
package com.pathfind.system.controller;

import com.pathfind.system.domain.ObjectAddress;
import com.pathfind.system.domain.Objects;
import com.pathfind.system.findPathDto.SearchPlaceVCResponse;
import com.pathfind.system.repository.ObjectsRepository;
import com.pathfind.system.service.ObjectsService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final ObjectsService objectsService;

    private final ObjectsRepository objectsRepository;

    @GetMapping("/searchPlace")
    public SearchPlaceVCResponse search(@RequestParam("query") @NotEmpty String query) {
        //@RequestParam은 필수로 값을 입력 받아야 한다 -> javascript 또는 thymeleaf에서 걸러 주어야 함
        //@RequestBody : 클라이언트에서 서버로 필요한 데이터를 요청하기 위해 JSON 데이터를 요청 본문에 담아서 서버로 보내면,
        //               어노테이션을 사용해 HTTP 요청 본문에 담긴 값들을 자바 객체로 변환시켜, 객체에 저장
        logger.info("장소, 구조물, 벤치를 검색: " + query);
        Long result = objectsService.search(query);
        if(result == -1L) {
            return new SearchPlaceVCResponse();
        }
        Objects byId = objectsRepository.findById(result);
        ObjectAddress objectAddress = byId.getObjectAddress();
        return new SearchPlaceVCResponse(byId.getName(), byId.getDescription(), objectAddress.getAddress(), byId.getObjectType());
    }

}
