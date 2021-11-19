package com.ssafy.cafe.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Notification;
import com.ssafy.cafe.model.service.NotificationService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/notification")
@CrossOrigin("*")
public class NotificationRestController {
	private static final Logger logger = LoggerFactory.getLogger(NotificationRestController.class);
    @Autowired
    NotificationService nService;
    

    
    @PostMapping
    @Transactional
    @ApiOperation(value="notification 객체를 추가한다.", response = Boolean.class)
    public Boolean insert(@RequestBody Notification notification  ) {
        nService.addNotification(notification);
        return true;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ApiOperation(value="{id}에 해당하는 notification 정보를 삭제한다.", response = Boolean.class)
    public Boolean delete(@PathVariable Integer id) {
        nService.removeNotification(id);
        return true;
    }
    
    
    @GetMapping("/{userId}")
    @ApiOperation(value="{userId}에 해당하는 notification의 정보를 반환한다."
            + "이 기능은 사용자의 notification를 조회할 때 사용된다.", response = List.class)
    public List<Notification> getUserWithNotifications(@PathVariable String userId){
        return nService.selectByUser(userId);
    }
    
    
    @GetMapping("/{userId}/{category}")
    @ApiOperation(value="{userId}에 해당하는 noti의 category별 정보를 반환한다."
            + "이 기능은 사용자의 notification을 카테고리별로 조회할 때 사용된다.", response = List.class)
    public List<Notification> getUserWithNotificationsAndNoti(@PathVariable String userId, @PathVariable String category){
       HashMap<String, Object> map = new HashMap<String, Object>();
       map.put("userId", userId);
       map.put("category", category);
       
       return nService.selectByUserAndCategory(map);
    }
    
}