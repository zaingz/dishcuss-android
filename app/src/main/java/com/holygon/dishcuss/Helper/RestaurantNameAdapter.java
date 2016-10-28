package com.holygon.dishcuss.Helper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.holygon.dishcuss.Model.RestaurantForStatus;
import com.holygon.dishcuss.Model.SearchRestaurant;
import com.holygon.dishcuss.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Naeem Ibrahim on 10/28/2016.
 */

public class RestaurantNameAdapter extends ArrayAdapter<SearchRestaurant> {

        Context context;
        int resource, textViewResourceId;
        List<SearchRestaurant> items, tempItems, suggestions;

        public RestaurantNameAdapter(Context context, int resource, int textViewResourceId, List<SearchRestaurant> items) {
            super(context, resource, textViewResourceId, items);
            this.context = context;
            this.resource = resource;
            this.textViewResourceId = textViewResourceId;
            this.items = items;
            tempItems = new ArrayList<SearchRestaurant>(items); // this makes the difference.
            suggestions = new ArrayList<SearchRestaurant>();
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.restaurant_row, parent, false);
        }
        SearchRestaurant people = items.get(position);
        if (people != null) {
        TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
        if (lblName != null)
            lblName.setText(people.getName());
        }
        return view;
        }

@Override
public Filter getFilter() {
        return nameFilter;
        }

        /**
         * Custom Filter implementation for custom suggestions we provide.
         */
        Filter nameFilter = new Filter() {
@Override
public CharSequence convertResultToString(Object resultValue) {
        String str = ((SearchRestaurant) resultValue).getName();
        return str;
        }

@Override
protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint != null) {
        suggestions.clear();
        for (SearchRestaurant people : tempItems) {
        if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
        suggestions.add(people);
        }
        }
        FilterResults filterResults = new FilterResults();
        filterResults.values = suggestions;
        filterResults.count = suggestions.size();
        return filterResults;
        } else {
        return new FilterResults();
        }
        }

@Override
protected void publishResults(CharSequence constraint, FilterResults results) {
        List<SearchRestaurant> filterList = (ArrayList<SearchRestaurant>) results.values;
        if (results != null && results.count > 0) {
        clear();
        for (SearchRestaurant people : filterList) {
        add(people);
        notifyDataSetChanged();
        }
        }
        }
        };

}
