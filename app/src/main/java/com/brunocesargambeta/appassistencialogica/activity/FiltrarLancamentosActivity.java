package com.brunocesargambeta.appassistencialogica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;

public class FiltrarLancamentosActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView textData;
    private Button  buttonFiltrar;
    private String dataEscolhida;
    private String idTecnicoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar_lancamentos);

        //Configuracoes iniciais
        inicializarComponentes();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtrar Lançamentos");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        idTecnicoSelecionado = (String) getIntent().getSerializableExtra("idTecnico");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String ano = String.valueOf(year);
                String mes = String.valueOf(month);
                String dia = String.valueOf(dayOfMonth);

                if (dayOfMonth >=1 && dayOfMonth <=9){
                    dia = "0"+dayOfMonth;
                }

                if (month +1 == 1) {
                    mes = "01";
                }
                if (month +1 == 2 ){
                    mes = "02";
                }
                if (month +1 == 3) {
                    mes = "03";
                }
                if (month +1 == 4 ){
                    mes = "04";
                }
                if (month +1 == 5) {
                    mes = "05";
                }
                if (month +1 == 6 ){
                    mes = "06";
                }
                if (month +1 == 7) {
                    mes = "07";
                }
                if (month +1 == 8 ){
                    mes = "08";
                }
                if (month +1 == 9) {
                    mes = "09";
                }
                if (month +1 == 10 ){
                    mes = "10";
                }
                if (month +1 ==11) {
                    mes = "11";
                }
                if (month +1 == 12 ){
                    mes = "12";
                }
                dataEscolhida  = dia + "-" + mes + "-" + ano ;
                textData.setText(dataEscolhida);

            }
        });

        buttonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FiltrarLancamentosActivity.this, LancamentosActivity.class);
                i.putExtra("dataSelecionada", dataEscolhida);
                i.putExtra("IDTecnico", idTecnicoSelecionado);
                startActivity(i);
                finish();
            }
        });
    }



    private void inicializarComponentes() {
        calendarView = findViewById(R.id.calendarView2);
        textData = findViewById(R.id.textViewData);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
        textData.setText(ConfiguracaoApp.getDateTime());
    }
}