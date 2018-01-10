package com.almundo.callcenter.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.almundo.callcenter.ctrl.Dispatcher;

public abstract class Employee {
	
	protected String name;
	protected String role;
	protected Integer rank;
	protected Employee successor;
	
	abstract protected void setName(String na);
	abstract protected String getName();
	
	abstract protected void setRole(String ro);
	abstract protected String getRole();
	
	abstract protected void setRank(Integer ra);
	abstract protected Integer getRank();
	
	public void setSuccessor(Employee empleado){
		this.successor = empleado;
	}
	
	public void processCallRequest(CallRequest callRequest, AtomicInteger operadoresAvailable, AtomicInteger supervisoresAvailable, AtomicInteger directoresAvailable, Dispatcher disp) {
		
		AtomicInteger availableEmployeeNumber = new AtomicInteger(0);
		int temporal = 0;
		
		switch(this.getRank()){
			case 1: availableEmployeeNumber = operadoresAvailable; break;
			case 2: availableEmployeeNumber = supervisoresAvailable; break;
			case 3: availableEmployeeNumber = directoresAvailable; break;
		}
		temporal = availableEmployeeNumber.get();
		if (temporal > 0) {            
            try {
            	switch(this.getRank()){
	    			case 1: if(operadoresAvailable.get()>0)disp.decrementarOperadoresDisponibles(); break;
	    			case 2: if(supervisoresAvailable.get()>0)disp.decrementarSupervisoresDisponibles(); break;
	    			case 3: if(directoresAvailable.get()>0)disp.decrementarDirectoresDisponibles(); break;
	    		}
            	System.out.println("El empleado '" + this.getRole().toUpperCase() + "' llamado " + this.getName() + " ha tomado la llamada con el cliente: "+callRequest.getClientName()+". operadoresAvailable:"+operadoresAvailable.get()+", supervisoresAvailable:"+supervisoresAvailable.get()+", directoresAvailable: "+directoresAvailable.get());
            	disp.removeLlamadaEnEspera(callRequest.getClientName());
            	TimeUnit.SECONDS.sleep(callRequest.getDuration());
            	switch(this.getRank()){
	    			case 1: disp.incrementarOperadoresDisponibles(); break;
	    			case 2: disp.incrementarSupervisoresDisponibles(); break;
	    			case 3: disp.incrementarDirectoresDisponibles(); break;
	    		}
    			System.out.println("Terminando la llamada con '" + this.getRole().toUpperCase() + "' llamado " + this.getName() + " con cliente: " + callRequest.getClientName() + ". Duración: " + callRequest.getDuration() + " segundos" );
    			
    			
    			
    		} catch (InterruptedException e) {
    			System.out.println(e);
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } else if (successor != null) {
        	switch(this.getRank()+1){
				case 2: availableEmployeeNumber = supervisoresAvailable;						
						successor.processCallRequest(callRequest, operadoresAvailable, availableEmployeeNumber, directoresAvailable, disp); 
						break;
				case 3: availableEmployeeNumber = directoresAvailable; 
						successor.processCallRequest(callRequest, operadoresAvailable, operadoresAvailable, availableEmployeeNumber, disp); 
						break;
			}
        } else{
        	System.out.println("RESPUESTA "+callRequest.getClientName()+" POR FAVOR ESPERE EN LA LÍNEA PARA SER ATENDIDO, NUESTROS AGENTES ESTÁN OCUPADOS, REMITIREMOS SU LLAMADA AL PRIMER AGENTE DISPONIBLE...");
        	/*try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				System.out.println(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(operadoresAvailable.get()>0){
        		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
        	}else if(supervisoresAvailable.get()>0){
        		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
        	}else if (directoresAvailable.get()>0){
        		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
        	}else{
        		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
        	}*/
        	disp.putLlamadaEnEspera(callRequest.getClientName(), callRequest);
        	
        }
    }
}
