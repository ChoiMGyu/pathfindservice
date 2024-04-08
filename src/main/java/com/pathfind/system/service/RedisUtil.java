/*
 * 클래스 기능 : redis DB로의 삽입, 삭제 등을 구현한 클래스
 * 최근 수정 일자 : 2024.04.04(수)
 */
package com.pathfind.system.service;

import com.pathfind.system.findPathService2Domain.RoomValue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;//Redis에 접근하기 위한 Spring의 Redis 템플릿 클래스

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    public ValueOperations<String, String> getAllData() { // 모든 데이터를 Redis에서 가져오는 메서드
        //logger.info("RedisUtil get all data");
        return redisTemplate.opsForValue();
    }

    public String getData(String key) {//지정된 키(key)에 해당하는 데이터를 Redis에서 가져오는 메서드
        //logger.info("RedisUtil getData - key : " + key);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //logger.info("RedisUtil getData return : " + valueOperations.get(key));
        return valueOperations.get(key);
    }

    public void setData(String key, String value) {//지정된 키(key)에 값을 저장하는 메서드
        //logger.info("RedisUtil setData - key : " + key + " value : " + value);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataExpire(String key, String value, long duration) {//지정된 키(key)에 값을 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public ListOperations<String, String> getAllDataList() { // 모든 redis list 데이터를 Redis에서 가져오는 메서드
        logger.info("RedisUtil get all data");
        return redisTemplate.opsForList();
    }

    public String getDataList(String key) { // 지정된 키(key)에 해당하는 redis list 데이터를 Redis에서 가져오는 메서드
        logger.info("RedisUtil get redis list Data - key: " + key);
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        String data = null;
        try {
            data = listOperations.index(key, 0);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
        logger.info("RedisUtil get redis list Data: " + data);
        return data;
    }

    public void setDataList(String key, String value) {//지정된 키(key)에 값을 redis list 형태로 저장하는 메서드
        logger.info("RedisUtil setData - key : " + key + " value : " + value);
        Long expire = redisTemplate.getExpire(key);
        while (expire == null) {
            expire = redisTemplate.getExpire(key);
        }
        logger.info("expire: {}", expire);
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        if(expire == -2 && key.length() != RoomValue.ROOM_ID_LENGTH) {
            while (listOperations.rightPush(key, value) == null);
        }
        else {
            try {
                listOperations.set(key, 0, value);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    public void setDataExpireList(String key, String value, long duration) {//지정된 키(key)에 값을 redis list 형태로 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        Duration expireDuration = Duration.ofSeconds(duration);
        while (listOperations.rightPush(key, value) == null) ;
        redisTemplate.expire(key, expireDuration);
    }

    public void deleteData(String key) {//지정된 키(key)에 해당하는 데이터를 Redis에서 삭제하는 메서드
        redisTemplate.delete(key);
    }

}
