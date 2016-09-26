package org.telegram.pravzhizn.ui.profile.birthday;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.ui.Components.LayoutHelper;

import java.util.Calendar;
import java.util.Date;


import static org.telegram.pravzhizn.ui.profile.birthday.BirthdayDateDialogHelper.getDate;

/**
 * Created by vlad on 9/7/16.
 */
public class BirthdayView extends LinearLayout {

    private final EditText mBirthdayDay;
    private final EditText mBirthdayMonth;
    private final EditText mBirthdayYear;

    public BirthdayView(final Context context, final BirthdayPresenter presenter) {
        super(context);

        setOrientation(VERTICAL);

        mBirthdayDay = buildStyledEditText(context);
        mBirthdayDay.setHint(LocaleController.getString("pravzhizn_day", R.string.pravzhizn_day));
        addView(mBirthdayDay, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));

        mBirthdayMonth = buildStyledEditText(context);
        mBirthdayMonth.setHint(LocaleController.getString("pravzhizn_month", R.string.pravzhizn_month));
        addView(mBirthdayMonth, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));

        mBirthdayYear = buildStyledEditText(context);
        mBirthdayYear.setHint(LocaleController.getString("pravzhizn_year", R.string.pravzhizn_year));
        mBirthdayYear.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mBirthdayYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    presenter.onBirthdayDateSelected();
                    return true;
                }
                return false;
            }
        });
        addView(mBirthdayYear, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));


        Button button = new Button(context);
        final Calendar calendar = Calendar.getInstance();
        button.setText(LocaleController.getString("pravzhizn_calendar", R.string.pravzhizn_calendar));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                new DatePickerDialog(context, new OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker datePicker, final int year, final int month, final int dayOfMonth) {
                        mBirthdayYear.setText(String.valueOf(year));
                        mBirthdayMonth.setText(String.valueOf(month));
                        mBirthdayDay.setText(String.valueOf(dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 24, 24, 24, 0));


        PravzhiznConfig config = new PravzhiznConfig(context);
        if (config.isBirthdayDateSelected()) {
            Date date = config.getBirthdayDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            mBirthdayYear.setText(String.valueOf(cal.get(Calendar.YEAR)));
            mBirthdayMonth.setText(String.valueOf(cal.get(Calendar.MONTH)));
            mBirthdayDay.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        }
    }

    private EditText buildStyledEditText(final Context context) {
        EditText editText = new EditText(context);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setHintTextColor(0xff979797);
        editText.setTextColor(0xff212121);
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AndroidUtilities.clearCursorDrawable(editText);

        return editText;
    }

    public Date getCurrentDate() {

        try {
            int year = Integer.valueOf(mBirthdayYear.getText().toString().trim());
            int month = Integer.valueOf(mBirthdayMonth.getText().toString().trim());
            int day = Integer.valueOf(mBirthdayDay.getText().toString().trim());
            return getDate(year, month, day);
        } catch (NumberFormatException ex) {
            FileLog.e("tmessages", ex);
        }

        return null;
    }

}
