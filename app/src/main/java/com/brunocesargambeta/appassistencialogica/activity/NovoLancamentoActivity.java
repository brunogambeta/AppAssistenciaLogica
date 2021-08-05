package com.brunocesargambeta.appassistencialogica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NovoLancamentoActivity extends AppCompatActivity {

    private DatabaseReference firebaseRef;
    private EditText descricaoOS, numeroOS, valorOS;
    private Button buttonSalvarOS;
    private String dataAtualSistema;
    private Double valorSaldoTecnico, valorTotalMeta;
    private Double valorSaldoMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_lancamento);

        //Configuracoes iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Buscar Saldo tecnico
        buscarSaldoDiarioTecnico();
        buscarSaldoDiarioMetas();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Lançamento");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                            lancamento.setDataLancamento(dataAtualSistema);
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
        DatabaseReference tecnicoRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
        Double valorAtualizado = valorSaldoTecnico + valor;
        Double valorNovo = valorTotalMeta + valor;
        tecnicoRef.child("valorLancadoDiario").setValue(valorAtualizado);
        tecnicoRef.child("valorLancadoTotal").setValue(valorNovo);
        tecnicoRef.child("dataUltimoLancamento").setValue(dataAtualSistema);

    }

    private void buscarSaldoDiarioTecnico() {
        DatabaseReference tecRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
        tecRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tecnicos tec = snapshot.getValue(Tecnicos.class);
                assert tec != null;
                if (tec.getDataUltimoLancamento().equals(ConfiguracaoApp.getDateTime())){
                    valorSaldoTecnico = tec.getValorLancadoDiario();
                }else{
                    valorSaldoTecnico = 0.00;
                }

                    valorTotalMeta = tec.getValorLancadoTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscarSaldoDiarioMetas() {
        DatabaseReference metasRef = firebaseRef.child("metas");
        metasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Metas metas = ds.getValue(Metas.class);
                    assert metas != null;
                    valorSaldoMetas = metas.getValorDiario();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void atualizarValorDiarioMeta(Double valor) {
        DatabaseReference metasRef = firebaseRef.child("metas").child(ConfiguracaoApp.getMesAno());
        Double valorAtualizado = valorSaldoMetas + valor;
        metasRef.child("valorDiario").setValue(valorAtualizado);

    }

}