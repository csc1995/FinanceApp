package com.databases.example.utils;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.databases.example.app.PlansActivity;
import com.databases.example.app.TransactionsFragment;

/**
 * Created by kwelsh on 3/5/17.
 */

public class DateUtils {
    private static final String TIME_PICKER_TAG = "timePicker";
    private static final String DATE_PICKER_TAG = "datePicker";


    //Method for selecting a Time when adding a transaction
    public static void showTimePickerDialog(AppCompatActivity appCompatActivity) {
        DialogFragment newFragment = new TransactionsFragment.TimePickerFragment();
        newFragment.show(appCompatActivity.getSupportFragmentManager(), TIME_PICKER_TAG);
    }

    //Method for selecting a Date when adding a transaction
    public static void showDatePickerDialog(AppCompatActivity appCompatActivity) {
        DialogFragment newFragment = new TransactionsFragment.DatePickerFragment();
        newFragment.show(appCompatActivity.getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    //Method for selecting a Date when adding a transaction
    public static void showDatePickerPlanDialog(AppCompatActivity appCompatActivity) {
        DialogFragment newFragment = new PlansActivity.DatePickerFragment();
        newFragment.show(appCompatActivity.getSupportFragmentManager(), DATE_PICKER_TAG);
    }
}
