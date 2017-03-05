package com.databases.example.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.databases.example.app.SearchActivity;

public class SearchWidget {
    public SearchWidget(final Context context, final SearchView abSearch) {
        final SearchView searchView = abSearch;
        searchView.setQueryHint("SearchActivity!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v(getClass().getSimpleName(), "newText=" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(getClass().getSimpleName(), "query=" + query);

                searchView.clearFocus();
                Intent intentSearch = new Intent(context, SearchActivity.class);
                intentSearch.putExtra(SearchActivity.QUERY_KEY, query);
                intentSearch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intentSearch);
                return true;
            }
        });
    }
}