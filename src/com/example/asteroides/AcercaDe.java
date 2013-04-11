package com.example.asteroides;

import android.app.Activity;

import android.os.Bundle;



public class AcercaDe extends Activity {

   /** Called when the activity is first created. */

   @Override public void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       Bundle extras = getIntent().getExtras();
       String nombre = extras.getString("nombre");
       // ya podemos usar el nombre que nos han pasado
       setContentView(R.layout.acercade);
   }

}