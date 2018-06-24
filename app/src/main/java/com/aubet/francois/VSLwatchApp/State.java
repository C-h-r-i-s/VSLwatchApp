package com.aubet.francois.VSLwatchApp;

/**
 * Created by root on 31.10.17.
 */

public class State {
	public static boolean connectedPi = false;
    public static boolean connectedWifi = false;
	public static double lightValue = 0.0;
	public static double AcceleroxValue = 0.0;
	public static double AcceleroyValue = 0.0;
	public static double AccelerozValue = 0.0;

	public static double GyroxValue = 0.0;
	public static double GyroyValue = 0.0;
	public static double GyrozValue = 0.0;

	public static double xValue = 0.0;
	public static double yValue = 0.0;
	public static double zValue = 0.0;


	public State(){
		connectedPi = false;
		connectedWifi = false;
	}


	public static void connectionLost(){
		connectedPi = false;
		connectedWifi = false;
	}

}
