package com.example.asteroides;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class Juego extends Activity {

	private VistaJuego vistaJuego;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.juego);
		vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
		vistaJuego.setPadre(this);

	}

	public void actualizarEtiquetas(float aceleracion, int giro) {
		TextView giroLabel = (TextView) findViewById(R.id.giroLabel);
		TextView aceleracionLabel = (TextView) findViewById(R.id.aceleracionLabel);
		giroLabel.setText("g = " + giro);
		aceleracionLabel.setText("a = " + aceleracion);
	}

	@Override
	protected void onPause() {

		super.onPause();
		vistaJuego.mSensorManager.unregisterListener(vistaJuego);
		vistaJuego.getThread().pausar();
		if (vistaJuego.mp.isPlaying())
			vistaJuego.mp.pause();

	}

	@Override
	protected void onResume() {

		super.onResume();

		List<Sensor> listSensors = vistaJuego.mSensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (!listSensors.isEmpty()) {

			Sensor orientationSensor = listSensors.get(0);

			vistaJuego.mSensorManager.registerListener(vistaJuego,
					orientationSensor,

					SensorManager.SENSOR_DELAY_GAME);
		}
		vistaJuego.getThread().reanudar();
		if(vistaJuego.music)
				vistaJuego.mp.start();

	}

	@Override
	protected void onDestroy() {

		vistaJuego.getThread().detener();
		if (vistaJuego.mp != null) {
			vistaJuego.mp.stop();
			vistaJuego.mp.release();
		}

		super.onDestroy();

	}

}