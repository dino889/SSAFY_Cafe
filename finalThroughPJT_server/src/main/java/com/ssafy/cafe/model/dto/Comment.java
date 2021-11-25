package com.ssafy.cafe.model.dto;

import java.util.Date;

public class Comment {
    private Integer id;
    private String userId;
    private Integer productId;
    private Double rating;
    private String comment;
    private Date date;
    
	public Comment(Integer id, String userId, Integer productId, Double rating, String comment, Date date) {
		super();
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.rating = rating;
		this.comment = comment;
		this.date = date;
	}
	
    
	public Comment( String userId, Integer productId, Double rating, String comment, Date date) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }
    
    public Comment() {
    	
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

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", userId=" + userId + ", productId=" + productId + ", rating=" + rating
				+ ", comment=" + comment + "]";
	}
    
    
    
}