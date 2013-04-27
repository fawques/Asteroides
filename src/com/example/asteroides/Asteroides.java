package com.example.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class Asteroides extends Activity {

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
				lanzarAcercaDe(null);

			}
		});
		bPunt = (Button) findViewById(R.id.button4);
		bPunt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lanzarPuntuaciones(null);

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
		
		TextView titulo = (TextView) findViewById(R.id.Titulo);
		Button bConfig = (Button) findViewById(R.id.button2);
		
		Animation animacionTitulo = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
		Animation animacionBoton1 = AnimationUtils.loadAnimation(this,R.anim.aparecer);
		Animation animacionBoton2 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_derecha);
		
		
		titulo.startAnimation(animacionTitulo);
		bJugar.startAnimation(animacionBoton1);
		bConfig.startAnimation(animacionBoton2);
		
		

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

			lanzarAcercaDe(null);

			break;
		case R.id.config:
			lanzarPreferencias(null);
			break;

		}

		return true;
		/** true -> consumimos el item, no se propaga */

	}

	public void lanzarAcercaDe(View view) {
		Animation animacion = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
		bAcercaDe.startAnimation(animacion);
		Intent i = new Intent(this, AcercaDe.class);
		i.putExtra("nombre", "prueba de datos");
		startActivity(i);

	}

	public void lanzarPreferencias(View view) {

		Intent i = new Intent(this, Preferencias.class);
		i.putExtra("nombre", "prueba de datos");
		startActivity(i);

	}

	public void lanzarPuntuaciones(View view) {

		Intent i = new Intent(this, Puntuaciones.class);

		startActivity(i);

	}

}
