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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.adapter.AdapterMetas;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MetasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterMetas adapterMetas;
    private DatabaseReference firebaseRef;
    private FloatingActionButton fab;
    private Metas metas;
    private DatabaseReference metasRef;

    private List<Metas> listaMetas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);

        //Configuração iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        metasRef = ConfiguracaoFirebase.getFirebase();
        swipe();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Metas");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracao RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterMetas = new AdapterMetas(listaMetas);
        recyclerView.setAdapter(adapterMetas);

        //Recuperar Metas
        recuperarMetas();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MetasActivity.this, NovaMetaActivity.class);
                startActivity(i);
            }
        });
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerMetas);
        fab = findViewById(R.id.floatingActionButton);
    }

    private void recuperarMetas() {
        metasRef = firebaseRef.child("metas");
        metasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMetas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaMetas.add(ds.getValue(Metas.class));
                }
                adapterMetas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void excluirLancamento(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        metas = listaMetas.get(position);
        metasRef = firebaseRef.child("metas");
        metasRef.child(metas.getVigenciaMeta()).removeValue();
        adapterMetas.notifyItemRemoved(position);
        adapterMetas.notifyDataSetChanged();
        ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Lançamento excluido com sucesso!");
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
                validaExclusaoMetas(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    private void validaExclusaoMetas(RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Exclusão de Metas");
        alertDialog.setMessage("Informe a senha para excluir a meta");
        alertDialog.setCancelable(false);

        View viewEditText = getLayoutInflater().inflate(R.layout.dialog_campo_senha, null);
        final EditText editSenha = viewEditText.findViewById(R.id.editTextSenha);
        alertDialog.setView(viewEditText);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String senhaConfirmacao = "automa";
                if (editSenha.getText().toString().equals(senhaConfirmacao)) {
                    excluirLancamento(viewHolder);
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Metas excluîda com sucesso!!");
                    finish();
                    adapterMetas.notifyDataSetChanged();
                } else {
                    ConfiguracaoApp.exibirMensagem(getApplicationContext(), "Senha inválida!!");
                    adapterMetas.notifyDataSetChanged();
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