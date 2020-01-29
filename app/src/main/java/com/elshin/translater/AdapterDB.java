package com.elshin.translater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.elshin.translater.models.Translation;

import java.util.List;


public class AdapterDB extends RecyclerView.Adapter<AdapterDB.ViewHolder> {

    private List<Translation> translationList;

    public AdapterDB(List<Translation> translationList) {
        this.translationList = translationList;
    }

    @NonNull
    @Override
    public AdapterDB.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_db, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDB.ViewHolder viewHolder, int i) {
        viewHolder.sourceText.setText(translationList.get(i).getInputText());
        viewHolder.translatedText.setText(translationList.get(i).getTranslatedText());
    }

    @Override
    public int getItemCount() {
        return translationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView sourceText;
        TextView translatedText;

        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cvDB);
            sourceText = (TextView) itemView.findViewById(R.id.sourceText_db);
            translatedText = (TextView) itemView.findViewById(R.id.translatedText_db);
        }
    }
}
