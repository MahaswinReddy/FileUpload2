package com.intuit.model;

import java.io.Serializable;

public class Data implements Serializable{
private String url;
private String order;
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getOrder() {
	return order;
}
public void setOrder(String order) {
	this.order = order;
}

}
