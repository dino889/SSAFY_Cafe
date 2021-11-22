package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.User;


@Service
public class UserServiceImpl implements UserService {
    
    private static UserServiceImpl instance = new UserServiceImpl();

    private UserServiceImpl() {}

    public static UserServiceImpl getInstance() {
        return instance;
    }
    
    @Autowired
    private UserDao userDao;

    @Override
    public void join(User user) {
        userDao.insert(user);

    }

    @Override
    public User login(String id, String pass) {
        User user = userDao.select(id);
        if (user != null && user.getPass().equals(pass)) {
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
}
