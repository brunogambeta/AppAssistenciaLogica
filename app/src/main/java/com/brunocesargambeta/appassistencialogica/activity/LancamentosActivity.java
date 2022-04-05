package com.brunocesargambeta.appassistencialogica.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String textDataFiltro;
    private String tipoTecnico;
    private String tipoTecnicoLogado = "X";
    private String idTecnicoSelecionado;
    private DatabaseReference firebaseRef;
    private DatabaseReference lancamentoRef, osRef;
    private List<Lancamentos> listaLancamentos = new ArrayList<>();
    private Lancamentos lancamento;
    private Double valorLancamento;
    private Double valorSaldoTecnico, valorLancadoTotal;
    private String diaAtual;
    private String dataFiltro, nomeTecnico;
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
        buscarDadosTecnicoLogado();
        configuraToolbar(textDataFiltro, nomeTecnico);


        //Configuracao RecyclerView
        recyclerLancamentos.setLayoutManager(new LinearLayoutManager(this));
        recyclerLancamentos.setHasFixedSize(true);
        adapterLancamentos = new AdapterLancamentos(listaLancamentos);
        recyclerLancamentos.setAdapter(adapterLancamentos);

        //Recuperar Lancamentos efetuados
        recuperarLancamentos(diaAtual, idTecnicoSelecionado);

        //Recuperar filtro de lancamento
        recuperarTelaFiltros();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LancamentosActivity.this, NovoLancamentoActivity.class);
                i.putExtra("IDTecnico", idTecnicoSelecionado);
                i.putExtra("TipoTecnico", tipoTecnico);
                i.putExtra("NomeTecnico", nomeTecnico);
                i.putExtra("dataLancamento", dataFiltro);
                startActivity(i);
            }
        });
    }

    private void inicializarComponentes() {
        fab = findViewById(R.id.floatingActionButton);
        recyclerLancamentos = findViewById(R.id.recyclerLancamentos);
        diaAtual = ConfiguracaoApp.getDateTime();
        textDataFiltro = ConfiguracaoApp.getDateTime();
        idTecnicoSelecionado = (String) getIntent().getSerializableExtra("IDTecnico");
        tipoTecnico = (String) getIntent().getSerializableExtra("TipoTecnico");
        nomeTecnico = (String) getIntent().getSerializableExtra("NomeTecnico");
    }

    private void recuperarLancamentos(String data, String id) {
        try {
            DatabaseReference lancamentosRef = firebaseRef.child("lancamentos").child(id).child(data);
            lancamentosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaLancamentos.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        listaLancamentos.add(ds.getValue(Lancamentos.class));
                    }
                    adapterLancamentos.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Log.e("errocarregarLancamentos", "Não foi possivel carregar os lançamentos");
        }
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
                lancamentoRef = firebaseRef.child("lancamentos").child(idTecnicoSelecionado).child(dataFiltro);
                lancamentoRef.child(lancamento.getIdLancamento()).removeValue();
                osRef = firebaseRef.child("ordemServico");
                osRef.child(lancamento.getNumeroOS()).removeValue();
                atualizarValorDiario(lancamento.getValorOS());
                atualizarValorDiarioMeta(lancamento.getValorOS());
                adapterLancamentos.notifyItemRemoved(position);
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
        try {
            DatabaseReference tecnicoRef = firebaseRef.child("tecnicos").child(idTecnicoSelecionado);
            valorLancamento = valorSaldoTecnico - valor;
            if (valorLancamento <= 0) {
                valorLancamento = 0.0;
            }
            Double valornovo = valorLancadoTotal - valor;
            if (valornovo <= 0) {
                valornovo = 0.00;
            }
            tecnicoRef.child("valorLancadoDiario").setValue(valorLancamento);
            tecnicoRef.child("valorLancadoTotal").setValue(valornovo);
            adapterLancamentos.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("erroCarregarValorDiario", "Não foi possivel carregar os valores diarios do tecnicos");
        }
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
        i.putExtra("TipoTecnico", tipoTecnico);
        i.putExtra("NomeTecnico", nomeTecnico);
        startActivity(i);
    }

    private void buscarSaldoDiarioTecnico() {
        try {
            DatabaseReference tecRef = firebaseRef.child("tecnicos").child(idTecnicoSelecionado);
            tecRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Tecnicos tec = snapshot.getValue(Tecnicos.class);
                    valorSaldoTecnico = tec.getValorLancadoDiario();
                    valorLancadoTotal = tec.getValorLancadoTotal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Log.e("erroCarregarValorDiario", "Não foi possivel carregar os valores diarios do tecnicos");
        }
    }

    private void atualizarValorDiarioMeta(Double valor) {
        try {
            DatabaseReference metasRef = firebaseRef.child("metas").child(ConfiguracaoApp.getMesAno());
            Double valorAtualizado = valorSaldoMetas - valor;
            if (valorAtualizado < 0) {
                valorAtualizado = 0.0;
            }
            metasRef.child("valorDiario").setValue(valorAtualizado);
            adapterLancamentos.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("erroCarregarValorDiario", "Não foi possivel carregar os valores diarios do tecnicos");
        }
    }

    private void buscarSaldoDiarioMetas() {
        try {

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
        } catch (Exception e) {
            Log.e("erroCarregarValorDiario", "Não foi possivel carregar os valores de meta");
        }
    }

    private void recuperarTelaFiltros() {
        dataFiltro = (String) getIntent().getSerializableExtra("dataSelecionada");
        idTecnicoSelecionado = (String) getIntent().getSerializableExtra("IDTecnico");
        tipoTecnico = (String) getIntent().getSerializableExtra("TipoTecnico");
        nomeTecnico = (String) getIntent().getSerializableExtra("NomeTecnico");

        if (dataFiltro == null || dataFiltro.isEmpty()) {
            dataFiltro = ConfiguracaoApp.getDateTime();
            configuraToolbar(dataFiltro, nomeTecnico);
        }
        if (dataFiltro.equals(ConfiguracaoApp.getDateTime())) {
            configuraToolbar(dataFiltro, nomeTecnico);
        } else {
            recuperarLancamentos(dataFiltro, idTecnicoSelecionado);
            configuraToolbar(dataFiltro, nomeTecnico);
        }
    }

    private void configuraToolbar(String data, String nome) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lançamentos");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle(data + " - Tec: " + nome);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void buscarDadosTecnicoLogado() {
        try {
            DatabaseReference tecnicosRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
            tecnicosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Tecnicos tecnicos = snapshot.getValue(Tecnicos.class);
                        tipoTecnicoLogado = tecnicos.getTipoTecnico();

                        Log.d("tipotecnicoLogado", tipoTecnicoLogado);
                        validacaoTela(tipoTecnicoLogado);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Log.e("erroCarregarValorDiario", "Não foi possivel carregar os valores de meta do tecnico");
        }
    }

    private void validacaoTela(String tipo) {
        String tecnicoPermitido = "A";
        Log.d("tipotecnico2", UsuarioFirebase.getIdUsuario() + "//" + idTecnicoSelecionado);

        if (UsuarioFirebase.getIdUsuario().equals(idTecnicoSelecionado) || tipoTecnicoLogado.equals(tecnicoPermitido)) {
            fab.setVisibility(View.VISIBLE);
            swipe();

        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }
}