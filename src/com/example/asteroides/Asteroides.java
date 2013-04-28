package com.example.asteroides;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Asteroides extends Activity implements OnGesturePerformedListener {

	private GestureLibrary libreria;
	private Button bAcercaDe;
	private Button bPunt;
	private Button bSalir;
	private Button bJugar;
	public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bAcercaDe = (Button) findViewById(R.id.button3);
		bAcercaDe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lanzarAcercaDe();

			}
		});
		bPunt = (Button) findViewById(R.id.button4);
		bPunt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lanzarPuntuaciones();

			}
		});
		bSalir = (Button) findViewById(R.id.button5);
		bSalir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		bJugar = (Button) findViewById(R.id.button1);
		bJugar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lanzarJuego();

			}
		});
		libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);

		if (!libreria.load()) {

			finish();

		}

		GestureOverlayView gesturesView =

		(GestureOverlayView) findViewById(R.id.gestures);

		gesturesView.addOnGesturePerformedListener(this);
	}

	protected void lanzarJuego() {
		Intent i = new Intent(this, Juego.class);

		startActivity(i);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.menu, menu);

		return true;
		/** true -> el menú ya está visible */

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.acercaDe:

			lanzarAcercaDe();

			break;
		case R.id.config:
			lanzarPreferencias();
			break;

		}

		return true;
		/** true -> consumimos el item, no se propaga */

	}

	public void lanzarAcercaDe() {

		Intent i = new Intent(this, AcercaDe.class);
		i.putExtra("nombre", "prueba de datos");
		startActivity(i);

	}

	public void lanzarPreferencias() {

		Intent i = new Intent(this, Preferencias.class);
		i.putExtra("nombre", "prueba de datos");
		startActivity(i);

	}

	public void lanzarPuntuaciones() {

		Intent i = new Intent(this, Puntuaciones.class);

		startActivity(i);

	}

	@Override
	public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {

		ArrayList<Prediction> predictions = libreria.recognize(gesture);

		if (predictions.size() > 0) {

			String comando = predictions.get(0).name;

			if (comando.equals("jugar")) {

				lanzarJuego();

			} else if (comando.equals("configurar")) {

				lanzarPreferencias();

			} else if (comando.equals("acerca_de")) {

				lanzarAcercaDe();

			} else if (comando.equals("cancelar")) {

				finish();

			}

		}

	}

}
