package com.ssafy.cafe.model.dto;

public class UserCustom {

	private Integer id;
    private String userId;
    private Integer productId;
    private Integer type;	// 1 or 0
    private String syrup;
    private Integer shot;
    
    public UserCustom() {}

    public UserCustom(String userId, Integer productId, Integer type, String syrup, Integer shot) {
    	this.userId = userId;
		this.productId = productId;
		this.type = type;
		this.syrup = syrup;
		this.shot = shot;
    }

    public UserCustom(Integer id, String userId, Integer productId, Integer type, String syrup, Integer shot) {
		super();
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.type = type;
		this.syrup = syrup;
		this.shot = shot;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
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

	@Override
	public String toString() {
		return "UserCustom [id=" + id + ", userId=" + userId + ", productId=" + productId + ", type=" + type
				+ ", syrup=" + syrup + ", shot=" + shot + "]";
	}
    
    

}
