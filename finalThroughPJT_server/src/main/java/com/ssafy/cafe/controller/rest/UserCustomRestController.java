package com.ssafy.cafe.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.UserCustom;
import com.ssafy.cafe.model.service.UserCustomService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/custom")
@CrossOrigin("*")
public class UserCustomRestController {

    @Autowired
    UserCustomService ucService;
    
	//-----------------------------------------------------------------------------------------
    // UserCustom Product
    @PostMapping
    @Transactional
    @ApiOperation(value="사용자 Custom 메뉴 정보를 추가한다.", response = Boolean.class)
    public Boolean insert(@RequestBody UserCustom userCustom) {
    	ucService.addUserCustomProd(userCustom);
    	return true;
    }
    
    @DeleteMapping("/{id}")
    @Transactional
    @ApiOperation(value="사용자 Custom 메뉴 정보를 삭제한다.", response = Boolean.class)
    public Boolean delete(@PathVariable Integer id) {
    	ucService.removeUserCustomProd(id);
    	return true;
    }
    
    @GetMapping()
    @ApiOperation(value="전체 사용자 Custom 메뉴로 등록된 정보를 반환한다.", response = List.class)
    public List<UserCustom> selectUserCustomList(){
        return ucService.selectUserCustomList();
    }
    
    @GetMapping("/{customId}")
    @ApiOperation(value="{customId}에 해당하는 custom 메뉴의 정보를 반환한다.", response = UserCustom.class)
    public UserCustom getProductWithComments(@PathVariable Integer customId){
        return ucService.selectUserCustom(customId);
    }
    
    @GetMapping("/uc/{userId}")
    @ApiOperation(value="{userId}에 해당하는 custom 메뉴의 목록을 반환한다."
    		+ "이 기능은 사용자별 custom 메뉴를 조회할 때 사용된다.", response = List.class)
    public List<UserCustom> getCustomWithUserId(@PathVariable String userId){
        return ucService.selectUserCustomWithUserId(userId);
    }
}
