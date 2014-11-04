package com.hofmn.defineo.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hofmn.defineo.R;

import java.util.ArrayList;

public class DefinitionAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> definitions;

    public DefinitionAdapter(Activity context, ArrayList<String> definitions) {
        super(context, R.layout.definition_list_item, definitions);
        this.context = context;
        this.definitions = definitions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.definition_list_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.numberText = (TextView) rowView.findViewById(R.id.definitionNumberTextView);
            viewHolder.numberText.setTypeface(Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Bold.ttf"));
            viewHolder.definitionText = (TextView) rowView.findViewById(R.id.definitionTextView);
            viewHolder.contextText = (TextView) rowView.findViewById(R.id.contextTextView);
            viewHolder.contextText.setTypeface(Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Light.ttf"));

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String definition = definitions.get(position);
        String context = definitions.get(position);
        holder.contextText.setText("\"" + context.replace("definition", "context") + "\"");
        holder.definitionText.setText(definition);
        holder.numberText.setText(Integer.toString(position + 1) + ".");

        return rowView;
    }

    static class ViewHolder {
        public TextView numberText;
        public TextView definitionText;
        public TextView contextText;
    }
} 