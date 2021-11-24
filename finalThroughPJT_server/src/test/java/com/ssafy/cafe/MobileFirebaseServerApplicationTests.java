package com.ssafy.cafe;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.cafe.firebase.FirebaseCloudMessageService;
import com.ssafy.cafe.model.dto.Notification;
import com.ssafy.cafe.model.service.NotificationService;

@SpringBootTest
class MobileFirebaseServerApplicationTests {

    @Autowired
    FirebaseCloudMessageService service;

    @Autowired
    NotificationService notiService;
    
    @Test
    void contextLoads() throws IOException {
    	
        String token = "ecrzF5g3Rrukh9fakKbS2R:APA91bFmu__z65gsHr_YN9g3JVDmFBuEMrDyJ8oYsPk2t_rC-m6mB7iz8CXFIe0KemIOy84afF_TWcElYKkWmaCP0Rh0-Wk2sGOx1GWR4jqbOp8EtCPwNeiwgNgQhTxwdaWg1JIIwax4";
//        String token1 = "cHI9bL4QQTKQI_nJVtoUt7:APA91bF-VKnKY0NxeikBXORc0FhmS3iRHFYZgH5yBF34GNt-tYaUCvABVal1CSibhAEWkj9DDcmpZWlD4lA481JoC_UjVnykSYhyd5OHi3-91E-kVlX4YjEEK2v0fTQlqoW35yjNnlrd";
//		한건 메시지
//        service.sendMessageTo(token, "from 사무국", "싸피 여러분 화이팅!!");
        
//		멀티 메시지        
//        service.addToken(token);
//        service.addToken(token1);
        
        service.broadCastMessage("Notice", "개인 텀블러 이용시 300원 할인!");
        notiService.addNotification(new Notification("test", "Notice", "개인 텀블러 이용시 300원 할인!"));
        
    }
}
