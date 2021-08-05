package com.brunocesargambeta.appassistencialogica.config;

import android.app.AppComponentFactory;
import android.app.Application;
import android.content.Context;
import android.graphics.ColorSpace;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfiguracaoApp extends Application {



    public static void exibirMensagem(Context context, String texto) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getMesAno(){
        DateFormat mesAnoFormat = new SimpleDateFormat("MM-yyyy");
        Date mesAno = new Date();
        return mesAnoFormat.format(mesAno);
    }

    public static String getNomeUsuario(){

        return "Bruno";

    }



}
