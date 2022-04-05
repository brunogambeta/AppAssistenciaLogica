package com.brunocesargambeta.appassistencialogica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.model.Lancamentos;
import com.brunocesargambeta.appassistencialogica.model.Metas;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AdapterMetas extends RecyclerView.Adapter<AdapterMetas.MyViewHolder> {

    private List<Metas> listaMetas;
    DecimalFormatSymbols dfs = new DecimalFormatSymbols (new Locale("pt", "BR"));
    DecimalFormat decimalFormat = new DecimalFormat ("#,##0.00", dfs);

    public AdapterMetas(List<Metas> listaMetas) {
        this.listaMetas = listaMetas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_metas, parent, false);
        return new AdapterMetas.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Metas metas = listaMetas.get(position);
        String valorFormatado = decimalFormat.format(metas.getValorDiarioPorTecnico());
        String valorMetaFormatado = decimalFormat.format(metas.getValorMensal());

        holder.valorMeta.setText("R$ " +valorMetaFormatado);
        holder.valorDiario.setText("R$ " +valorFormatado);
        holder.vigencia.setText(String.valueOf(metas.getVigenciaMeta()));
    }

    @Override
    public int getItemCount() {
        return listaMetas.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView valorMeta, valorDiario, vigencia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            valorDiario = itemView.findViewById(R.id.textValorDiario);
            valorMeta = itemView.findViewById(R.id.textValorMeta);
            vigencia = itemView.findViewById(R.id.textVigencia);

        }
    }
}