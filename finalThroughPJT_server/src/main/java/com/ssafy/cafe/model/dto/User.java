package com.ssafy.cafe.model.dto;

import java.util.ArrayList;
import java.util.List;


public class User {
    private String id;
    private String name;
    private String pass;
    private Integer stamps;
    private String phone;
    private Integer money;
    private String token;


	private List<Stamp> stampList = new ArrayList<>();

    public User() {}
    

	public User(String id, String name, String pass, Integer stamps, String phone, Integer money) {
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.stamps = stamps;
        this.phone = phone;
        this.money = money;
    }
    

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Integer getStamps() {
		return stamps;
	}

	public void setStamps(Integer stamps) {
		this.stamps = stamps;
	}

	public List<Stamp> getStampList() {
		return stampList;
	}

	public void setStampList(List<Stamp> stampList) {
		this.stampList = stampList;
	}

	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Integer getMoney() {
		return money;
	}
	
	public void setMoney(Integer money) {
		this.money = money;
	}

    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pass=" + pass + ", stamps=" + stamps + ", phone=" + phone
				+ ", money=" + money + ", stampList=" + stampList + "]";
	}

    
}
