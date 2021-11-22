package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;

import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.dto.User;


public interface UserService {
    /**
     * 사용자 정보를 DB에 저장한다.
     * 
     * @param user
     */
    public void join(User user);

    /**
     * id, pass에 해당하는 User 정보를 반환한다.
     * 
     * @param id
     * @param pass
     * @return
     * 조회된 User 정보를 반환한다.
     */
    public User login(String id, String pass);
    
    /**
     * id에 해당하는 사용자 정보를 삭제한다.
     * @param id
     */
    public void leave(String id);
    
    /**
     * 해당 아이디가 이미 사용 중인지를 반환한다.
     * @param id
     * @return
     */
    public boolean isUsedId(String id);


    public User getInfo(String id);

    void updateUserMoney(User user);
    
    /**
     * 비밀번호 체크
     * @param id
     * @param pw
     * @return
     */
    public boolean chkPw(String id, String pw);
 
    /**
     *  사용자 Token이 없데이트되면 DB에도 수정한다.
     * @param user
     */
    void updateUserToken(User user);
    
    String selectUserId(String token);
    
    String selectUserToken(String id);
    
    public List<String> selectTokens();
    

}
