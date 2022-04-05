package com.brunocesargambeta.appassistencialogica.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brunocesargambeta.appassistencialogica.R;
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoApp;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AdapterListaTecnicos extends RecyclerView.Adapter<AdapterListaTecnicos.MyViewHolder> {

    private final List<Tecnicos> listaTecnicos;
    private List<Metas> listaMetas;
    DecimalFormatSymbols dfs = new DecimalFormatSymbols (new Locale("pt", "BR"));
    DecimalFormat decimalFormat = new DecimalFormat ("#,##0.00", dfs);


    public AdapterListaTecnicos(List<Tecnicos> tecnicos, List<Metas> listaMetas) {
        this.listaTecnicos = tecnicos;
        this.listaMetas = listaMetas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista_tecnicos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListaTecnicos.MyViewHolder holder, int position) {
        try {
            Tecnicos tecnico = listaTecnicos.get(position);
            Metas metas = listaMetas.get(listaMetas.size() - 1);
            holder.nomeTecnico.setText(tecnico.getNomeTecnico());
            String valorformatado = decimalFormat.format(tecnico.getValorLancadoTotal());
            if (metas.getVigenciaMeta().equals(ConfiguracaoApp.getMesAno())) {
                if (tecnico.getValorLancadoTotal() != null) {
                    holder.valorMeta.setText("R$ " + valorformatado);
                }
            } else {
                holder.valorMeta.setText("R$ 0,00");
            }
        }catch (Exception e){
            Log.e("erroListaTecnicos", "Não foi possivel carregar as informações - AdapterListatecnicos");
        }

        }

        @Override
        public int getItemCount () {
            return listaTecnicos.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView nomeTecnico;
            private TextView valorMeta;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                nomeTecnico = itemView.findViewById(R.id.textNomeTecnico);
                valorMeta = itemView.findViewById(R.id.textValorMeta);

            }

        }

}
