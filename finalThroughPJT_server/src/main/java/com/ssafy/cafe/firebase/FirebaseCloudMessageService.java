package com.ssafy.cafe.firebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssafy.cafe.firebase.*;
import com.ssafy.cafe.firebase.FcmMessage.Message;
import com.ssafy.cafe.model.dto.User;
import com.ssafy.cafe.model.dto.Notification;
import com.ssafy.cafe.model.service.NotificationService;
import com.ssafy.cafe.model.service.UserService;
import com.ssafy.cafe.model.service.UserServiceImpl;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Component
public class FirebaseCloudMessageService {

    
   private static final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageService.class);
   
    public final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/final-coffee-project/messages:send";
    
    
    @Autowired
    UserService uService;

    @Autowired
    NotificationService notiService;
    
    /**
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     * @return
     * @throws IOException
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는객체
        GoogleCredentials googleCredentials = GoogleCredentials
                // 서버로부터 받은 service key 파일 활용
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                // 인증하는 서버에서 필요로 하는 권한 지정
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
        
        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();
//        logger.info(token.toString());
        return token;
    }
    
    /**
     * FCM 알림 메시지 생성
     * @param targetToken
     * @param title
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    // 누구한테 title, body 보낼건지
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
//        Notification noti = new FcmMessage.Notification(title, body, null);
//        Message message = new FcmMessage.Message(noti, targetToken);
       // 추가 - background 구동
       Message message = new FcmMessage.Message(null, targetToken);
       Map<String, String> data = new HashMap<>();
       data.put("title", title);
       data.put("body", body);
       
       message.setData(data);
       
       FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }
    

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * @param targetToken
     * @param title
     * @param body
     * @throws IOException
     */
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);
        logger.info("message : {}", message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                // 전송 토큰 추가 - 서버로 부터 받은 토큰 값
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        
        logger.info("service"+targetToken);
        if(response.isSuccessful()) {
        	String userId = uService.selectUserId(targetToken);
        	logger.info("USERID  "+userId);
        	notiService.addNotification(new Notification(userId, title, body));
        }
    }


    private List<String> clientTokens = new ArrayList<>();
    
    public FirebaseCloudMessageService(ObjectMapper objectMapper){
       this.objectMapper = objectMapper;
    }

    
    // 클라이언트 토큰 관리
    public void addToken(String token, String userId) {	// userI랑 token을 파라미타로 받아서 t_user의 token 값 저장
//        clientTokens.add(token);
    	User user = new User();
    	user.setId(userId);
    	user.setToken(token);
        uService.updateUserToken(user);
    }
    
    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastMessage(String title, String body) throws IOException {	// select token from t_user -> 모든 사용자에게 noti 전송
    	List<User> users = uService.selectAllUser();
    	for(User user: users) {
//          logger.debug("broadcastmessage : {},{},{}", user.getToken(), title, body);
          sendMessageTo(user.getToken(), title, body);
        }
    
       return users.size();
    }

}