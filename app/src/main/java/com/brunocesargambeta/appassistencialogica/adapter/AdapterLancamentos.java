package com.brunocesargambeta.appassistencialogica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterLancamentos extends RecyclerView.Adapter<AdapterLancamentos.MyViewHolder> {

    private List<Lancamentos> listaLancamentos;

    public AdapterLancamentos(List<Lancamentos> listaLancamentos) {
        this.listaLancamentos = listaLancamentos;
    }

    @Override
    public AdapterLancamentos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lancamentos, parent, false);
        return new AdapterLancamentos.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLancamentos.MyViewHolder holder, int position) {
        Lancamentos lancamentos = listaLancamentos.get(position);
        holder.descricao.setText(lancamentos.getDescricaoOS());
        holder.numeroOS.setText(lancamentos.getNumeroOS());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String resultadoFormatado = decimalFormat.format( lancamentos.getValorOS() );
        holder.valorOS.setText("R$ " +resultadoFormatado);
    }

    @Override
    public int getItemCount() {
        return listaLancamentos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView descricao, numeroOS, valorOS;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textViewDadosDescricao);
            numeroOS = itemView.findViewById(R.id.textViewDadosNumeroOS);
            valorOS = itemView.findViewById(R.id.textViewDadosValorOS);

        }


    }

}
