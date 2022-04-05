package com.brunocesargambeta.appassistencialogica.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NovaMetaActivity extends AppCompatActivity {

    private EditText campoValorMeta, campoQuantidadeTecnicos, campoDiasUteis;
    private Button buttonSalvar;
    private CalendarView calendarView;
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols (new Locale("pt", "BR"));
    DecimalFormat decimalFormat = new DecimalFormat ("#,##0.00", dfs);
    private Double valorMetaDiaria, valorAjustado, valorMetaTotal;
    private DatabaseReference firebaseRef;

    private List<Tecnicos> listaTecnicos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novas_metas);
        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Toolaar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lançamentos de Meta");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double valorMeta = Double.parseDouble(campoValorMeta.getText().toString());
                int quantidadeTecnicos = Integer.parseInt(campoQuantidadeTecnicos.getText().toString());
                int diasUteis = Integer.parseInt(campoDiasUteis.getText().toString());

                if (valorMeta >= 1) {
                    if (quantidadeTecnicos >= 1) {
                        if (diasUteis >= 1) {
                            validaSalvarMetas(valorMeta, quantidadeTecnicos, diasUteis);
                        } else {
                            ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Campo dias uteis nao informado!!");
                        }
                    } else {
                        ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Campo quantidade de tecnico nao informado!!");
                    }
                } else {
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Valor de meta inválido!");
                }
            }
        });

    }

    private void inicializarComponentes() {
        campoValorMeta = findViewById(R.id.editTextValorMeta);
        campoQuantidadeTecnicos = findViewById(R.id.editTextQtdTecnicos);
        campoDiasUteis = findViewById(R.id.editTextDiasUteis);
        buttonSalvar = findViewById(R.id.buttonSalvarMeta);
        calendarView = findViewById(R.id.calendarView);
    }


    private void validaSalvarMetas(Double valorMeta, int qtdTecnicos, int diasUteis) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Adicionar Metas");
        alertDialog.setMessage("Informe a senha para adicionar a meta aos técnicos");
        alertDialog.setCancelable(false);

        View viewEditText = getLayoutInflater().inflate(R.layout.dialog_campo_senha, null);
        final EditText editSenha = viewEditText.findViewById(R.id.editTextSenha);
        alertDialog.setView(viewEditText);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String senhaConfirmacao = "automa";
                if (editSenha.getText().toString().equals(senhaConfirmacao)) {
                    Metas metas = new Metas();
                    valorMetaTotal = valorMeta;
                    valorMetaDiaria = valorMeta / qtdTecnicos / diasUteis;
                    metas.setDiasUteis(diasUteis);
                    metas.setQtdTecnicos(qtdTecnicos);
                    metas.setValorMensal(valorMeta);
                    metas.setVigenciaMeta(ConfiguracaoApp.getMesAno());
                    metas.setValorDiario(0.00);
                    metas.setValorDiarioPorTecnico(valorMetaDiaria);
                    metas.salvarMetas();
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Metas salva com sucesso!!");
                    finish();
                } else {
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Senha inválida!!");
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Procedimento cancelado!!");
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }
}