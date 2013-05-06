package com.example.asteroides;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Juego extends Activity {

	 private VistaJuego vistaJuego;


	   @Override public void onCreate(Bundle savedInstanceState) {

	          super.onCreate(savedInstanceState);

	          setContentView(R.layout.juego);
	          VistaJuego vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
	          vistaJuego.setPadre(this);
	          vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
	   }
	   
	   public void actualizarEtiquetas(float aceleracion, int giro){
		   TextView giroLabel = (TextView) findViewById(R.id.giroLabel);
		   TextView aceleracionLabel= (TextView) findViewById(R.id.aceleracionLabel);
		   giroLabel.setText("g = " + giro);
		   aceleracionLabel.setText("a = " + aceleracion);
	   }
	   
	   @Override protected void onPause() {

		   super.onPause();

		   vistaJuego.getThread().pausar();

		}

		 

		@Override protected void onResume() {

		   super.onResume();

		   vistaJuego.getThread().reanudar();

		}

		 

		@Override protected void onDestroy() {

		   vistaJuego.getThread().detener();

		   super.onDestroy();

		}

	}