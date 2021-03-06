package com.ssafy.cafe.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.OrderDetail;

public interface OrderService {
    /**
     * 새로운 Order를 생성한다.
     * Order와 OrderDetail에 정보를 추가한다.
     * [심화]User 테이블에 사용자의 Stamp 개수를 업데이트 한다.
     * [심화]Stamp 테이블에 Stamp 이력을 추가한다.
     * @param order
     */
    public void makeOrder(Order order);
    
    /**
     * orderId에 대한 Order를 반환한다.
     * 이때 Order에 해당하는 OrderDetail에 대한 내용까지 반환한다.
     * OrderDetail의 내용은 id에 대한 내림차순으로 조회한다.
     * @param orderId
     * @return
     */
    public Order getOrderWithDetails(Integer orderId);
    
    
    /**
     * userId에 해당하는 사용자의 Order 목록을 주문 번호의 내림차순으로 반환한다.
     * 
     * @param userId
     * @return
     */
    public List<Order> getOrdreByUser(String userId);
    
    /**
     * 주문 정보를 수정한다. - 주문의 상태만 변경된다.
     * @param order
     */
    public void updateOrder(Order order);
    
    /**
     *  back end 관통에서 추가함
     *  화면에 뿌려주기 위한 합성 정보 전달
     * @param id
     * @return
     */
    List<Map> selectOrderTotalInfo(int id); 
    
    /**
     * 최근 1개월의 주문 내역을 반환한다.
     * 관통 6단계에서 추가됨
     * @param id
     * @return
     */
    List<Map<String, Object>> getLastMonthOrder(String id);
    
    /**
     * User의 최근 7일 간 주문 내역 중 comment가 작성되어 있지 않은 상품 최근 1건 조회 -> 없으면 리뷰 작성 ㄴㄴ
     * @param userId
     * @return 
     */
//    public Integer getDupChk(String userId, Integer productId);
    
    
//    public OrderDetail getDupChk(Integer productId, Integer orderId);
    
    public Integer selectDupChk(HashMap<String, Object> map);
    
    
    public void updateDupChk(Integer odId);
    
    
}
