package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class UserDietaryAdapter extends ArrayAdapter<FoodType> {

    public UserDietaryAdapter(Context context) {
        super(context, 0, FoodType.values());
    }

    public UserDietaryAdapter(Context context, List<FoodType> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text == null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).resourceId);
        return text;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text == null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).resourceId);

        return text;
    }
}
