package com.example.asteroides;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Juego extends Activity {

	   @Override public void onCreate(Bundle savedInstanceState) {

	          super.onCreate(savedInstanceState);

	          setContentView(R.layout.juego);
	          VistaJuego vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
	          vistaJuego.setPadre(this);
	   }
	   
	   public void actualizarEtiquetas(float aceleracion, int giro){
		   TextView giroLabel = (TextView) findViewById(R.id.giroLabel);
		   TextView aceleracionLabel= (TextView) findViewById(R.id.aceleracionLabel);
		   giroLabel.setText("g = " + giro);
		   aceleracionLabel.setText("a = " + aceleracion);
	   }

	}