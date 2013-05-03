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
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class VistaJuego extends View implements SensorEventListener {

	SharedPreferences pref;
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

	// //// MISIL //////

	private Vector<Grafico> misiles;

	private static int PASO_VELOCIDAD_MISIL = 12;

	private Vector<Boolean> misilActivo;

	private int numMisiles = 5;

	private Vector<Integer> tiempoMisil;

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

		this.padre = (Juego) padre;

	}

	public VistaJuego(Context context, AttributeSet attrs) {

		super(context, attrs);

		Drawable drawableNave, drawableAsteroide, drawableMisil;

		pref = context.getSharedPreferences(
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

			ShapeDrawable dMisil = new ShapeDrawable(new RectShape());

			dMisil.getPaint().setColor(Color.WHITE);

			dMisil.getPaint().setStyle(Style.STROKE);

			dMisil.setIntrinsicWidth(15);

			dMisil.setIntrinsicHeight(3);

			drawableMisil = dMisil;

			setBackgroundColor(Color.BLACK);

		} else {

			drawableAsteroide = context.getResources().getDrawable(
					R.drawable.asteroide1);
			drawableNave = context.getResources().getDrawable(R.drawable.nave);

			drawableMisil = context.getResources().getDrawable(
					R.drawable.misil1);
		}

		nave = new Grafico(this, drawableNave);
		// misil = new Grafico(this, drawableMisil);
		misiles = new Vector<Grafico>();
		Asteroides = new Vector<Grafico>();

		for (int i = 0; i < numAsteroides; i++) {

			Grafico asteroide = new Grafico(this, drawableAsteroide);

			asteroide.setIncY(Math.random() * 4 - 2);

			asteroide.setIncX(Math.random() * 4 - 2);

			asteroide.setAngulo((int) (Math.random() * 360));

			asteroide.setRotacion((int) (Math.random() * 8 - 4));

			Asteroides.add(asteroide);

		}

		misilActivo = new Vector<Boolean>();
		tiempoMisil = new Vector<Integer>();
		for (int i = 0; i < numMisiles; i++) {

			Grafico misil = new Grafico(this, drawableMisil);
			misilActivo.add(false);
			tiempoMisil.add(1);
			misiles.add(misil);

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

		// Actualizamos posición de misil

		for (int i = 0; i < numMisiles; i++) {
			if (misilActivo.get(i)) {
				Grafico misil = misiles.get(i);
				misil.incrementaPos(retardo);

				int tiempoAux = tiempoMisil.get(i);

				tiempoAux -= retardo;
				tiempoMisil.set(i, tiempoAux);
				if (tiempoAux < 0) {
					misilActivo.set(i, false);
				} else {
					for (int j = 0; j < Asteroides.size(); j++) {

						if (misil.verificaColision(Asteroides.elementAt(j))) {

							destruyeAsteroide(j);
							
							misilActivo.set(i, false);

							break;

						}
					}

				}
			}

		}

	}

	private void destruyeAsteroide(int i) {

		Asteroides.remove(i);

		

	}

	private void ActivaMisil() {
		Grafico misil = null;
		int indice = 0;
		for (int i = 0; i < numMisiles; i++) {
			if (!misilActivo.get(i)) {
				misil = misiles.get(i);
				indice = i;
			}
		}
		if (misil != null) {
			misil.setPosX(nave.getPosX() + nave.getAncho() / 2
					- misil.getAncho() / 2);

			misil.setPosY(nave.getPosY() + nave.getAlto() / 2 - misil.getAlto()
					/ 2);

			misil.setAngulo(nave.getAngulo());

			misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo()))
					* PASO_VELOCIDAD_MISIL);

			misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo()))
					* PASO_VELOCIDAD_MISIL);

			tiempoMisil.set(indice, (int) Math.min(
					this.getWidth() / Math.abs(misil.getIncX()),
					this.getHeight() / Math.abs(misil.getIncY())) - 2);

			misilActivo.set(indice, true);
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
				if (pref.getString("controles", "-1").equals("1")) {
					giroNave = Math.round((x - mX) / 2);
				}
					disparo = false;
			} else if (dx < 6 && dy > 6) {
				if (pref.getString("controles", "-1").equals("1")) {
					aceleracionNave = Math.round((mY - y) / 25);
					if (aceleracionNave < 0)
						aceleracionNave = 0;
				}
					disparo = false;
			}

			break;

		case MotionEvent.ACTION_UP:

			giroNave = 0;

			aceleracionNave = 0;

			if (disparo) {

				ActivaMisil();

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

		for (int i = 0; i < numMisiles; i++) {
			if (misilActivo.get(i)) {
				Grafico misil = misiles.get(i);
				misil.dibujaGrafico(canvas);
			}

		}

	}

	@Override
	public boolean onKeyDown(int codigoTecla, KeyEvent evento) {

		super.onKeyDown(codigoTecla, evento);
		if (pref.getString("controles", "-1").equals("0")) {
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

				ActivaMisil();

				break;

			default:

				// Si estamos aquí, no hay pulsación que nos interese

				procesada = false;

				break;

			}

			return procesada;
		} else {
			return false;
		}

	}

	@Override
	public boolean onKeyUp(int codigoTecla, KeyEvent evento) {

		super.onKeyUp(codigoTecla, evento);
		if (pref.getString("controles", "-1").equals("0")) {
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
		} else {
			return false;
		}

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
		if (pref.getString("controles", "-1").equals("2")) {
			float valor = event.values[1];
			float valorAcel = event.values[2];

			if (!hayValorInicial) {

				valorInicial = valor;
				valorInicialAcel = valorAcel;

				hayValorInicial = true;

			}

			giroNave = (int) (valor - valorInicial) * 2;

			aceleracionNave = (float) ((valorAcel - valorInicialAcel) / 5.0);
			padre.actualizarEtiquetas(aceleracionNave, giroNave);
		}

	}
}