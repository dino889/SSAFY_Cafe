package com.ssafy.cafe.model.dto;


public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer type;	// 1 or 0
    private String syrup;
    private Integer shot;
    private Integer totalPrice;
    
    


	public OrderDetail() {}
    

	public OrderDetail(Integer id, Integer orderId, Integer productId, Integer quantity, Integer type, String syrup, Integer shot, Integer totalPrice) {
        super();
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
        this.syrup = syrup;
        this.shot = shot;
        this.totalPrice = totalPrice;
    }
    
    public OrderDetail(Integer id, Integer orderId, Integer productId, Integer quantity, Integer totalPrice) {
        super();
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
    
    public OrderDetail(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public OrderDetail(Integer orderId, Integer productId, Integer quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
    

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSyrup() {
		return syrup;
	}

	public void setSyrup(String syrup) {
		this.syrup = syrup;
	}

	public Integer getShot() {
		return shot;
	}

	public void setShot(Integer shot) {
		this.shot = shot;
	}
	
	public Integer getTotalPrice() {
		return totalPrice;
	}


	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public String toString() {
		return "OrderDetail [id=" + id + ", orderId=" + orderId + ", productId=" + productId + ", quantity=" + quantity
				+ ", type=" + type + ", syrup=" + syrup + ", shot=" + shot + "]";
	}
	
	
    
    
}
