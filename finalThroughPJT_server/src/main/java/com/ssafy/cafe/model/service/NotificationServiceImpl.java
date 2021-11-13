package com.ssafy.cafe.model.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.model.dao.NotificationDao;
import com.ssafy.cafe.model.dto.Notification;

/**
 * @author JiwooChoi
 * @since 2021. 11. 13.
 */

@Service
public class NotificationServiceImpl implements NotificationService {

   @Autowired
   NotificationDao nDao;
   
   @Override
   @Transactional
   public void addNotification(Notification notification) {
      nDao.insert(notification);
   }

   @Override
   @Transactional
   public void removeNotification(Integer id) {
      nDao.delete(id);
   }

   @Override
   @Transactional
   public Notification selectNotification(Integer id) {
      return nDao.select(id);
   }

   @Override
   public List<Notification> selectByUser(String userId) {
      return nDao.selectByUser(userId);
   }

   @Override
   public List<Notification> selectByUserAndCategory(HashMap<String, Object> map) {
//      HashMap<String, Object> map = new HashMap<String, Object>();
//      map.put("userId", userId);
//      map.put("category", category);

      return nDao.selectByUserAndCategory(map);
   }

}