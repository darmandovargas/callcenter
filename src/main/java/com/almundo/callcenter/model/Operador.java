package com.almundo.callcenter.model;

/**
 * Clase que extiende de Employee quien es la que procesa la llamada 
 * de manera recursiva y escala la cadena de mando con base en la disponibilidad 
 * @author Diego
 *
 */
public class Operador extends Employee{
	
	public Operador(String nam, String rol, Integer ran){
		this.name = nam;
		this.role = rol;
		this.rank = ran;
	}
	
	protected void setName(String na){
		this.name = na;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected void setRole(String ro){
		this.role = ro;
	}
	
	protected String getRole(){
		return this.role;
	}
	
	protected void setRank(Integer ra){
		this.rank = ra;
	}
	
	protected Integer getRank(){
		return this.rank;
	}
}
