package com.brunocesargambeta.appassistencialogica.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.brunocesargambeta.appassistencialogica.BuildConfig;
import com.brunocesargambeta.appassistencialogica.R;

public class SobreActivity extends AppCompatActivity {

    private TextView textVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sobre o Aplicativo");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textVersao = findViewById(R.id.textViewVersao);
        String versionName = BuildConfig.VERSION_NAME;
        textVersao.setText("Versão do app: "+versionName);

    }
}