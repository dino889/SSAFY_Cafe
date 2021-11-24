package com.ssafy.cafe.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.firebase.FirebaseCloudMessageService;
import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.User;


@Service
public class UserServiceImpl implements UserService {
    
    private static UserServiceImpl instance = new UserServiceImpl();

	@Autowired
	FirebaseCloudMessageService fcmService;
    
    private UserServiceImpl() {}
    
    public static String firstId = "";
    
    public static UserServiceImpl getInstance() {
        return instance;
    }
    
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void join(User user) {
    	String encodePassword = passwordEncoder.encode(user.getPass());
    	user.setPass(encodePassword);
        userDao.insert(user);
        firstId = user.getId().toString();
    }

    @Override
    public User login(String id, String pass) {
        User user = userDao.select(id);
        
        if(user != null && passwordEncoder.matches(pass, user.getPass())) {
        	return user;
        	
        } else {
        	return null;
        }
        
    }

    @Override
    public User getInfo(String id) {
        return userDao.select(id);
    }

    @Override
    public void leave(String id) {
        userDao.delete(id);
    }

    @Override
    public boolean isUsedId(String id) {
        return userDao.select(id)!=null;
    }

	@Override
	public void updateUserMoney(User user) {
		userDao.updateMoney(user);
	}
	
	@Override
	public boolean chkPw(String id, String pw) {
		return userDao.select(id).getPass().equals(pw);
	}

	@Override
	public void updateUserToken(User user) {
//		try {
//			if(user.getId().equals(firstId)) {
//				fcmService.sendMessageTo(user.getToken(), "User", "저희 어플을 이용해주셔서 감사합니다. 좋은 하루 되세요^^");
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//		}

		userDao.updateToken(user);
		
	}

	@Override
	public String selectUserId(String token) {
		return userDao.selectUserId(token);
	}
	
	@Override
	public String selectUserToken(String id) {
		
		return userDao.selectUserToken(id);
	}

	@Override
	public List<String> selectTokens() {
		return userDao.selectAllUserToken();
	}

	@Override
	public List<User> selectAllUser() {
		return userDao.selectAll();
	}
	
	
}
