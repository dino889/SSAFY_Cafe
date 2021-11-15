package com.ssafy.cafe.model.dao;

import java.util.List;

import com.ssafy.cafe.model.dto.UserCustom;

public interface UserCustomDao {
	
	int insert(UserCustom userCustom);
	
	int delete(Integer customId);
	
	UserCustom select(Integer customId);
	
	List<UserCustom> selectAll();
	
	List<UserCustom> selectWithUser(String userId);
	
}
