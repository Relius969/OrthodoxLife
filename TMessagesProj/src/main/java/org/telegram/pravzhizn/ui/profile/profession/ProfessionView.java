package org.telegram.pravzhizn.ui.profile.profession;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.ui.Components.LayoutHelper;

/**
 * Created by vlad on 9/7/16.
 */
public class ProfessionView extends LinearLayout implements TextView.OnEditorActionListener {

    private final ProfessionPresenter mPresenter;

    private EditText mProfessionField;
    private TextView mCheckTextView;


    public ProfessionView(Context context, ProfessionPresenter presenter) {
        super(context);

        mPresenter = presenter;

        setOrientation(VERTICAL);

        mProfessionField = new EditText(context);
        mProfessionField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mProfessionField.setHintTextColor(0xff979797);
        mProfessionField.setTextColor(0xff212121);
        mProfessionField.setMaxLines(1);
        mProfessionField.setLines(1);
        mProfessionField.setPadding(0, 0, 0, 0);
        mProfessionField.setSingleLine(true);
        mProfessionField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        mProfessionField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        mProfessionField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mProfessionField.setHint(LocaleController.getString("pravzhizn_profession_placeholder", R.string.pravzhizn_profession_placeholder));
        AndroidUtilities.clearCursorDrawable(mProfessionField);
        mProfessionField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    mPresenter.onDoneClicked();
                    return true;
                }
                return false;
            }
        });

        mProfessionField.setText(new PravzhiznConfig(context).getProfession());

        addView(mProfessionField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));

        mCheckTextView = new TextView(context);
        mCheckTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mCheckTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(mCheckTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 12, 24, 0));

        mProfessionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mPresenter.onTextChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCheckTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
        return false;
    }

    public void setCheckTextViewVisibility(final int visibility) {
        mCheckTextView.setVisibility(visibility);
    }

    public void showCheckTextView() {
        setCheckTextViewVisibility(View.VISIBLE);
    }

    public void hideCheckTextView() {
        setCheckTextViewVisibility(View.GONE);
    }

    public void setCheckTextViewText(final String string) {
        mCheckTextView.setText(string);
    }

    public void setCheckTextColor(final int color) {
        mCheckTextView.setTextColor(color);
    }

    public String profession() {
        return mProfessionField.getText().toString();
    }
}
