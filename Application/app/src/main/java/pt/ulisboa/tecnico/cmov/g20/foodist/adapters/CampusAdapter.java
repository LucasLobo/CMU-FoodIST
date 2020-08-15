package pt.ulisboa.tecnico.cmov.g20.foodist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.CampusLocation;

public class CampusAdapter extends ArrayAdapter<CampusLocation.Campus> {

    public CampusAdapter(Context context) {
        super(context, 0);
        super.add(CampusLocation.Campus.ALAMEDA);
        super.add(CampusLocation.Campus.TAGUS);
        super.add(CampusLocation.Campus.CTN);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text == null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).id);
        return text;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text == null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).id);

        return text;
    }
}
