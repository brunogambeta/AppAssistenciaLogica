package com.brunocesargambeta.appassistencialogica.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DatabaseReference;

public class TecnicosActivity extends AppCompatActivity {

    private EditText nomeTecnico;
    private Button botaoSalvarTecnico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnicos);

        //Configurações iniciais.
        inicializarComponentes();


        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro de Técnicos");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        botaoSalvarTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nomeTecnico.getText().toString();

                if (!nome.isEmpty()) {
                    Tecnicos tecnicos = new Tecnicos();
                    tecnicos.setIdTecnico(UsuarioFirebase.getIdUsuario());
                    tecnicos.setNomeTecnico(nome);
                    tecnicos.setValorLancadoDiario(0.00);
                    tecnicos.setValorLancadoDiario(0.00);
                    tecnicos.setValorLancadoTotal(0.00);
                    tecnicos.setDataUltimoLancamento(ConfiguracaoApp.getDateTime());
                    tecnicos.salvarTecnico();
                    finish();
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(),"Técnico salvo com sucesso!");
                } else {
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(),"Nome não informado, verifique!");
                }

            }
        });
    }

    private void inicializarComponentes() {
        nomeTecnico = findViewById(R.id.editNomeTecnico);
        botaoSalvarTecnico = findViewById(R.id.botaoSalvarTecnico);

    }

}