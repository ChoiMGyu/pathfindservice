/*
 * 클래스 기능 : 검색 기능 인터페이스
 * 최근 수정 일자 : 2024.05.28(화)
 */
package com.pathfind.system.service;

import java.util.List;

public interface ObjectsService {

    public Long search(String searchContent);

    public List<String> findObjectsNameListBySearchWord(String searchWord);
}
