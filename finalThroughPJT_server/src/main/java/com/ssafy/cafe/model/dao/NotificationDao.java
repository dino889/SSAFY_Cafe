package com.ssafy.cafe.model.dao;

import java.util.HashMap;
import java.util.List;
import com.ssafy.cafe.model.dto.Notification;

public interface NotificationDao {
   
    int insert(Notification notification);

    int delete(Integer notificationId);

    Notification select(Integer commentId);

    List<Notification> selectAll();

    List<Notification> selectByUser(String userId);
    
    List<Notification> selectByUserAndCategory(HashMap<String, Object> map);
}