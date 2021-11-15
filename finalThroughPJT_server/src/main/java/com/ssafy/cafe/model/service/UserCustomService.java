package com.ssafy.cafe.model.service;

import java.util.List;

import com.ssafy.cafe.model.dto.UserCustom;

public interface UserCustomService {
	
	/**
     * 사용자 custom + 즐겨찾기 메뉴를 DB에 저장한다.
     * 
     * @param UserCustom
     */
	public void addUserCustomProd(UserCustom userCustom);
	
	/**
	 * id에 해당하는 사용자 custom 상품 정보를 삭제한다.
	 * @param customId
	 */
	public void removeUserCustomProd(Integer customId);
	
	/**
	 * id에 해당하는 사용자 custom 상품 정보를 반환한다.
	 * @param customId
	 * @return
	 */
	UserCustom selectUserCustom(Integer customId);
	
	/**
	 * 모든 사용자 custom 상품을 반환한다.
	 * @return
	 */
	List<UserCustom> selectUserCustomList();
	
	/**
	 * 사용자 id에 해당하는 사용자 custom 상품 정보를 반환한다.
	 * @param userId
	 * @return
	 */
	List<UserCustom> selectUserCustomWithUserId(String userId);
	
}
