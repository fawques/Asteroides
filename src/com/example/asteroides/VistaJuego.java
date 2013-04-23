package com.example.asteroides;

import java.util.Vector;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class VistaJuego extends View {

	// //// ASTEROIDES //////

	private Vector<Grafico> Asteroides; // Vector con los Asteroides

	private int numAsteroides = 5; // Número inicial de asteroides

	private int numFragmentos = 3; // Fragmentos en que se divide

	// //// NAVE //////

	private Grafico nave;// Gráfico de la nave

	private int giroNave; // Incremento de dirección

	private float aceleracionNave; // aumento de velocidad

	// Incremento estándar de giro y aceleración

	private static final int PASO_GIRO_NAVE = 5;

	private static final float PASO_ACELERACION_NAVE = 0.5f;

	public VistaJuego(Context context, AttributeSet attrs) {

		super(context, attrs);

		Drawable drawableNave, drawableAsteroide, drawableMisil;

		SharedPreferences pref = context.getSharedPreferences(
				"com.example.asteroides_preferences", Context.MODE_PRIVATE);

		if (pref.getString("graficos", "1").equals("0")) {
			desactivarAceleracionHardware();
			Path pathAsteroide = new Path();

			pathAsteroide.moveTo((float) 0.3, (float) 0.0);
			pathAsteroide.lineTo((float) 0.6, (float) 0.0);
			pathAsteroide.lineTo((float) 0.6, (float) 0.3);
			pathAsteroide.lineTo((float) 0.8, (float) 0.2);
			pathAsteroide.lineTo((float) 1.0, (float) 0.4);
			pathAsteroide.lineTo((float) 0.8, (float) 0.6);
			pathAsteroide.lineTo((float) 0.9, (float) 0.9);
			pathAsteroide.lineTo((float) 0.8, (float) 1.0);
			pathAsteroide.lineTo((float) 0.4, (float) 1.0);
			pathAsteroide.lineTo((float) 0.0, (float) 0.6);
			pathAsteroide.lineTo((float) 0.0, (float) 0.2);
			pathAsteroide.lineTo((float) 0.3, (float) 0.0);
			ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(
					pathAsteroide, 1, 1));

			dAsteroide.getPaint().setColor(Color.WHITE);
			dAsteroide.getPaint().setStyle(Style.STROKE);
			dAsteroide.setIntrinsicWidth(50);
			dAsteroide.setIntrinsicHeight(50);
			drawableAsteroide = dAsteroide;

			// Nave
			Path pathNave = new Path();
			pathNave.moveTo((float) 0.0, (float) 0.0);
			pathNave.lineTo((float) 1.0, (float) 0.5);
			pathNave.lineTo((float) 0.0, (float) 1.0);
			pathNave.lineTo((float) 0.0, (float) 0.0);
			ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1,
					1));
			dNave.getPaint().setColor(Color.YELLOW);
			dNave.getPaint().setStyle(Style.STROKE);
			dNave.getPaint().setStrokeWidth(0);
			dNave.setIntrinsicWidth(40);
			dNave.setIntrinsicHeight(15);
			drawableNave = dNave;

			setBackgroundColor(Color.BLACK);

		} else {

			drawableAsteroide = context.getResources().getDrawable(
					R.drawable.asteroide1);
			drawableNave = context.getResources().getDrawable(R.drawable.nave);
		}

		Asteroides = new Vector<Grafico>();
		nave = new Grafico(this, drawableNave);

		for (int i = 0; i < numAsteroides; i++) {

			Grafico asteroide = new Grafico(this, drawableAsteroide);

			asteroide.setIncY(Math.random() * 4 - 2);

			asteroide.setIncX(Math.random() * 4 - 2);

			asteroide.setAngulo((int) (Math.random() * 360));

			asteroide.setRotacion((int) (Math.random() * 8 - 4));

			Asteroides.add(asteroide);

		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void desactivarAceleracionHardware() {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	protected void onSizeChanged(int ancho, int alto, int ancho_anter,
			int alto_anter) {

		super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

		// Una vez que conocemos nuestro ancho y alto.

		for (Grafico asteroide : Asteroides) {
			nave.setPosX(ancho / 2);
			nave.setPosY(alto / 2);
			do {

				asteroide.setPosX(Math.random()
						* (ancho - asteroide.getAncho()));

				asteroide.setPosY(Math.random() * (alto - asteroide.getAlto()));

			} while (asteroide.distancia(nave) < (ancho + alto) / 5);

		}

	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		for (Grafico asteroide : Asteroides) {

			asteroide.dibujaGrafico(canvas);

		}
		nave.dibujaGrafico(canvas);

	}

}