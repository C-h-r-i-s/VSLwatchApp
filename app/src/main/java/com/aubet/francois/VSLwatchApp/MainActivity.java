package com.aubet.francois.VSLwatchApp;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener  {
    Button btnCoPi, btnCoWifi, btnCoWeb, btnCoIPSEC, btnStop;
    private static final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
    static ConnectedFeedback theFeedback;
    static SocketManager sock;
    //static final String ipAddress = "10.180.27.117";
    //static final String ipAddress = "192.168.20.44";
    static final String ipAddress = "10.0.32.85";
    //static final String ipAddress = "192.168.178.48";
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mAcceleration;

    EditText ssid = null;
    EditText pass = null;

    State theState = new State();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


		System.out.println("trying");

		sock = new SocketManager(queue,ipAddress, 20009);
		sock.start();

        //sendCommand("getWIFI");
        try {
            Thread.sleep(100);
        }catch(InterruptedException e){}
        //System.out.println("rep:" + answer);

        /*try {
        sock.join();
        }catch(InterruptedException e){}*/

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//Used Sensor
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//Used Sensor
        mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);//Used Sensor


        btnCoPi = (Button) findViewById(R.id.button_copi);
        btnCoPi.setOnClickListener(this);

        btnCoWifi = (Button) findViewById(R.id.button_cowi);
        btnCoWifi.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.button_stop);
        btnStop.setOnClickListener(this);


        theFeedback = new ConnectedFeedback();
        theFeedback.start();

		System.out.println("end");
    }



    public void onClick(View v) {
            switch (v.getId()) {
            case R.id.button_copi:
                if(!State.connectedPi) {
                    sock.interrupt();
                    sock = new SocketManager(queue, ipAddress, 20009);
                    sock.start();
                }
                break;
            case R.id.button_cowi:
                    //sendCommand("Lumi:");

                    sendCommand(Double.toString((State.lightValue)));
                break;

                case R.id.button_stop:
                    sock.interrupt();
                    //com.aubet.francois.VSLwatchApp.State.connectedPi = false;
                    break;



            default:
                break;
            }
    }



    public static void onReceivedCommand(String com) {
        switch (com) {
            case "WIFIfalse":
                State.connectedWifi = false;
                break;
            case "lux":
                sendCommand("lux="+Double.toString(State.lightValue));
                break;


            case "accelerometer":
                sendCommand("accelerometer="+Double.toString((State.AcceleroxValue))+ "," + Double.toString((State.AcceleroyValue))+ "," + Double.toString((State.AccelerozValue)));
                break;

            case "gyroscope":
                sendCommand("acceleration="+Double.toString((State.GyroxValue))+ "," + Double.toString((State.GyroyValue))+ "," + Double.toString((State.GyrozValue)));

                break;
            case "accelero":
            case "acceleration":
                sendCommand("acceleration="+Double.toString((State.xValue))+ "," + Double.toString((State.yValue))+ "," + Double.toString((State.zValue)));
                break;
            case "all":
                sendCommand("all="+Double.toString((State.AcceleroxValue))+ "," + Double.toString((State.AcceleroyValue))+ "," + Double.toString((State.AccelerozValue))+ "," +Double.toString((State.GyroxValue))+ "," + Double.toString((State.GyroyValue))+ "," + Double.toString((State.GyrozValue))+ "," + Double.toString((State.xValue))+ "," + Double.toString((State.yValue))+ "," + Double.toString((State.zValue))+ "," +Double.toString(State.lightValue));
                break;
            default:
        }

    }

    private static void sendCommand(String com) {
        while (!queue.offer(com)) {
            queue.poll();
        }
    }



    	//+++++++++++++++++++++			sensor methods to show the connection feedbacks			+++++++++++++++++++++++

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                float accex = event.values[0];
                float accey = event.values[1];
                float accez = event.values[2];
                State.AcceleroxValue = accex;
                State.AcceleroyValue = accey;
                State.AccelerozValue = accez;

                break;

            case Sensor.TYPE_GYROSCOPE:
                float gyrx = event.values[0];
                float gyry = event.values[1];
                float gyrz = event.values[2];
                State.GyroxValue = gyrx;
                State.GyroyValue = gyry;
                State.GyrozValue = gyrz;

                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                float accx = event.values[0];
                float accy = event.values[1];
                float accz = event.values[2];
                State.xValue = accx;
                State.yValue = accy;
                State.zValue = accz;

                break;
            case Sensor.TYPE_LIGHT:
                float lightMeasure = event.values[0];
                State.lightValue = lightMeasure;

                break;

            default:
                break;
        }





    // Do something with this sensor data.
    }

    @Override
    protected void onResume() {
    // Register a listener for the sensor.
    super.onResume();
    mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
    // Be sure to unregister the sensor when the activity pauses.
    super.onPause();
    //mSensorManager.unregisterListener(this);//Nope soll weiter laufen
    }

    	//+++++++++++++++++++++			to show the connection feedbacks			+++++++++++++++++++++++
	private class ConnectedFeedback extends Thread {
		int i = 0;
		Instrumentation inst = new Instrumentation();


		public ConnectedFeedback() {
		}


		@Override
		public void run() {
			boolean a = false;

			while (true) {
				//onResume();

				try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(com.aubet.francois.VSLwatchApp.State.connectedPi) {
                                //btnCoIPSEC.setHighlightColor(Color.GREEN);
                                btnCoPi.setBackgroundColor(Color.GREEN);
                            } else {
                                btnCoPi.setBackgroundColor(Color.LTGRAY);
                            }
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(com.aubet.francois.VSLwatchApp.State.connectedWifi) {
                                btnCoWifi.setBackgroundColor(Color.GREEN);
                                btnCoWifi.setText(R.string.btn_cowi_done);
                            } else {
                                btnCoWifi.setBackgroundColor(Color.LTGRAY);
                            }
                        }
                    });



				} catch (Exception e) {}

				try {
					Thread.sleep(390);
				} catch (Exception e) {
				}

			}

		}

	}




}
