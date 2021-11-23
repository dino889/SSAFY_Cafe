package com.ssafy.cafe.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.firebase.FirebaseCloudMessageService;
import com.ssafy.cafe.model.dao.OrderDao;
import com.ssafy.cafe.model.dao.OrderDetailDao;
import com.ssafy.cafe.model.dao.StampDao;
import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.OrderDetail;
import com.ssafy.cafe.model.dto.Stamp;
import com.ssafy.cafe.model.dto.User;

/**
 * @author itsmeyjc
 * @since 2021. 6. 23.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao oDao;
    
    @Autowired
    OrderDetailDao dDao;
    
    @Autowired
    StampDao sDao;
    
    @Autowired
    UserDao uDao;
    
    @Autowired
    UserService uService;
    
    @Autowired
    FirebaseCloudMessageService fcmService;
    
    @Autowired
    OrderStateChangeService observer;
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    
    @Override
    @Transactional
    public void makeOrder(Order order) {
        // 주문 및 주문 상세 테이블 저장
        oDao.insert(order);

        List<OrderDetail> details = order.getDetails();
        int quantitySum = 0;
        for(OrderDetail detail: details) {
            detail.setOrderId(order.getId());
            dDao.insert(detail);
            quantitySum += detail.getQuantity();
        }

        Stamp stamp = new Stamp(order.getUserId(), order.getId(), quantitySum);
        sDao.insert(stamp);

        User user = new User();
        user.setId(order.getUserId());
        user.setStamps(stamp.getQuantity());
        uDao.updateStamp(user);
        updateOrder(order);

    }

    @Override
    public Order getOrderWithDetails(Integer orderId) {
        return oDao.selectWithDetail(orderId);
    }

    @Override
    public List<Order> getOrdreByUser(String userId) {
        return oDao.selectByUser(userId);
    }
    static int count = 1;
    
    
    @Override
    public void updateOrder(Order order) {

    	Order observable = new Order();
        observable.addPropertyChangeListener(observer);

    	logger.info("updateOrder" + order.getUserId());
    	observer.setUserId(order.getUserId());
    	observer.setOrderId(order.getId());
    	
    	Random random = new Random();
    	
    	Timer timer = new Timer();
    	TimerTask task = new TimerTask() {
			@Override
			public void run() {
				observable.setCompleted(order.getCompleted());	// 주문 상태 변경 감지
				
				logger.info("timer on");
				logger.info(" 1:" + order.getCompleted());
				order.setCompleted(order.getCompleted() + 1);
				oDao.update(order);
				logger.info(order.toString());
				
				if(order.getCompleted() > 4)
				{
					this.cancel();
				}
			}
    		
    	};
    	timer.schedule(task, 1000, random.nextInt(30000) + 5000);	// 5초 ~ 30초 사이 랜덤
    	
    }
    
    @Override
    public List<Map> selectOrderTotalInfo(int id) {
        return oDao.selectOrderTotalInfo(id);
    }

    @Override
    public List<Map<String, Object>> getLastMonthOrder(String id) {
        return oDao.getLastMonthOrder(id);
    }
    
	@Override
	public void updateDupChk(Integer odId) {
		dDao.updateCommChk(odId);
	}

	@Override
	public Integer selectDupChk(HashMap<String, Object> map) {
		return oDao.selectdupchk(map);
	}
    

}
