package com.ssafy.cafe.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.service.CommentService;
import com.ssafy.cafe.model.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/comment")
@CrossOrigin("*")
public class CommentRestController {

    @Autowired
    CommentService cService;
    
    @Autowired
    OrderService oService;
    
    @PostMapping
    @Transactional
    @ApiOperation(value="comment 객체를 추가한다.", response = Boolean.class)
    public Boolean insert(@RequestBody Comment comment  ) {
        cService.addComment(comment);
        return true;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ApiOperation(value="{id}에 해당하는 사용자 정보를 삭제한다.", response = Boolean.class)
    public Boolean delete(@PathVariable Integer id) {
        cService.removeComment(id);
        return true;
    }
    
    @PutMapping
    @Transactional
    @ApiOperation(value="comment 객체를 수정한다.", response = Boolean.class)
    public Boolean update(@RequestBody Comment comment  ) {
        cService.updateComment(comment);
        return true;
    }
    
    @GetMapping("/{userId}")
    @ApiOperation(value="{userId}에 해당하는 comment 정보를 반환한다."
    		+ "이 기능은 사용자의 comment를 조회할 때 사용된다.", response = List.class)
    public List<Comment> selectCommentByUser(@PathVariable String userId) {
    	return cService.selectByUser(userId);
    }
    
    @GetMapping("/dupchk")
    @ApiOperation(value="사용자가 리뷰를 작성하려는 해당 품목이 최근 7일 내에 리뷰를 작성한 적이 있는지 체크한다. "
    		+ "작성하지 않았다면 detail Id를 반환한다.", response = Integer.class)
    public Integer selectNotWrittenComm(@RequestParam("userId") String userId, @RequestParam("productId") Integer productId) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("productId", productId);
        
    	return oService.selectDupChk(map);
    }
    
    @PutMapping("/dupchk")
    @Transactional
    @ApiOperation(value="중복 리뷰를 방지하기 위해 사용자가 리뷰를 작성하면 dup 컬럼의 값을 true로 바꿔준다.", response = Boolean.class)
    public Boolean updateDupChk(@RequestParam Integer dId) {
    	oService.updateDupChk(dId);
    	return true;
    }
    
   

}
