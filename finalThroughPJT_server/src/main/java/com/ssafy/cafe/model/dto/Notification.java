package com.ssafy.cafe.model.dto;

public class Notification {
	private Integer id;
    private String userId;
    private String category;
    private String content;
    
    public Notification() {}
    
    public Notification(String userId, String category, String content) {
    	this.userId = userId;
    	this.category = category;
    	this.content = content;
    }

    public Notification(Integer id, String userId, String category, String content) {
		super();
		this.id = id;
		this.userId = userId;
		this.category = category;
		this.content = content;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", userId=" + userId + ", category=" + category + ", content=" + content
				+ "]";
	}	
}
