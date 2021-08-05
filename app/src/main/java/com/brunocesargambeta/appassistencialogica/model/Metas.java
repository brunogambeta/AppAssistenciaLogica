package com.brunocesargambeta.appassistencialogica.model;

import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

public class Metas {

    private String idMeta;
    private Double valorMensal;
    private Double valorDiario;
    private String vigenciaMeta;
    private Double valorDiarioPorTecnico;
    private Integer diasUteis;
    private Integer qtdTecnicos;

    public Metas() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference metasRef = firebaseRef
                .child("metas");
        setIdMeta(metasRef.push().getKey());
    }

    public void salvarMetas(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference metasRef = firebaseRef
                .child("metas")
                .child(getVigenciaMeta());
        metasRef.setValue(this);
    }

    public Double getValorDiarioPorTecnico() {
        return valorDiarioPorTecnico;
    }

    public void setValorDiarioPorTecnico(Double valorDiarioPorTecnico) {
        this.valorDiarioPorTecnico = valorDiarioPorTecnico;
    }

    public String getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(String idMeta) {
        this.idMeta = idMeta;
    }

    public Double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(Double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public Double getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(Double valorDiario) {
        this.valorDiario = valorDiario;
    }

    public String getVigenciaMeta() {
        return vigenciaMeta;
    }

    public void setVigenciaMeta(String vigenciaMeta) {
        this.vigenciaMeta = vigenciaMeta;
    }

    public Integer getDiasUteis() {
        return diasUteis;
    }

    public void setDiasUteis(Integer diasUteis) {
        this.diasUteis = diasUteis;
    }

    public Integer getQtdTecnicos() {
        return qtdTecnicos;
    }

    public void setQtdTecnicos(Integer qtdTecnicos) {
        this.qtdTecnicos = qtdTecnicos;
    }
}