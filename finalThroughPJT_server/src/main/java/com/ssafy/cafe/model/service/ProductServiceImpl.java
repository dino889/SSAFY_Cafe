package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ssafy.cafe.model.dao.ProductDao;
import com.ssafy.cafe.model.dto.Product;

/**
 * @author jiwoo
 * @since 2021. 11. 18.
 */
@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductDao pDao;

    @Override
    @Cacheable(value="getProductList")
    public List<Product> getProductList() {
        return pDao.selectAll();
    }

    @Override
    public List<Map<String, Object>> selectWithComment(Integer productId) {
        return pDao.selectWithComment(productId);
    }
    
	@Override
	public List<Product> selectWithProductType(String type) {
		return pDao.selectWithProductType(type);
	}

	@Override
	public List<Map<String, Object>> selectBestProduct() {
		return pDao.selectBestProduct();
	}

	@Override
	public Product selectProduct(Integer productId) {
		return pDao.select(productId);
	}

	@Override
	public List<Product> selectByName(String name) {
		return pDao.selectByName(name);
	}

	@Override
	public List<Product> getWeekBest() {
		return pDao.selectWeekBest();
	}

}
