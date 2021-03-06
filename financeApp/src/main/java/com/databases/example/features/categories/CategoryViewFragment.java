package com.databases.example.features.categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.databases.example.R;

public class CategoryViewFragment extends DialogFragment {
    public static CategoryViewFragment newInstance(int gPos, int cPos, int t) {
        CategoryViewFragment frag = new CategoryViewFragment();
        Bundle args = new Bundle();
        args.putInt("group", gPos);
        args.putInt("child", cPos);
        args.putInt("type", t);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int type = getArguments().getInt("type");
        final int groupPos = getArguments().getInt("group");
        final int childPos = getArguments().getInt("child");

        final LayoutInflater li = LayoutInflater.from(this.getActivity());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setCancelable(true);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            final View categoryStatsView = li.inflate(R.layout.subcategory_item, null);
            alertDialogBuilder.setView(categoryStatsView);

            Subcategory record = ((CategoriesActivity) getActivity()).getAdapterCategory().getSubCategory(groupPos, childPos);

            //Set Statistics
            TextView statsName = (TextView) categoryStatsView.findViewById(R.id.subcategory_name);
            statsName.setText(record.name);
            TextView statsValue = (TextView) categoryStatsView.findViewById(R.id.subcategory_parent);
            statsValue.setText(String.valueOf(record.catId));
            TextView statsDate = (TextView) categoryStatsView.findViewById(R.id.subcategory_note);
            statsDate.setText(record.note);
            TextView statsIsDefault = (TextView) categoryStatsView.findViewById(R.id.subcategory_is_default);
            statsIsDefault.setText(String.valueOf(record.isDefault));
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            final View categoryStatsView = li.inflate(R.layout.category_item, null);
            alertDialogBuilder.setView(categoryStatsView);

            Category record = ((CategoriesActivity) getActivity()).getAdapterCategory().getCategory(groupPos);

            //Set Statistics
            TextView statsName = (TextView) categoryStatsView.findViewById(R.id.category_name);
            statsName.setText(record.name);
            TextView statsDate = (TextView) categoryStatsView.findViewById(R.id.category_note);
            statsDate.setText(record.note);
            TextView statsIsDefault = (TextView) categoryStatsView.findViewById(R.id.category_is_default);
            statsIsDefault.setText(String.valueOf(record.isDefault));
        }

        return alertDialogBuilder.create();
    }
}
