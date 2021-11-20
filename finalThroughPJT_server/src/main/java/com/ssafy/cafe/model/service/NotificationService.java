package com.ssafy.cafe.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import com.ssafy.cafe.model.dto.Notification;


public interface NotificationService {
   /**
     * Notification을 추가한다.
     * @param notification
     */
    void addNotification(Notification notification);
    
    /**
     * id에 해당하는 Notification을 삭제한다.
     * @param id
     */
    void removeNotification(Integer id);

    /**
     * id에 해당하는 notification을 반환한다.
     * @param userId
     * @return
     */
    Notification selectNotification(Integer id);
    
   
    /**
     * userId에 해당하는 Notification의 목록을 Notification id의 내림차순으로 반환한다.
     * @param userId
     * @return
     */
    List<Notification> selectByUser(String userId);
    
    
//    /**
//     * userId 해당하는 Notification에서 category별 목록을 Notification id의 내림차순으로 반환한다.
//     * @param userId, category
//     * @return
//     */
//    List<Notification> selectByUserCategory(String userId, String category);
    List<Notification> selectByUserAndCategory(HashMap<String, Object> map);

//    ResponseEntity<String> send(HttpEntity<String> entity); 
}