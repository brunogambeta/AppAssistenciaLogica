package com.brunocesargambeta.appassistencialogica.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NovoLancamentoActivity extends AppCompatActivity {

    private DatabaseReference firebaseRef;
    private EditText descricaoOS, numeroOS, valorOS;
    private Button buttonSalvarOS;
    private String dataAtualSistema, idTecnico, tipoTecnico, dataLancamento;
    private Double valorSaldoTecnico, valorTotalMeta;
    private final ArrayList<Lancamentos> listaOrdemServico = new ArrayList<>();
    private Double valorSaldoMetas;
    private final String id = "";
    private final String tipoPermitido = "A";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_lancamento);

        //Configuracoes iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        idTecnico = (String) getIntent().getSerializableExtra("IDTecnico");
        tipoTecnico = (String) getIntent().getSerializableExtra("tipoTecnicoLogado");
        dataLancamento = (String) getIntent().getSerializableExtra("dataLancamento");


        //Buscar Saldo tecnico
        buscarSaldoDiarioTecnico();
        buscarSaldoDiarioMetas();
        validaNumeroOs();


        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Lançamento");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSalvarOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String descricao = descricaoOS.getText().toString();
                String numero = numeroOS.getText().toString();
                Double valor = Double.parseDouble(valorOS.getText().toString());

                if (!descricao.isEmpty()) {
                    if (!numero.isEmpty()) {
                        if (valor > 0) {
                            Lancamentos lancamento = new Lancamentos();
                            lancamento.setDescricaoOS(descricao);
                            lancamento.setNumeroOS(numero);
                            lancamento.setValorOS(valor);
                            lancamento.setDataLancamento(dataLancamento);
                            lancamento.setIdUsuarioLancamento(idTecnico);

                            lancamento.salvar();
                            atualizarValorDiarioTecnico(valor);
                            atualizarValorDiarioMeta(valor);
                            ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Lançamento salvo com sucesso!");
                            finish();


                        } else {
                            ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Valor da OS não informado, verifique!");
                        }
                    } else {
                        ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Número da OS nnao informado, verifique");
                    }
                } else {
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Descrição não informada, verifique!");
                }
            }
        });
    }


    private void inicializarComponentes() {
        descricaoOS = findViewById(R.id.editTextDescricaoOS);
        numeroOS = findViewById(R.id.editTextNumeroOS);
        valorOS = findViewById(R.id.editTextValorOS);
        buttonSalvarOS = findViewById(R.id.buttonSalvarLancamento);
        dataAtualSistema = ConfiguracaoApp.getDateTime();
    }

    private void atualizarValorDiarioTecnico(Double valor) {
        DatabaseReference tecnicoRef = firebaseRef.child("tecnicos").child(idTecnico);
        Double valorAtualizado = valorSaldoTecnico + valor;
        Double valorNovo = valorTotalMeta + valor;
        tecnicoRef.child("valorLancadoDiario").setValue(valorAtualizado);
        tecnicoRef.child("valorLancadoTotal").setValue(valorNovo);
        tecnicoRef.child("dataUltimoLancamento").setValue(dataAtualSistema);

    }

    private void buscarSaldoDiarioTecnico() {
        try {

            DatabaseReference tecRef = firebaseRef.child("tecnicos").child(idTecnico);
            tecRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Tecnicos tec = snapshot.getValue(Tecnicos.class);
                    assert tec != null;
                    if (tec.getDataUltimoLancamento().equals(ConfiguracaoApp.getDateTime())) {
                        valorSaldoTecnico = tec.getValorLancadoDiario();
                    } else {
                        valorSaldoTecnico = 0.00;
                    }

                    valorTotalMeta = tec.getValorLancadoTotal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            Log.e("BuscaSaldoTecnicoNL", "Novo foi possivel carregar os dados do saldo do Tecnico");
        }
    }

    private void buscarSaldoDiarioMetas() {
        try {

            DatabaseReference metasRef = firebaseRef.child("metas").child(ConfiguracaoApp.getMesAno());
            metasRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Metas metas = snapshot.getValue(Metas.class);
                        valorSaldoMetas = metas.getValorDiario();
                    } catch (Exception e) {
                        ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Não foi possível carregar os valores de meta!");
                        finish();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Log.e("BuscaSaldoDiarioNL", "Não foi possivel carregar os valores de Metas");
        }
    }

    private void atualizarValorDiarioMeta(Double valor) {
        try {


            DatabaseReference metasRef = firebaseRef.child("metas").child(ConfiguracaoApp.getMesAno());
            Double valorAtualizado = valorSaldoMetas + valor;
            if (valorSaldoMetas.isNaN() || valorSaldoMetas != null) {
                metasRef.child("valorDiario").setValue(valorAtualizado);
            } else {
                Toast.makeText(getApplicationContext(), "Meta não informada, Verifique!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("atualizarValoresDiarios", "Não foi possivel salvar os valores diarios das metas");
        }


    }

    private void validaNumeroOs() {
        DatabaseReference osRef = firebaseRef.child("ordemServico");
        osRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    listaOrdemServico.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        listaOrdemServico.add(snapshot.getValue(Lancamentos.class));
                    }
                } catch (Exception e) {
                    Log.i("ErroTryCatch", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
