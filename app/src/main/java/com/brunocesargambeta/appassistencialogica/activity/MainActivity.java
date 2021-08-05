package com.brunocesargambeta.appassistencialogica.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.adapter.AdapterListaTecnicos;
import com.brunocesargambeta.appassistencialogica.adapter.AdapterTecnicos;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.brunocesargambeta.appassistencialogica.helper.RecyclerItemClickListener;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerView, recyclerViewListaTecnicos;
    private AdapterTecnicos adapterTecnicos;
    private AdapterListaTecnicos adapterListaTecnicos;
    private DatabaseReference firebaseRef;
    private TextView nomeTecnicoLogado, textMetaDiaria;
    private TextView textValorMetaGeral;
    private TextView textValorMetaAtingida,textDataAtual, textValorAtingido;
    private List<Tecnicos> listaTecnicos = new ArrayList<>();
    private List<Tecnicos> listaTecnicos2 = new ArrayList<>();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private List<Metas> listaMetas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configuracoes Iniciais
        inicializarComponentes();
        dataAtual();
        buscarMetaGeral();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tela Principal");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //Configuracao RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterTecnicos = new AdapterTecnicos(listaTecnicos, listaMetas);
        recyclerView.setAdapter(adapterTecnicos);

        //Configuracao RecyclerView
        recyclerViewListaTecnicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewListaTecnicos.setHasFixedSize(true);
        adapterListaTecnicos = new AdapterListaTecnicos(listaTecnicos2, listaMetas);
        recyclerViewListaTecnicos.setAdapter(adapterListaTecnicos);

        //Recupera Listagem de tecnicos
        recuperarListaTecnicos();
        buscarNomeTecnicoLogado();


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Tecnicos listadeTecnicos = listaTecnicos.get(position);
                Intent i = new Intent(MainActivity.this, LancamentosActivity.class);
                i.putExtra("NomeTecnico", listadeTecnicos.getNomeTecnico());
                i.putExtra("IDTecnico", listadeTecnicos.getIdTecnico());
                i.putExtra("valorDiario", listadeTecnicos.getValorLancadoDiario());
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));
    }

    //Metodo para inicializar os campos iniciais
    private void inicializarComponentes() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        recyclerView = findViewById(R.id.listaTecnicosHome);
        recyclerViewListaTecnicos = findViewById(R.id.recyclerViewValorTecnicos);
        nomeTecnicoLogado = findViewById(R.id.textNomeTecnicoLogado);
        textValorMetaAtingida = findViewById(R.id.textValorMetaAtingida);
        textValorMetaGeral = findViewById(R.id.textValorMetaGeral);
        textDataAtual = findViewById(R.id.textDataAtual);
        textMetaDiaria = findViewById(R.id.textValorMetaDiaria);
        textValorAtingido = findViewById(R.id.textValorAtingido);


        nomeTecnicoLogado.setText("Téc. Logado: Não Localizado");
    }

    //Metodo para fazer a consulta de tecnicos no Firebase
    private void recuperarListaTecnicos() {
        DatabaseReference listaTecnicosRef = firebaseRef.child("tecnicos");
        listaTecnicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTecnicos.clear();
                listaTecnicos2.clear();
                for (DataSnapshot tecnicos : snapshot.getChildren()) {
                    listaTecnicos.add(tecnicos.getValue(Tecnicos.class));
                    listaTecnicos2.add(tecnicos.getValue(Tecnicos.class));

                }
                adapterTecnicos.notifyDataSetChanged();
                adapterListaTecnicos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
                deslogarUsuario();
                break;
            case R.id.menu_metas:
                abrirtelaMetas();
                break;
            case R.id.menu_tecnicos:
                abrirTelaTecnicos();
                break;
            case R.id.sobre_app:
                abrirTelaSobre();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //Metodo para abrir a tela de cadastro de Técnicos
    private void abrirTelaTecnicos() {
        Intent intent = new Intent(MainActivity.this, TecnicosActivity.class);
        startActivity(intent);
    }

    //Metodo para deslogar o usuario
    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Metodo para abrir a tela de sobre do app
    private void abrirTelaSobre() {
        Intent i = new Intent(MainActivity.this, SobreActivity.class);
        startActivity(i);
    }

    //Metodo para abrir a tela de lancamentos de metas
    private void abrirtelaMetas() {
        Intent metas = new Intent(MainActivity.this, MetasActivity.class);
        startActivity(metas);
    }

    private void dataAtual(){
        textDataAtual.setText(ConfiguracaoApp.getDateTime());
    }

    private void buscarMetaGeral(){
        DatabaseReference metasRef = firebaseRef.child("metas");
        metasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMetas.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    listaMetas.add(ds.getValue(Metas.class));
                    Metas metas = ds.getValue(Metas.class);
                    if ( metas.getValorMensal() == null){
                        textValorMetaGeral.setText("Valor Meta Geral: R$ 0,00");
                        textValorMetaAtingida.setText("Valor Meta Atingida: ");
                        textMetaDiaria.setText("Valor Meta Diária: R$ 0,00");
                    }else{
                        String resultadoFormatado = decimalFormat.format( metas.getValorMensal());
                        textValorMetaGeral.setText("Valor Meta Geral: R$ "+ resultadoFormatado);

                        String resultadoAtingidoFormatado = decimalFormat.format( metas.getValorDiario());
                        if (metas.getValorDiario() >= metas.getValorDiarioPorTecnico()){
                           textValorAtingido.setText("R$ "+resultadoAtingidoFormatado);
                           textValorAtingido.setTextColor(Color.rgb(0,0,255));
                        }else{
                            textValorMetaAtingida.setText("Valor Meta Atingida:");
                            textValorAtingido.setText("R$ " +resultadoAtingidoFormatado);
                        }

                        String resultadoMetaDiaria = decimalFormat.format(metas.getValorDiarioPorTecnico());
                        textMetaDiaria.setText("Valor Meta Diária: R$ "+resultadoMetaDiaria);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscarNomeTecnicoLogado(){
      DatabaseReference tecnicosRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
      tecnicosRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot ds : snapshot.getChildren()){
                  Tecnicos tecnicos = snapshot.getValue(Tecnicos.class);
                  nomeTecnicoLogado.setText("Téc. Logado: "+tecnicos.getNomeTecnico());
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapterTecnicos.notifyDataSetChanged();
        adapterListaTecnicos.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterTecnicos.notifyDataSetChanged();
        adapterListaTecnicos.notifyDataSetChanged();
    }
}