package com.brunocesargambeta.appassistencialogica.model;

import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class Lancamentos {

    private String descricaoOS;
    private String idLancamento;
    private String numeroOS;
    private Double valorOS;
    private String idUsuarioLancamento;
    private String dataLancamento;


    public Lancamentos() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference lancamentoRef = firebaseRef
                .child("lancamentos");
        setIdLancamento(lancamentoRef.push().getKey());
        setIdUsuarioLancamento(UsuarioFirebase.getIdUsuario());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference lancamentoRef = firebaseRef.child("lancamentos")
                .child(UsuarioFirebase.getIdUsuario())
                .child(getDataLancamento().toString())
                .child(getIdLancamento());
        lancamentoRef.setValue(this);
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getIdLancamento() {
        return idLancamento;
    }

    public void setIdLancamento(String idLancamento) {
        this.idLancamento = idLancamento;
    }

    public String getDescricaoOS() {
        return descricaoOS;
    }

    public void setDescricaoOS(String descricaoOS) {
        this.descricaoOS = descricaoOS;
    }

    public String getNumeroOS() {
        return numeroOS;
    }

    public void setNumeroOS(String numeroOS) {
        this.numeroOS = numeroOS;
    }

    public Double getValorOS() {
        return valorOS;
    }

    public void setValorOS(Double valorOS) {
        this.valorOS = valorOS;
    }

    public String getIdUsuarioLancamento() {
        return idUsuarioLancamento;
    }

    public void setIdUsuarioLancamento(String idUsuarioLancamento) {
        this.idUsuarioLancamento = idUsuarioLancamento;
    }
}
