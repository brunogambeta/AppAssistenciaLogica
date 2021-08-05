package com.brunocesargambeta.appassistencialogica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.adapter.AdapterLancamentos;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LancamentosActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerLancamentos;
    private AdapterLancamentos adapterLancamentos;
    private TextView textDataFiltro;
    private String idTecnicoSelecionado;
    private DatabaseReference firebaseRef;
    private DatabaseReference lancamentoRef;
    private List<Lancamentos> listaLancamentos = new ArrayList<>();
    private Lancamentos lancamento;
    private Double valorLancamento;
    private Double valorSaldoTecnico, valorLancadoTotal;
    private String diaAtual;
    private String dataFiltro;

    private Double valorSaldoMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamentos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Buscar saldo diario tecnico
        buscarSaldoDiarioTecnico();
        buscarSaldoDiarioMetas();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lançamentos");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idTecnicoSelecionado = (String) getIntent().getSerializableExtra("IDTecnico");

        //Configuracao RecyclerView
        recyclerLancamentos.setLayoutManager(new LinearLayoutManager(this));
        recyclerLancamentos.setHasFixedSize(true);
        adapterLancamentos = new AdapterLancamentos(listaLancamentos);
        recyclerLancamentos.setAdapter(adapterLancamentos);

        //Recuperar Lancamentos efetuados
        recuperarLancamentos(diaAtual, idTecnicoSelecionado);

        //Recuperar filtro de lancamento
        recuperarTelaFiltros();

        if (UsuarioFirebase.getIdUsuario().equals(idTecnicoSelecionado)) {
            fab.setVisibility(View.VISIBLE);
            swipe();
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LancamentosActivity.this, NovoLancamentoActivity.class);
                startActivity(i);
            }
        });
    }

    private void inicializarComponentes() {

        fab = findViewById(R.id.floatingActionButton);
        recyclerLancamentos = findViewById(R.id.recyclerLancamentos);
        diaAtual = ConfiguracaoApp.getDateTime();
        textDataFiltro = findViewById(R.id.textDataFiltro);
        textDataFiltro.setText(ConfiguracaoApp.getDateTime());
    }

    private void recuperarLancamentos(String data, String id) {

        DatabaseReference lancamentosRef = firebaseRef.child("lancamentos").child(id).child(data);
        lancamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaLancamentos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaLancamentos.add(ds.getValue(Lancamentos.class));
                    Log.i("Retorno", listaLancamentos.toString());
                }
                adapterLancamentos.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void excluirLancamento(RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Excluir Lançamento");
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir este lançamento?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                lancamento = listaLancamentos.get(position);
                lancamentoRef = firebaseRef.child("lancamentos").child(UsuarioFirebase.getIdUsuario()).child(diaAtual);
                lancamentoRef.child(lancamento.getIdLancamento()).removeValue();
                adapterLancamentos.notifyItemRemoved(position);
                atualizarValorDiario(lancamento.getValorOS());
                atualizarValorDiarioMeta(lancamento.getValorOS());
                ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Lançamento excluido com sucesso!");
                adapterLancamentos.notifyDataSetChanged();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Cancelado");
                adapterLancamentos.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void swipe() {

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirLancamento(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerLancamentos);
    }

    private void atualizarValorDiario(Double valor) {
        DatabaseReference tecnicoRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
        valorLancamento = valorSaldoTecnico - valor;
        if (valorLancamento <= 0) {
            valorLancamento = 0.0;
        }
        Double valornovo = valorLancadoTotal - valor;
        if (valornovo <= 0){
            valornovo = 0.00;
        }
        tecnicoRef.child("valorLancadoDiario").setValue(valorLancamento);
        tecnicoRef.child("valorLancadoTotal").setValue(valornovo);
        adapterLancamentos.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lancamentos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_datas:
                filtrarLancamentos();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void filtrarLancamentos() {
        Intent i = new Intent(LancamentosActivity.this, FiltrarLancamentosActivity.class);
        i.putExtra("idTecnico", idTecnicoSelecionado);
        startActivity(i);
    }

    private void buscarSaldoDiarioTecnico() {
        DatabaseReference tecRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
        tecRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tecnicos tec = snapshot.getValue(Tecnicos.class);
                Log.i("snap", "teste" + tec.getValorLancadoDiario());
                valorSaldoTecnico = tec.getValorLancadoDiario();
                valorLancadoTotal = tec.getValorLancadoTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void atualizarValorDiarioMeta(Double valor) {
        DatabaseReference metasRef = firebaseRef.child("metas").child(ConfiguracaoApp.getMesAno());
        Double valorAtualizado = valorSaldoMetas - valor;
        if (valorAtualizado < 0) {
            valorAtualizado = 0.0;
        }
        metasRef.child("valorDiario").setValue(valorAtualizado);
        adapterLancamentos.notifyDataSetChanged();
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

    private void recuperarTelaFiltros() {
        dataFiltro = (String) getIntent().getSerializableExtra("dataSelecionada");
        idTecnicoSelecionado = (String) getIntent().getSerializableExtra("IDTecnico");
        if (dataFiltro == null || dataFiltro.isEmpty()) {
            dataFiltro = ConfiguracaoApp.getDateTime();
            textDataFiltro.setText(dataFiltro);
        }
        if (dataFiltro.equals(ConfiguracaoApp.getDateTime())) {
            recuperarLancamentos(dataFiltro, idTecnicoSelecionado);
            textDataFiltro.setText(dataFiltro);
        } else {
            recuperarLancamentos(dataFiltro, idTecnicoSelecionado);
            textDataFiltro.setText(dataFiltro);
        }
    }
}