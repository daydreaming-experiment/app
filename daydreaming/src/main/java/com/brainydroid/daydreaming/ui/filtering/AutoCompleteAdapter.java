package com.brainydroid.daydreaming.ui.filtering;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.filtering.Filterer;
import com.brainydroid.daydreaming.ui.filtering.MetaString;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AutoCompleteAdapter implements Filterable, ListAdapter {

    private static String TAG = "AutoCompleteAdapter";

    @Inject private LayoutInflater inflater;
    @Inject private Context context;

    private ArrayList<MetaString> results = null;

    @Inject private Filterer filter;
    @Inject private HashSet<DataSetObserver> observers;
    @Inject private HashMap<Long,MetaString> idCache;

    public void initialize(ArrayList<String> possibilities) {
        Log.d(TAG, "Initializing");
        filter.initialize(this, possibilities);
    }

    public boolean areFilterResultsEmpty() {
        return results == null || results.size() == 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return !areFilterResultsEmpty();
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= getCount()) throw new ArrayIndexOutOfBoundsException();
        return !areFilterResultsEmpty();  // The "nothing found" item is not active
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        // If no results, we still have one item for "nothing found"
        if (areFilterResultsEmpty()) return 1;
        return results.size();
    }

    @Override
    public MetaString getItem(int position) {
        if (position >= getCount()) throw new ArrayIndexOutOfBoundsException();
        if (areFilterResultsEmpty()) {
            return MetaString.getInstance("Nothing found");
        } else {
            return results.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        MetaString item = getItem(position);
        long id = item.hashCode();
        if (!idCache.containsKey(id)) {
            idCache.put(id, item);
        }
        return id;
    }

    public MetaString getItemById(long id) {
        if (!idCache.containsKey(id)) {
            throw new RuntimeException("Item never shown in list, so id not cached");
        }
        return idCache.get(id);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null || !(convertView instanceof LinearLayout)) {
            layout = (LinearLayout)inflater.inflate(R.layout.question_auto_list_item_view,
                    parent, false);
        } else {
            layout = (LinearLayout)convertView;
        }

        MetaString item = getItem(position);
        TextView text = (TextView)layout.findViewById(R.id.question_auto_list_item_itemText);
        text.setText(item.getOriginal());
        TextView tagsView = (TextView)layout.findViewById(R.id.question_auto_list_item_itemTags);
        if (!areFilterResultsEmpty()) {
            text.setTextColor(context.getResources().getColor(android.R.color.black));
            String joinedTags = item.getJoinedTags();
            if (joinedTags == null) {
                tagsView.setVisibility(View.GONE);
            } else {
                tagsView.setVisibility(View.VISIBLE);
                tagsView.setText(joinedTags);
            }
        } else {
            tagsView.setVisibility(View.GONE);
            text.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        return layout;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        // We're never empty since we always have at least the "Nothing found." item
        return false;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setResults(ArrayList<MetaString> results) {
        Log.d(TAG, "Setting results");
        this.results = results;
        if (observers.size() > 0) {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }
    }

    public void addPossibility(String possibility) {
        filter.addPossibility(possibility);
    }
}
