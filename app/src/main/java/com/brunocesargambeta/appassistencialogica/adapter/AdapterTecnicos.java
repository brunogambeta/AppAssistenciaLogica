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
import com.brunocesargambeta.appassistencialogica.config.ConfiguracaoFirebase;
import com.brunocesargambeta.appassistencialogica.model.Metas;
import com.brunocesargambeta.appassistencialogica.model.Tecnicos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AdapterTecnicos extends RecyclerView.Adapter<AdapterTecnicos.MyViewHolder> {

    private final List<Tecnicos> listaTecnicos;
    DecimalFormatSymbols dfs = new DecimalFormatSymbols (new Locale("pt", "BR"));
    DecimalFormat decimalFormat = new DecimalFormat ("#,##0.00", dfs);
    private final List<Metas> listaMetas;

    public AdapterTecnicos(List<Tecnicos> tecnicos, List<Metas> listaMetas) {
        this.listaTecnicos = tecnicos;
        this.listaMetas = listaMetas;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tecnicos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTecnicos.MyViewHolder holder, int position) {
        try{


        Tecnicos tecnico = listaTecnicos.get(position);
        Metas metas = listaMetas.get(listaMetas.size() -1);
        String resultadoFormatado = decimalFormat.format(tecnico.getValorLancadoDiario());
        holder.nomeTecnico.setText("" + tecnico.getNomeTecnico());
        if (tecnico.getDataUltimoLancamento().equals(ConfiguracaoApp.getDateTime())) {
            holder.valorMeta.setText("R$ " + resultadoFormatado);
            if (metas.getVigenciaMeta().equals(ConfiguracaoApp.getMesAno())){
                if (tecnico.getValorLancadoDiario() >= metas.getValorDiarioPorTecnico()) {
                    holder.valorMeta.setTextColor(Color.rgb(0, 0, 255));
                } else {
                    holder.valorMeta.setTextColor(Color.rgb(255, 0, 0));
                }
            }else {
                holder.valorMeta.setText("R$ 0,00");
                holder.valorMeta.setTextColor(Color.rgb(255, 0, 0));
            }

        }else{
            holder.valorMeta.setText("R$ 0,00");
            holder.valorMeta.setTextColor(Color.rgb(255, 0, 0));
        }

        }catch (Exception e){
            Log.e("erroAdapterTecnicos", "Não foi possivel carregar os dados dos tecnicos - AdapterTecnicos.");
        }

    }

    @Override
    public int getItemCount() {
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