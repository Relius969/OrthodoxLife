package org.telegram.pravzhizn.ui.profile.birthday;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by vlad on 9/7/16.
 */
public class BirthdayDateDialogHelper {

    private final Listener mListener;

    public interface Listener {

        void onSuccess();

        void onError();
    }

    public BirthdayDateDialogHelper(Listener listener) {
        mListener = listener;
    }

    public Dialog buildDialog(final Context context) {
        final Calendar calendar = Calendar.getInstance();

        final BirthdayPresenter presenter = new BirthdayPresenter(context, new BirthdayPresenter.Listener() {
            @Override
            public void onSuccess() {
                mListener.onSuccess();
            }

            @Override
            public void onError() {
                mListener.onError();
            }
        });

        return new DatePickerDialog(context, new OnDateSetListener() {
            @Override
            public void onDateSet(final DatePicker datePicker, final int year, final int month, final int dayOfMonth) {
                final Date date = getDate(year, month, dayOfMonth);
                presenter.onBirthdayDateSelected(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
