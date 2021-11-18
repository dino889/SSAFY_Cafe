package com.ssafy.cafe.controller.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.Product;
import com.ssafy.cafe.model.dto.User;
import com.ssafy.cafe.model.dto.UserCustom;
import com.ssafy.cafe.model.service.OrderService;
import com.ssafy.cafe.model.service.StampService;
import com.ssafy.cafe.model.service.UserCustomService;
import com.ssafy.cafe.model.service.UserService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/user")
@CrossOrigin("*")
public class UserRestController {


    @Autowired
    UserService uService;

    @Autowired
    StampService sService;

    @Autowired
    OrderService oService;
    


    @PostMapping
    @ApiOperation(value = "사용자 정보를 추가한다.", response = Boolean.class)
    public Boolean insert(@RequestBody User user) {
        uService.join(user);
        return true;
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려보낸다.", response = User.class)
    public User login(@RequestBody User user, HttpServletResponse response) throws UnsupportedEncodingException {
        User selected = uService.login(user.getId(), user.getPass());
        if (selected != null) {
            Cookie cookie = new Cookie("loginId", URLEncoder.encode(selected.getId(), "utf-8"));
            cookie.setMaxAge(1000 * 1000);
            response.addCookie(cookie);
        }else {
        	selected = new User();
        }
        return selected;
    }

    @PostMapping("/info")
    @ApiOperation(value = "사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보를 반환한다.", response = Map.class)
    public Map<String, Object> getInfo(@RequestParam("userId") String userId) {
        User selected = uService.getInfo(userId);
        if (selected == null) {
            return null;
        } else {
            Map<String, Object> info = new HashMap<>();
            info.put("user", selected);
            List<Order> orders = oService.getOrdreByUser(userId);
            info.put("order", orders);
            info.put("grade", getGrade(selected.getStamps()));
            return info;
        }
    }
    
    @GetMapping("/isUsed")
    @ApiOperation(value = "id가 사용 중이면 true를 반환한다.", response = Boolean.class)
    public Boolean isUsedId(@RequestParam("userId") String userId) {
    	return uService.isUsedId(userId);
    }
    
    public Map<String, Object> getGrade(Integer stamp) {
        Map<String, Object> grade = new HashMap<>();
        int pre = 0;
        for (Level level : levels) {
            if (level.max >= stamp) {
                grade.put("title", level.title);
                grade.put("img", level.img);
                if (!level.title.equals("커피나무")) {
                    int step = (stamp - pre) / level.unit + ((stamp - pre) % level.unit > 0 ? 1 : 0);
                    grade.put("step", step);
                    int to = level.unit - (stamp - pre) % level.unit;
                    grade.put("to", to);
                }
                break;
            } else {
                pre = level.max;
            }
        }
        return grade;
    }

    private List<Level> levels;

    @PostConstruct
    public void setup() {
        levels = new ArrayList<>();
        levels.add(new Level("씨앗", 10, 50, "seeds.png"));
        levels.add(new Level("꽃", 15, 125, "flower.png"));
        levels.add(new Level("열매", 20, 225, "coffee_fruit.png"));
        levels.add(new Level("커피콩", 25, 350, "coffee_beans.png"));
        levels.add(new Level("커피나무", Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png"));
    }



    class Level {
        private String title;
        private int unit;
        private int max;
        private String img;

        private Level(String title, int unit, int max, String img) {
            this.title = title;
            this.unit = unit;
            this.max = max;
            this.img = img;
        }
    }
    
    
    
    
}
