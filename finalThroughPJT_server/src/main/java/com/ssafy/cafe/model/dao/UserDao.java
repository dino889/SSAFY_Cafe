package com.ssafy.cafe.model.dao;

import java.util.List;
import java.util.Map;

import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.dto.User;

public interface UserDao {
    /**
     * 사용자 정보를 추가한다.
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 사용자의 Stamp 정보를 수정한다.
     * @param user
     * @return
     */
    int updateStamp(User user);
    
    /**
     * 사용자 정보를 조회한다.
     * @param userId
     * @return
     */

    User select(String userId);

    /**
     * 사용자 정보를 삭제한다.
     * @param userId
     * @return
     */
    int delete(String userId);
    
    List<User> selectAll(); 
    
    int update(User user);
    
    int updateMoney(User user);

    
    /**
     *  사용자 토큰을 수정한다.
     * @param user
     * @return
     */
    int updateToken(User user);
    
    /**
     * 
     * @param token
     * @return
     */
    String selectUserId(String token);
    
    /**
     * 사용자 id로 token을 가져온다.
     * @param id
     * @return
     */
    String selectUserToken(String id);
    
    /**
     * 모든 사용자의 토큰 값을 조회한다.
     * @return
     */
    List<String> selectAllUserToken();
}
