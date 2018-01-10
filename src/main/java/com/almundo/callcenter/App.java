package com.almundo.callcenter;

import com.almundo.callcenter.ctrl.Dispatcher;
/**
 * Clase principal de la aplicación
 * @author Diego
 *
 */
public class App{
	public static void main( String[] args ){
		// Se crea una instancia de Dispatcher, clase encargada de generar las llamadas concurrentes
		Dispatcher callHandler = new Dispatcher();
		// Se ejecuta el método que genera las llamadas concurrentes y las procesa según sea el tipo de empleado
		// recibe un entero con el número de llamadas concurrentes que se ejecutarán de manera concurrente.
    	callHandler.dispatchCall(21);
    }
}
