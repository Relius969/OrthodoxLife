package org.telegram.pravzhizn.ui.profile;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.ui.profile.birthday.BirthdayPresenter;
import org.telegram.pravzhizn.ui.profile.profession.ProfessionPresenter;
import org.telegram.pravzhizn.ui.profile.profession.ProfessionPresenter.Listener;
import org.telegram.pravzhizn.ui.profile.saint.SaintPresenter;
import org.telegram.pravzhizn.ui.select_city.OpenScenario;
import org.telegram.pravzhizn.ui.select_country.SelectCountry;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.DialogsActivity;

import java.util.HashMap;
import java.util.Map;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by vlad on 9/7/16.
 */
public class PravzhiznLoginActivity extends BaseFragment {

    private static final String SCREEN_NAME = "Pravzhizn login";

    public enum LoginItem {
        Profession, Birthday, Saint;
    }

    private LoginItem mCurrentViewNum = LoginItem.Profession;

    private Map<LoginItem, SlideView> mViews = new HashMap<>();
    private ProgressDialog mProgressdialog;

    private final static int done_button = 1;

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

        for(SlideView view: mViews.values()) {
            if (view != null) {
                view.onDestroyActivity();
            }
        }

        if (mProgressdialog != null) {
            try {
                mProgressdialog.dismiss();
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
            mProgressdialog = null;
        }
    }

    @Override
    public View createView(final Context context) {
        actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == done_button) {
                    mViews.get(mCurrentViewNum).onNextPressed();
                } else if (id == -1) {
                    onBackPressed();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

        fragmentView = new ScrollView(context);
        ScrollView scrollView = (ScrollView) fragmentView;
        scrollView.setFillViewport(true);

        FrameLayout frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));

        mViews.put(LoginItem.Profession, new ChangeProfessionView(context));
        mViews.put(LoginItem.Birthday, new ChangeBirthdayView(context));
        mViews.put(LoginItem.Saint, new ChangeSaintView(context));

        for (View view : mViews.values()) {
            final boolean isFirst = view instanceof ChangeProfessionView;
            view.setVisibility(isFirst ? View.VISIBLE : View.GONE);
            frameLayout.addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, isFirst ? LayoutHelper.WRAP_CONTENT : LayoutHelper.MATCH_PARENT));
        }

        actionBar.setTitle(mViews.get(mCurrentViewNum).getHeaderName());

        new UpdatePravzhiznProfileStep(context).invoke(new UpdatePravzhiznProfileStep.Listener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(final Throwable t) {

            }
        });

        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
    }

    @Override
    public boolean onBackPressed() {
        mViews.get(mCurrentViewNum).onBackPressed();
        return true;
    }

    public void setPage(LoginItem page, boolean animated, Bundle params, boolean back) {
        if (animated) {
            final SlideView outView = mViews.get(mCurrentViewNum);
            final SlideView newView = mViews.get(page);
            mCurrentViewNum = page;
            actionBar.setBackButtonImage(newView.needBackButton() ? R.drawable.ic_ab_back : 0);

            newView.setParams(params);
            actionBar.setTitle(newView.getHeaderName());
            newView.onShow();
            newView.setX(back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x);
            outView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @SuppressLint("NewApi")
                @Override
                public void onAnimationEnd(Animator animator) {
                    outView.setVisibility(View.GONE);
                    outView.setX(0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x).start();
            newView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    newView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(0).start();
        } else {
            actionBar.setBackButtonImage(mViews.get(page).needBackButton() ? R.drawable.ic_ab_back : 0);
            mViews.get(mCurrentViewNum).setVisibility(View.GONE);
            mCurrentViewNum = page;
            mViews.get(page).setParams(params);
            mViews.get(page).setVisibility(View.VISIBLE);
            actionBar.setTitle(mViews.get(page).getHeaderName());
            mViews.get(page).onShow();
        }
    }

    private void needFinishActivity() {
        PravzhiznConfig config = new PravzhiznConfig(ApplicationLoader.applicationContext);
        if (!config.isCountrySelected()) {
            presentFragment(new SelectCountry(OpenScenario.InitialLaunch), true);
        } else {
            presentFragment(new DialogsActivity(null), true);
        }
    }

    public class ChangeProfessionView extends SlideView {

        private final ProfessionPresenter mPresenter;

        public ChangeProfessionView(final Context context) {
            super(context);

            mPresenter = new ProfessionPresenter(context, new Listener() {
                @Override
                public void onProfessionSaved() {
                    setPage(LoginItem.Birthday, true, null, false);
                }

                @Override
                public void onError() {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder.create());
                }
            });

            addView(mPresenter.view(), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        @Override
        public void onNextPressed() {
            mPresenter.onDoneClicked();
        }

        @Override
        public String getHeaderName() {
            return LocaleController.getString("pravzhizn_profession", R.string.pravzhizn_profession);
        }

        @Override
        public boolean needBackButton() {
            return true;
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            needFinishActivity();
        }
    }

    public class ChangeSaintView extends SlideView {

        private final SaintPresenter mPresenter;

        public ChangeSaintView(final Context context) {
            super(context);

            mPresenter = new SaintPresenter(context, new SaintPresenter.Listener() {

                @Override
                public void onSaintSaved() {
                    needFinishActivity();
                }

                @Override
                public void onError() {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder.create());
                }
            });

            addView(mPresenter.view(), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        @Override
        public void onNextPressed() {
            needFinishActivity();
        }

        @Override
        public String getHeaderName() {
            return LocaleController.getString("pravzhizn_personal_saint", R.string.pravzhizn_personal_saint);
        }

        @Override
        public boolean needBackButton() {
            return true;
        }

        @Override
        public void onBackPressed() {
            setPage(LoginItem.Birthday, true, null, true);
        }
    }

    public class ChangeBirthdayView extends SlideView {

        private final BirthdayPresenter mPresenter;

        public ChangeBirthdayView(final Context context) {
            super(context);

            mPresenter = new BirthdayPresenter(context, new BirthdayPresenter.Listener() {
                @Override
                public void onSuccess() {
                    setPage(LoginItem.Saint, true, null, false);
                }

                @Override
                public void onError() {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder.create());
                }
            });

            addView(mPresenter.view(), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        @Override
        public void onNextPressed() {
            mPresenter.onBirthdayDateSelected();
        }

        @Override
        public String getHeaderName() {
            return LocaleController.getString("pravzhizn_birthday_date", R.string.pravzhizn_birthday_date);
        }

        @Override
        public boolean needBackButton() {
            return true;
        }

        @Override
        public void onBackPressed() {
            setPage(LoginItem.Profession, true, null, true);
        }
    }

}
