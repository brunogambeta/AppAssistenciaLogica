package com.brunocesargambeta.appassistencialogica.config;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfiguracaoApp extends Application {


    public static void exibirMensagem(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getMesAno() {
        DateFormat mesAnoFormat = new SimpleDateFormat("MM-yyyy");
        Date mesAno = new Date();
        return mesAnoFormat.format(mesAno);
    }



}
