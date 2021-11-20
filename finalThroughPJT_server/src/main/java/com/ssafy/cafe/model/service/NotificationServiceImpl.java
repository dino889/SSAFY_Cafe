package com.ssafy.cafe.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ssafy.cafe.model.dao.NotificationDao;
import com.ssafy.cafe.model.dto.Notification;

/**
 * @author JiwooChoi
 * @since 2021. 11. 13.
 */

@Service
public class NotificationServiceImpl implements NotificationService {

	private static final String firebase_server_key="AAAA04gVb9E:APA91bHenc0IOPweHRQip5LsyrE83AYIqQirNw_Cfl7d2JYEyiru7wvMJIJBMHCZGDHECGGjjp-VJaI8kOtdBDIBUAr5wtqVeE-n1tZ4tXMMq7XiGtQQaGVkH0tG1YAjnOKGaCFpmY7Q";
    private static final String firebase_api_url="https://fcm.googleapis.com/fcm/send";
	
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

//   @Async
//   public CompletableFuture<String> send(HttpEntity<String> entity) {
//
//       RestTemplate restTemplate = new RestTemplate();
//
//       ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//
//       interceptors.add(new HeaderRequestInterceptor("Authorization",  "key=" + firebase_server_key));
//       interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; UTF-8 "));
//       restTemplate.setInterceptors(interceptors);
//
//       String firebaseResponse = restTemplate.postForObject(firebase_api_url, entity, String.class);
//
//       return CompletableFuture.completedFuture(firebaseResponse);
//   }
}