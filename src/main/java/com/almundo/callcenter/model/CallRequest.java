package com.almundo.callcenter.model;

public class CallRequest {
	private String clientName;
	private Integer duration;
	
	public CallRequest (String cn, Integer dur){
		this.clientName = cn;
		this.duration = dur;
		System.out.println(cn+" inicia Llamada...");
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
}
