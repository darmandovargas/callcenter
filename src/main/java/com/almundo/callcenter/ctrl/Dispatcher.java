package com.almundo.callcenter.ctrl;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.almundo.callcenter.model.CallRequest;
import com.almundo.callcenter.model.Director;
import com.almundo.callcenter.model.Operador;
import com.almundo.callcenter.model.Supervisor;

public class Dispatcher {

	public static AtomicInteger operadoresDisponibles = new AtomicInteger(6);
	public static AtomicInteger supervisoresDisponibles = new AtomicInteger(3);
	public static AtomicInteger directoresDisponibles = new AtomicInteger(1);
	public static Map<String, CallRequest> listaLlamadasEnEspera = new ConcurrentHashMap<String, CallRequest>();

	public Dispatcher() {

	}

	public static void incrementarOperadoresDisponibles() {
		operadoresDisponibles.incrementAndGet();
	}

	public static void decrementarOperadoresDisponibles() {
		operadoresDisponibles.decrementAndGet();
	}

	public static void incrementarSupervisoresDisponibles() {
		supervisoresDisponibles.incrementAndGet();
	}

	public static void decrementarSupervisoresDisponibles() {
		supervisoresDisponibles.decrementAndGet();
	}

	public static void incrementarDirectoresDisponibles() {
		directoresDisponibles.incrementAndGet();
	}

	public static void decrementarDirectoresDisponibles() {
		directoresDisponibles.decrementAndGet();
	}
	
	public static void putLlamadaEnEspera(String key, CallRequest value){
		listaLlamadasEnEspera.put(key, value);
	}
	
	public CallRequest getLlamadaEnEsperaByKey(String key){
		return listaLlamadasEnEspera.get(key);
	}
	
	public Map<String, CallRequest> getLlamadaEnEsperaMap(){
		return listaLlamadasEnEspera;
	}
	
	public boolean removeLlamadaEnEspera(String key){
		return listaLlamadasEnEspera.remove(key).getClientName() == key;
	}

	public boolean dispatchCall(Integer callsAmount) {		
		// Dispatch amount of calls 
		for (int x = 0; x < callsAmount; x++) {
			Operador operador = new Operador("Empleado # "+x, "Operador", 1);
			Supervisor supervisor = new Supervisor("Empleado # "+x, "Supervisor", 2);
			Director director = new Director("Empleado # "+x, "Director", 3);

			operador.setSuccessor(supervisor);
			supervisor.setSuccessor(director);
			
			ExecutorService service = null;
			Random random = new Random();
			Integer duracionLlamada = random.ints(5, 10).findFirst().getAsInt();
			String name = "Client " + String.valueOf(x);
			try {
				service = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(callsAmount);
				service.submit(() -> operador.processCallRequest(new CallRequest(name, duracionLlamada), operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this));
			} finally {
				if (service != null) {
					service.shutdown();
				}
			}
		}
		/*
		 * Mock de solución extra al problema de las llamadas en espera que no pudieron ser atendidas concurrentemente
		if(!this.listaLlamadasEnEspera.isEmpty()){
			//this.listaLlamadasEnEspera.forEach((k,v) -> (service.submit(() -> operador.processCallRequest(v, operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this))));//System.out.println("Item : " + k + " Count : " + v)
			for (Map.Entry<String, CallRequest> entry : listaLlamadasEnEspera.entrySet()) {
				System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
				ExecutorService servicePending = null;
				try {
					servicePending = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(callsAmount);
					Random random = new Random();
					Integer duracionLlamada = random.ints(5, 10).findFirst().getAsInt();
					String name = "Client " + String.valueOf(x);
					servicePending.submit(() -> operador.processCallRequest(entry.getValue(), operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this));
				} finally {
					if (servicePending != null) {
						servicePending.shutdown();
					}
				}
			}*/
			/*ExecutorService servicePending = null;
			try {
				service = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(callsAmount);
				Random random = new Random();
				Integer duracionLlamada = random.ints(5, 10).findFirst().getAsInt();
				String name = "Client " + String.valueOf(x);
				servicePending.submit(() -> operador.processCallRequest(new CallRequest(name, duracionLlamada), operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this));
			} finally {
				if (servicePending != null) {
					servicePending.shutdown();
				}
			}*/
		//}

		return true;
	}
	
	/* Mock de solución extra al problema de las llamadas en espera que no pudieron ser atendidas concurrentemente
	public Integer atenderColaDeLlamadas(){
		
		if(operadoresDisponibles.get()>0){
    		this.processCallRequest(callRequest, operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, disp);
    	}else if(supervisoresDisponibles.get()>0){
    		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
    	}else if (directoresAvailable.get()>0){
    		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
    	}else{
    		this.processCallRequest(callRequest, operadoresAvailable, supervisoresAvailable, directoresAvailable, disp);
    	}
	}*/

}
