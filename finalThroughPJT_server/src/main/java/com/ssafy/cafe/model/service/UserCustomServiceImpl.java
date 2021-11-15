package com.ssafy.cafe.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.model.dao.UserCustomDao;
import com.ssafy.cafe.model.dto.UserCustom;

/**
 * 
 * @author Jiwoo Choi
 * @since 21.11.16.
 */

@Service
public class UserCustomServiceImpl implements UserCustomService {

	@Autowired
	UserCustomDao ucDao;
	
	
	@Override
	@Transactional
	public void addUserCustomProd(UserCustom userCustom) {
		ucDao.insert(userCustom);
		
	}

	@Override
	@Transactional
	public void removeUserCustomProd(Integer customId) {
		ucDao.delete(customId);
	}

	@Override
	public UserCustom selectUserCustom(Integer customId) {
		return ucDao.select(customId);
	}

	@Override
	public List<UserCustom> selectUserCustomList() {
		return ucDao.selectAll();
	}

	@Override
	public List<UserCustom> selectUserCustomWithUserId(String userId) {
		return ucDao.selectWithUser(userId);
	}

}
