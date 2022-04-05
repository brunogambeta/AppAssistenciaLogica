package com.brunocesargambeta.appassistencialogica.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DatabaseReference;

public class TecnicosActivity extends AppCompatActivity {

    private EditText nomeTecnico;
    private Spinner spinnerTipoTecnico;
    private Button botaoSalvarTecnico;


    //Variaveis para funcionalidade do Spinner.
    private ArrayAdapter<String> adapterTecnicos;
    private String[] tipoTecnicos;
    private String textTipoTecnico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnicos);

        //Configurações iniciais.
        inicializarComponentes();
        carregarDadosSpinner();


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
                    validarJogadores();
                    tecnicos.setIdTecnico(UsuarioFirebase.getIdUsuario());
                    tecnicos.setNomeTecnico(nome);
                    tecnicos.setValorLancadoDiario(0.00);
                    tecnicos.setValorLancadoDiario(0.00);
                    tecnicos.setValorLancadoTotal(0.00);
                    tecnicos.setDataUltimoLancamento(ConfiguracaoApp.getDateTime());
                    tecnicos.setTipoTecnico(textTipoTecnico);
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
        spinnerTipoTecnico = findViewById(R.id.spinnerTipoTecnico);
        textTipoTecnico = "V";

    }

    //Metodo para carregar os dados de quantidade de jogadores no spinner.
    private void carregarDadosSpinner() {

        //Configura spinner de estados
        tipoTecnicos = getResources().getStringArray(R.array.tipoTecnico);
        adapterTecnicos = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                tipoTecnicos
        );
        //Carrega o array no spinner.
        adapterTecnicos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoTecnico.setAdapter(adapterTecnicos);
    }

    //Metodo para validação de qual tipo de tecnico
    private void validarJogadores() {
        long posicao = spinnerTipoTecnico.getSelectedItemId();

        switch ((int) posicao) {
            case 0:
                textTipoTecnico = "A";
                break;
            case 1:
                textTipoTecnico = "T";
                break;
            case 2:
                textTipoTecnico = "O";
                break;
        }
    }


}