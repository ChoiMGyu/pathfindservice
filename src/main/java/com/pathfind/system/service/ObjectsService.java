/*
 * 클래스 기능 : 검색 기능 인터페이스
 * 최근 수정 일자 : 2024.05.31(금)
 */
package com.pathfind.system.service;

import com.pathfind.system.findPathDto.PageObject;
import com.pathfind.system.findPathDto.SearchObjectsPageCSResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ObjectsService {

    public Long search(String searchContent);

    public List<String> findObjectsNameListBySearchWord(String searchWord);

    public Page<PageObject> paging(String searchWord, SearchObjectsPageCSResponse searchObjectsPageCSResponse);

}
