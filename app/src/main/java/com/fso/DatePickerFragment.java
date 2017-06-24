package com.fso;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by hoint on 20/06/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        return  dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //btnDate.setText(ConverterDate.ConvertDate(year, month + 1, day));
        month += 1;
        String sMonth ;
        if(month <10)
            sMonth = "0" + String.valueOf(month);
        else
            sMonth = String.valueOf(month);
        String sDate = String.valueOf(year) + sMonth + String.valueOf(day);
        ((MainActivity)getActivity()).getDataLocation(sDate);
        //mActivity.getDataLocation("20170617");
    }
}
