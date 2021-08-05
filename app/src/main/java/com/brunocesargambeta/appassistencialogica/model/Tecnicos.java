package com.brunocesargambeta.appassistencialogica.model;

import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Tecnicos {

    private String idTecnico;
    private String nomeTecnico;
    private Double valorLancadoDiario;
    private Double valorLancadoTotal;
    private String dataUltimoLancamento;


    public Tecnicos() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference tecnicoRef = firebaseRef
                .child("tecnicos");
        setIdTecnico(tecnicoRef.push().getKey());
    }

    public void salvarTecnico(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference tecnicoRef = firebaseRef.child("tecnicos").child(getIdTecnico());
        tecnicoRef.setValue(this);
    }

    public String getDataUltimoLancamento() {
        return dataUltimoLancamento;
    }

    public void setDataUltimoLancamento(String dataUltimoLancamento) {
        this.dataUltimoLancamento = dataUltimoLancamento;
    }

    public String getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(String idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public Double getValorLancadoDiario() {
        return valorLancadoDiario;
    }

    public void setValorLancadoDiario(Double valorLancadoDiario) {
        this.valorLancadoDiario = valorLancadoDiario;
    }


    public Double getValorLancadoTotal() {
        return valorLancadoTotal;
    }

    public void setValorLancadoTotal(Double valorLancadoTotal) {
        this.valorLancadoTotal = valorLancadoTotal;
    }
}
