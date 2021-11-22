package com.ssafy.cafe.model.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.cafe.controller.rest.TokenController;
import com.ssafy.cafe.firebase.FirebaseCloudMessageService;
import com.ssafy.cafe.model.dao.NotificationDao;
import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.Notification;

@Component
public class OrderStateChangeService implements PropertyChangeListener {
	private static final Logger logger = LoggerFactory.getLogger(OrderStateChangeService.class);

	@Autowired
	FirebaseCloudMessageService fcmService;
	
    private Integer c;

    public void propertyChange(PropertyChangeEvent evt) {
        this.setCompleted((Integer) evt.getNewValue());
        logger.info("message : {}", evt.getNewValue());

        Integer state = (Integer) evt.getNewValue();
        
        String userToken = "eZbzN9zNQT26t0TsQ6DrRG:APA91bH5-hOF3BrZi4Zo-FTvdsXoJLetMSuZ-jtyoAl3VG4BqWcQj9wuVwVLjkezNcA299AbWn4c9rOJx1E-EpFj74ujVaClojUDJGQd88d87SkBa5M81SR6ir7ESqjoa0PoXPFj2ZFs";
        
        try {
        	if(state == 0) {
        		fcmService.sendMessageTo(userToken, "Order", "주문이 완료되었습니다.");
        		} else if(state == 1) {
        		fcmService.sendMessageTo(userToken, "Order", "주문 접수가 완료되었습니다."); 
	        } else if(state == 2) {
	        	fcmService.sendMessageTo(userToken, "Order", "주문하신 음료 제조가 완료되었습니다."); 
	        } else if(state == 3) {
	        	fcmService.sendMessageTo(userToken, "Order", "픽업 완료");
	        }
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        

    }

    public Integer getCompleted() {
        return c;
    }

    public void setCompleted(Integer c) {
        this.c = c;
    }
}
