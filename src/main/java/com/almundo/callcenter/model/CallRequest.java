package com.almundo.callcenter.model;
/**
 * Esta clase representa la petición de llamada de parte del cliente
 * @author Diego
 *
 */
public class CallRequest {
	// Nombre del cliente
	private String clientName;
	// Duración de la llamada
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
