package com.hofmn.defineo.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hofmn.defineo.R;
import com.hofmn.defineo.data.model.Translation;

import java.util.ArrayList;

public class TranslationAdapter extends ArrayAdapter<Translation> {
    private final Activity context;
    private final ArrayList<Translation> translations;

    public TranslationAdapter(Activity context, ArrayList<Translation> translations) {
        super(context, R.layout.translation_list_item, translations);
        this.context = context;
        this.translations = translations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.translation_list_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.numberText = (TextView) rowView.findViewById(R.id.translationNumberTextView);
            viewHolder.numberText.setTypeface(Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Bold.ttf"));
            viewHolder.translationText = (TextView) rowView.findViewById(R.id.translationTextView);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String translation = translations.get(position).getTranslation();
        holder.translationText.setText(translation);
        holder.numberText.setText(Integer.toString(position + 1) + ".");

        return rowView;
    }

    static class ViewHolder {
        public TextView numberText;
        public TextView translationText;
    }
} 