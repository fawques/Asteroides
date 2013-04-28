package com.example.asteroides;

import java.util.List;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class VistaJuego extends View implements SensorEventListener {

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

	// //// THREAD Y TIEMPO //////

	// Thread encargado de procesar el juego

	private ThreadJuego thread = new ThreadJuego();

	// Cada cuanto queremos procesar cambios (ms)

	private static int PERIODO_PROCESO = 50;

	// Cuando se realizó el último proceso

	private long ultimoProceso = 0;

	private float mX = 0, mY = 0;

	private boolean disparo = false;
	private Juego padre;

	public void setPadre(Activity padre) {

		this.padre = (Juego)padre;

	}

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

		SensorManager mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> listSensors = mSensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (!listSensors.isEmpty()) {

			Sensor orientationSensor = listSensors.get(0);

			mSensorManager.registerListener(this, orientationSensor,

			SensorManager.SENSOR_DELAY_GAME);
		}

	}

	protected synchronized void actualizaFisica() {

		long ahora = System.currentTimeMillis();

		// No hagas nada si el período de proceso no se ha cumplido.

		if (ultimoProceso + PERIODO_PROCESO > ahora) {

			return;

		}

		// Para una ejecución en tiempo real calculamos retardo

		double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;

		ultimoProceso = ahora; // Para la próxima vez

		// Actualizamos velocidad y dirección de la nave a partir de
		// giroNave y aceleracionNave (según la entrada del jugador)

		nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));

		double nIncX = nave.getIncX() + aceleracionNave
				* Math.cos(Math.toRadians(nave.getAngulo())) * retardo;

		double nIncY = nave.getIncY() + aceleracionNave
				* Math.sin(Math.toRadians(nave.getAngulo())) * retardo;

		// Actualizamos si el módulo de la velocidad no excede el máximo

		if (Math.hypot(nIncX, nIncY) <= Grafico.getMaxVelocidad()) {

			nave.setIncX(nIncX);

			nave.setIncY(nIncY);

		}

		// Actualizamos posiciones X e Y

		nave.incrementaPos(retardo);

		for (Grafico asteroide : Asteroides) {

			asteroide.incrementaPos(retardo);

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);

		float x = event.getX();

		float y = event.getY();

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			disparo = true;

			break;

		case MotionEvent.ACTION_MOVE:

			float dx = Math.abs(x - mX);

			float dy = Math.abs(y - mY);

			if (dy < 6 && dx > 6) {

				giroNave = Math.round((x - mX) / 2);

				disparo = false;

			} else if (dx < 6 && dy > 6) {

				aceleracionNave = Math.round((mY - y) / 25);
				if (aceleracionNave < 0)
					aceleracionNave = 0;

				disparo = false;

			}

			break;

		case MotionEvent.ACTION_UP:

			giroNave = 0;

			aceleracionNave = 0;

			if (disparo) {

				// ActivaMisil();

			}

			break;

		}

		mX = x;
		mY = y;

		return true;

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

		ultimoProceso = System.currentTimeMillis();

		thread.start();

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		for (Grafico asteroide : Asteroides) {

			asteroide.dibujaGrafico(canvas);

		}
		nave.dibujaGrafico(canvas);

	}

	@Override
	public boolean onKeyDown(int codigoTecla, KeyEvent evento) {

		super.onKeyDown(codigoTecla, evento);

		// Suponemos que vamos a procesar la pulsación

		boolean procesada = true;

		switch (codigoTecla) {

		case KeyEvent.KEYCODE_DPAD_UP:

			aceleracionNave = +PASO_ACELERACION_NAVE;

			break;

		case KeyEvent.KEYCODE_DPAD_LEFT:

			giroNave = -PASO_GIRO_NAVE;

			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			giroNave = +PASO_GIRO_NAVE;

			break;

		case KeyEvent.KEYCODE_DPAD_CENTER:

		case KeyEvent.KEYCODE_ENTER:

			// ActivaMisil();

			break;

		default:

			// Si estamos aquí, no hay pulsación que nos interese

			procesada = false;

			break;

		}

		return procesada;

	}

	@Override
	public boolean onKeyUp(int codigoTecla, KeyEvent evento) {

		super.onKeyUp(codigoTecla, evento);

		// Suponemos que vamos a procesar la pulsación

		boolean procesada = true;

		switch (codigoTecla) {

		case KeyEvent.KEYCODE_DPAD_UP:

			aceleracionNave = 0;

			break;

		case KeyEvent.KEYCODE_DPAD_LEFT:

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			giroNave = 0;

			break;

		default:

			// Si estamos aquí, no hay pulsación que nos interese

			procesada = false;

			break;

		}

		return procesada;

	}

	class ThreadJuego extends Thread {

		@Override
		public void run() {

			while (true) {

				actualizaFisica();

			}

		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private boolean hayValorInicial = false;

	private float valorInicial;
	private float valorInicialAcel;

	@Override
	public void onSensorChanged(SensorEvent event) {

		float valor = event.values[1];
		float valorAcel = event.values[2];

		if (!hayValorInicial) {

			valorInicial = valor;
			valorInicialAcel = valorAcel;

			hayValorInicial = true;

		}

		giroNave = (int) (valor - valorInicial) * 2;

		aceleracionNave = (float) ((valorAcel - valorInicialAcel) / 5.0);
		padre.actualizarEtiquetas(aceleracionNave,giroNave);

	}
}