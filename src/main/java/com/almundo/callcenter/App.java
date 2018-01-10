package com.almundo.callcenter;

import com.almundo.callcenter.ctrl.Dispatcher;

public class App{
	public static void main( String[] args ){
		Dispatcher callHandler = new Dispatcher();
    	callHandler.dispatchCall(20);
    }
}
