package com.ssafy.cafe.model.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Random;

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
import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.User;

@Component
public class OrderStateChangeService implements PropertyChangeListener {
	private static final Logger logger = LoggerFactory.getLogger(OrderStateChangeService.class);

	@Autowired
	FirebaseCloudMessageService fcmService;
	
	
	@Autowired
	UserService uService;
	

    private Integer orderState;
    
    private String userId;
    
    private Integer orderId;
    
    @Autowired
    OrderService oService;
    
    
	public void propertyChange(PropertyChangeEvent evt) {
        this.setCompleted((Integer) evt.getNewValue());
//        logger.info("message : {}", evt.getNewValue());
        
        String userToken = uService.selectUserToken(userId);
        
//        String userToken = "fuwi-tnmQZ-LiZKppdkkrL:APA91bG3ZYcde-fSLMXVcO0xatlrWscbVkx_QT56LPUmWnwVEugDx5rYG912zve9AtQI6arGQqrq0ZBLHLmhKtjsh0yIb3EqMk8iwd3MbcFZl-hIIQZVwcejvIkVrL-J3KL-Bj4cWxEH";
        Random random = new Random();
        try {
        	switch(orderState) {
        		case 0:
        			fcmService.sendMessageTo(userToken, "Order", "주문이 완료되었습니다.");
        			break;
        		case 1:
        			fcmService.sendMessageTo(userToken, "Order", "접수번호 : " + orderId + "\n주문 접수가 완료되었습니다.\n" + (random.nextInt(50) + 1) + "번째 메뉴로 준비중입니다.");
        			break;
        		case 2:
        			fcmService.sendMessageTo(userToken, "Order", "접수번호 : " + orderId + "\n메뉴가 모두 준비되었어요.\n픽업대에서 메뉴를 픽업해주세요!");
        			break;
        		case 3:
        			fcmService.sendMessageTo(userToken, "Order", "접수번호 : " + orderId + "\n픽업이 완료 되었습니다.");
        			break;
        		case 4:
        			List<Order> oList = oService.getOrdreByUser(userId);
        			
        			if(oList.size() == 1) {
        				logger.info("isEmpty");
            			fcmService.sendMessageTo(userToken, "User", "첫 주문이네요 ! \n 환영합니다 적립금 1000원을 선물로 드릴게요^^\n 많은 이용 부탁드릴게요~");
            			User user = uService.getInfo(userId);
            			int money = user.getMoney()+1000;
            			user.setMoney(money);
            			uService.updateUserMoney(user);
            			
            			
            			break;
        			}else {
        				break;
        			}
        	}
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
    }

    public Integer getCompleted() {
        return orderState;
    }

    public void setCompleted(Integer orderState) {
        this.orderState = orderState;
    }
    
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

}
