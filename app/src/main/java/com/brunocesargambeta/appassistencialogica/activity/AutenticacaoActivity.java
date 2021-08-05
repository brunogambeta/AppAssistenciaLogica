package com.brunocesargambeta.appassistencialogica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {

    private EditText editEmailCadastro, editSenhaCadastro;
    private Switch tipoLogin;
    private Button buttonAcessoCadastro;

    //Firebase
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        //Configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializaComponentes();

        //Verifica Usuario Logado
        verificarUsuarioLogado();

        buttonAcessoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailCadastro.getText().toString();
                String senha = editEmailCadastro.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //Cadastro usuario
                        if (tipoLogin.isChecked()) {
                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        exibiMensagem("Cadastro realizado com sucesso!");
                                        abrirTelaHome();
                                    } else {
                                        String erroCadastro = "";

                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroCadastro = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroCadastro = "Por favor, digite um email valido!";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroCadastro = "Email já utilizado para outro usuário!";
                                        } catch (Exception e) {
                                            erroCadastro = "ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        exibiMensagem("Erro: " + erroCadastro);
                                    }
                                }
                            });
                        } else {
                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        exibiMensagem("Usuário logado com sucesso!");
                                        abrirTelaHome();
                                    } else {
                                        String erro = "";
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            erro = "Erro: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        exibiMensagem(erro);
                                    }
                                }
                            });
                        }

                    } else {
                        exibiMensagem("Senha não informada, Verifique!");
                    }
                } else {
                    exibiMensagem("Email não informado, Verifique!");
                }
            }
        });

    }

    private void inicializaComponentes() {
        editEmailCadastro = findViewById(R.id.editEmailCadastro);
        editSenhaCadastro = findViewById(R.id.editSenhaCadastro);
        buttonAcessoCadastro = findViewById(R.id.buttonAcessoCadastro);
        tipoLogin = findViewById(R.id.switchTipoLogin);
    }

    private void exibiMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    private void abrirTelaHome() {
        Intent intent = new Intent(AutenticacaoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            abrirTelaHome();
        }
    }
}