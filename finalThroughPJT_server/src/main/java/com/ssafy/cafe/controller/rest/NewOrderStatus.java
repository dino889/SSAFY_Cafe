package com.ssafy.cafe.controller.rest;

public class NewOrderStatus implements OrderStatus {
	private Integer completed;
	
	@Override
	public void update(Object completed) {
		this.completed = ((Integer)completed);
	}

}
