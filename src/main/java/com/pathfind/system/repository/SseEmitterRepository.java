/*
 * 클래스 기능 : SseEmitter 리포지토리 인터페이스
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;


public interface SseEmitterRepository {

    public SseEmitter saveSseEmitter(String sseEmitterId, SseEmitter sseEmitter);

    public boolean checkEmitterById(String sseEmitterId);

    public SseEmitter findEmitterById(String sseEmitterId);

    public List<Map.Entry<String, SseEmitter>> findAllEmitterByNickname(String nickname);

    public void deleteEmitterById(String sseEmitterId);
}
