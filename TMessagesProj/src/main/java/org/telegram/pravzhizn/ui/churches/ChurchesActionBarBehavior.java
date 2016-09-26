package org.telegram.pravzhizn.ui.churches;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vlad on 9/12/16.
 */
public class ChurchesActionBarBehavior {

    public static final int TRESHHOLD_TO_START_SEARCHING = 3;
    public static final int DELAY_TO_PERFORM_SEARCH = 300;
    private final Delegate mDelegate;

    public interface Delegate {

        void onBackClicked();

        void onSearchStarted();

        void onSearchFinished();

        void onSearchWithTerm(String term);
    }

    public ChurchesActionBarBehavior(final Delegate delegate) {
        mDelegate = delegate;
    }

    public void invoke(final Activity activity, final ActionBar actionBar) {
        final Delegate delegate = mDelegate;
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Techranch_Churches", R.string.Techranch_Churches));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    delegate.onBackClicked();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            private Timer performSearchTimer;

            @Override
            public void onSearchExpand() {
                delegate.onSearchStarted();
            }

            @Override
            public void onSearchCollapse() {
                cancelTimer();

                delegate.onSearchFinished();

            }

            @Override
            public void onTextChanged(EditText editText) {
                final String term = editText.getText().toString();

                if (!TextUtils.isEmpty(term) && term.length() >= TRESHHOLD_TO_START_SEARCHING) {
                    cancelTimer();

                    performSearchTimer = new Timer();
                    performSearchTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    delegate.onSearchWithTerm(term);
                                }
                            });
                        }
                    }, DELAY_TO_PERFORM_SEARCH);
                }

            }

            void cancelTimer() {
                if (performSearchTimer != null) {
                    performSearchTimer.cancel();
                    performSearchTimer = null;
                }
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
    }
}
