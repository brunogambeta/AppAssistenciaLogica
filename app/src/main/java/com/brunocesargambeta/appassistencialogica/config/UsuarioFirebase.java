package com.brunocesargambeta.appassistencialogica.config;

import android.util.Log;

import androidx.annotation.NonNull;

import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Bruno Gambeta
 */

public class UsuarioFirebase {


    public static String getIdUsuario() {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static String getTipoUsuarioLogado() {
        final String[] tipoTecnico = {"A"};
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("tecnicos").child(UsuarioFirebase.getIdUsuario());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tecnicos tec = snapshot.getValue(Tecnicos.class);
                assert tec != null;
                tipoTecnico[0] = tec.getTipoTecnico();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return tipoTecnico[0];


    }
}