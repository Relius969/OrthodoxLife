package org.telegram.pravzhizn.ui.profile.saint;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.SaintObject;
import org.telegram.pravzhizn.ui.profile.saint.SaintsAdapter.Listener;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.List;


import static org.telegram.pravzhizn.utils.ViewUtils.createNoResultsView;

/**
 * Created by vlad on 9/8/16.
 */
public class SaintView extends LinearLayout implements OnEditorActionListener, Listener {

    private final RecyclerListView mListView;
    private final EditText mSaintNameText;
    private final SaintPresenter mPresenter;

    public SaintView(final Context context, final SaintPresenter presenter) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);

        mPresenter = presenter;

        mSaintNameText = buildStyledEditText(context);
        mSaintNameText.setHint(LocaleController.getString("pravzhizn_personal_saint_hint", R.string.pravzhizn_personal_saint_hint));
        mSaintNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                String text = mSaintNameText.getText().toString();
                presenter.saintFilterChanged(text);
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });


        mListView = configuredListView(context);

        final TextView emptyView = createNoResultsView(context, LocaleController.getString("pravzhizn_no_results", R.string.pravzhizn_no_saint_found));
        mListView.setEmptyView(emptyView);

        FrameLayout listViewContainer = new FrameLayout(context);
        listViewContainer.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listViewContainer.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        addView(mSaintNameText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));
        addView(listViewContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    @Override
    public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
        return false;
    }

    private RecyclerListView configuredListView(final Context context) {
        RecyclerListView listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= 11) {
            listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }

        return listView;
    }

    private EditText buildStyledEditText(final Context context) {
        EditText editText = new EditText(context);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setHintTextColor(0xff979797);
        editText.setTextColor(Color.BLACK);
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        AndroidUtilities.clearCursorDrawable(editText);

        return editText;
    }

    @Override
    public void onSaintClicked(final SaintObject saint) {
        mPresenter.saintClicked(saint);
    }

    public void displaySaints(final List<SaintObject> items) {
        mListView.setAdapter(new SaintsAdapter(items, SaintView.this));
    }

    public void setSaintEditTextEnabled(final boolean isEnabled) {
        mSaintNameText.setEnabled(isEnabled);
    }

    public void resume() {
        AndroidUtilities.showKeyboard(mSaintNameText);
    }
}
