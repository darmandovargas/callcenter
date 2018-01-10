package com.almundo.callcenter.ctrl;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.almundo.callcenter.model.CallRequest;
import com.almundo.callcenter.model.Director;
import com.almundo.callcenter.model.Operador;
import com.almundo.callcenter.model.Supervisor;

/**
 * Clase que maneja las llamadas y las envía en hilos de manera concurrente; 
 * también se encarga de poner en cola las llamadas que no se pudieron atender
 * porque todo el personal estaba ocupado, y revisa la cola cada 20 segundos ejecutando 
 * las llamadas en espera hasta que la cola esté vacía.
 * @author Diego
 *
 */
public class Dispatcher {
	// Las siguientes variables representa el entero con los diferentes tipos de empleados disponibles disponibles, 
	// es una variable estática de tipo AtomicInteger la cual contola el acceso concurrente de los hilos para evitar 
	// problemas de integridad
	public static AtomicInteger operadoresDisponibles = new AtomicInteger(6);
	public static AtomicInteger supervisoresDisponibles = new AtomicInteger(3);
	public static AtomicInteger directoresDisponibles = new AtomicInteger(1);
	// Es un Map en el que se guardarán las llamadas en espera
	public static Map<String, CallRequest> listaLlamadasEnEspera = new ConcurrentHashMap<String, CallRequest>();
	// Constructor
	public Dispatcher() {

	}
	// Métodos get y set
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
		listaLlamadasEnEspera.remove(key);
		//return listaLlamadasEnEspera.remove(key).getClientName() == key;
		return true;
	}
	/**
	 * Método que recibe el número de llamadas concurrentes, por cada llamado construye una cadena de mando
	 * asignando a quien sigue de manera ascendente en la cadena de responsabilidad. Se crea un ExecutorService,
	 * se da un tiempo de duración de llamada de manera aleatoria entre 5 y 10 segundos y se dá un nombre de cliente
	 * según el número de la iteración, luego se ejecuta el método del padre Employee llamado processCallRequest,
	 * el cual se procesa la llamada en sí, y agrega y disminuye tipos de empleados disponibles según sea que inicia o termina la llamada.
	 * @param callsAmount
	 * @return
	 */
	public boolean dispatchCall(Integer callsAmount) {		
		// Dispatch amount of calls 
		for (int x = 0; x < callsAmount; x++) {
			// Creo cadena de responsabilidad
			Operador operador = new Operador("Empleado # "+x, "Operador", 1);
			Supervisor supervisor = new Supervisor("Empleado # "+x, "Supervisor", 2);
			Director director = new Director("Empleado # "+x, "Director", 3);
			// Asigno siguiente escala de responsabilidad de manera ascendente
			operador.setSuccessor(supervisor);
			supervisor.setSuccessor(director);
			// Creo el servicio que creará los hilos concurrentes
			ExecutorService service = null;
			// Creo una instancia de Random
			Random random = new Random();
			// Determino una duración de llamada de manera aleatoria entre 5 y 10 segundos
			Integer duracionLlamada = random.ints(5, 10).findFirst().getAsInt();
			// Creo un nombre de cliente según el número de iteración actual
			String name = "Client " + String.valueOf(x);
			try {
				// Creo un hilo de ejecución
				service = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(callsAmount);
				// Ejecuto el hilo con submit, del cual puedo obtener una instancia de Future para obtener algun resultado de la ejecución
				// dentro del hilo, ejecuto el método del padre de operador, llamado processCallRequest, que recibe una instancia de CallRequest, 
				// los AtomInteger de los tipos de empleados disponibles, y una instancia del objeto actual, de Dispatcher, para alimentar el ConcurrentHashMap
				// con la lista de las llamadas en espera
				service.submit(() -> operador.processCallRequest(new CallRequest(name, duracionLlamada), operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this));
			} finally {
				if (service != null) {
					// Ésto permite cerrar el hilo, el cual terminará una vez finalice la ejecución de la tarea enviada más no recibirá más peticiones.
					service.shutdown();
				}
			}
		}
		
		// Esta es una solución básica para cuando no hay empleados disponibles que atiendan una llamada, 
		// el processCallRequest del empleado pone en cola las llamadas que no pudo atender y revisa la cola cada 20 segundos
		// entonces procede a lanzar un hilo por cada llamada en cola hasta que la cola esté vacía.
		// TODO: Esta solución podría ser mejorada con CompletableFuture, pero habría que reestructurar toda la solución, 
		// o trabajando con el resultado de submit para obtener la instancia Future con el resultado de la tarea, se puede 
		// mejorar mucho aún pero es un buen inicio hacia una solución optima. 
		while(!this.listaLlamadasEnEspera.isEmpty()){
			System.out.println("ESPERANDO 20 SEGUNDOS PARA REVISAR COLA PENDIENTE...");
			try {
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			if(!this.listaLlamadasEnEspera.isEmpty()){
				System.out.println("EJECUTANDO LLAMADAS EN ESPERA...");
				//this.listaLlamadasEnEspera.forEach((k,v) -> (service.submit(() -> operador.processCallRequest(v, operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this))));//System.out.println("Item : " + k + " Count : " + v)
				for (Map.Entry<String, CallRequest> entry : listaLlamadasEnEspera.entrySet()) {
					//System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
					
					Operador operador = new Operador("Empleado: "+entry.getKey(), "Operador", 1);
					Supervisor supervisor = new Supervisor("Empleado "+entry.getKey(), "Supervisor", 2);
					Director director = new Director("Empleado "+entry.getKey(), "Director", 3);
	
					operador.setSuccessor(supervisor);
					supervisor.setSuccessor(director);
					
					ExecutorService servicePending = null;
					try {
						servicePending = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(callsAmount);
						Random random = new Random();
						Integer duracionLlamada = random.ints(5, 10).findFirst().getAsInt();
						String name = entry.getKey();//"Client " + String.valueOf(x);
						servicePending.submit(() -> operador.processCallRequest(entry.getValue(), operadoresDisponibles, supervisoresDisponibles, directoresDisponibles, this));
					} finally {
						if (servicePending != null) {
							servicePending.shutdown();
						}
					}
				}
			}else{
				System.out.println("NO HAY LLAMADAS EN COLA, FIN DE LA EJECUCIÓN !");
			}
			
			/* Debug log
			for (Map.Entry<String, CallRequest> entry : listaLlamadasEnEspera.entrySet()) {
				System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
			}*/
			
		}

		return true;
	}
}
