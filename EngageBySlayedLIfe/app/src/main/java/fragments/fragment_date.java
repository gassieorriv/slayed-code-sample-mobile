package fragments;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import java.text.DecimalFormat;
import java.util.Calendar;

import activities.ManageScheduleActivity;
import activities.MissingInformationActivity;
import activities.PersonalInformationActivity;

public class fragment_date extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        DecimalFormat formatter = new DecimalFormat("00");

        String value = datePicker.getYear() + "-" + formatter.format(datePicker.getMonth() + 1) + "-" + formatter.format(datePicker.getDayOfMonth());
        try {
            MissingInformationActivity activity = (MissingInformationActivity) getActivity();
            if(activity != null) {
                activity.getDate(value);
            }
        } catch(Exception ignore){ }
        try {
            PersonalInformationActivity activity = (PersonalInformationActivity) getActivity();
            if(activity != null) {
                activity.getDate(value);
            }
        } catch(Exception ignore){ }
        try {
            ManageScheduleActivity activity = (ManageScheduleActivity) getActivity();
            if(activity != null) {
                activity.getDate(value);
            }
        } catch(Exception ignore){
            String s = ignore.getMessage();
        }
    }
}
