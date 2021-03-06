package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;
import com.ssafy.cafe.model.dto.Product;

public interface ProductService {
    /**
     * 모든 상품 정보를 반환한다.
     * @return
     */
    List<Product> getProductList();
    
    /**
     * backend 관통 과정에서 추가됨
     * 상품의 정보, 판매량, 평점 정보를 함께 반환
     * @param productId
     * @return
     */
    List<Map<String, Object>> selectWithComment(Integer productId);
    
    List<Product> selectWithProductType(String type);
    
    List<Map<String, Object>> selectBestProduct();
    
    /**
     * productId에 해당하는 상품 정보를 반환한다.
     * @param productId
     * @return
     */
    Product selectProduct(Integer productId);
    
    List<Product> selectByName(String name);
    
    List<Product> getWeekBest();
    
}
