package org.telegram.pravzhizn.ui.churches;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse;
import org.telegram.pravzhizn.ui.add_church.AddChurch;
import org.telegram.pravzhizn.ui.churches.mode.IPresenter;
import org.telegram.pravzhizn.ui.churches.mode.list.ChurchesPresenter;
import org.telegram.pravzhizn.ui.churches.mode.list.ChurchesView;
import org.telegram.pravzhizn.ui.churches.mode.search.ChurchesSearchPresenter;
import org.telegram.pravzhizn.ui.details.ChurchDetails;
import org.telegram.pravzhizn.ui.select_city.OpenScenario;
import org.telegram.pravzhizn.ui.select_city.SelectCity;
import org.telegram.pravzhizn.ui.select_country.SelectCountry;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.DialogsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by vlad on 5/31/16.
 */
public class ChurchesActivity extends BaseFragment implements
        ChurchesActionBarBehavior.Delegate,
        ChurchesPresenterListener,
        NotificationCenter.NotificationCenterDelegate {

    public static final String SCREEN_NAME = "Churches list";
    private final OpenScenario mOpenScenario;

    private final PravzhiznService mService;
    private ChurchesView mView;
    private ChurchesPresenter mPresenter;
    private IPresenter mCurrentPresenter;
    private final ChurchesActionBarBehavior mActionBarBehavior = new ChurchesActionBarBehavior(this);
    private FrameLayout mRoot;

    public ChurchesActivity(OpenScenario openScenario) {
        mService = PravzhiznService.instance.create(PravzhiznService.class);
        mOpenScenario = openScenario;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);

        trackScreenName(SCREEN_NAME);

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        mActionBarBehavior.invoke(getParentActivity(), actionBar);

        mRoot = new FrameLayout(context);
        fragmentView = mRoot;

        mPresenter = new ChurchesPresenter(getParentActivity(), mService, this);

        mCurrentPresenter = mPresenter;

        return fragmentView;
    }

    void swapPresenter(final IPresenter newPresenter) {
        mCurrentPresenter.deactivate(mRoot);
        mCurrentPresenter = newPresenter;
        mCurrentPresenter.activate(getParentActivity(), mRoot);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentPresenter.activate(getParentActivity(), mRoot);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.locationPermissionGranted) {
            mCurrentPresenter.onLocationPermissionGranted();
        }
    }

    @Override
    public void onItemSelected(final RemoteChurch selectedChurch) {
        if (mOpenScenario != OpenScenario.InitialLaunch) {
            presentFragment(new ChurchDetails(selectedChurch));
        } else {
            processAddToMyChurchesDialog(selectedChurch);
        }
    }

    private void processAddToMyChurchesDialog(final RemoteChurch selectedChurch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("pravzhizn_add_to_my_churches", R.string.pravzhizn_add_to_my_churches));
        builder.setPositiveButton(LocaleController.getString("pravzhizn_add", R.string.pravzhizn_add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                addTempleToMyTemples(selectedChurch, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getParentActivity(), LocaleController.getString("techranch_church_is_added", R.string.techranch_church_is_added), Toast.LENGTH_LONG).show();
                        gotoDialogsActivity();
                    }
                });
            }
        });
        builder.setNegativeButton(LocaleController.getString("pravzhizn_no", R.string.pravzhizn_later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Toast.makeText(getParentActivity(), LocaleController.getString("techranch_add_later_in_my_churches", R.string.techranch_add_later_in_my_churches), Toast.LENGTH_LONG).show();
                gotoDialogsActivity();
            }
        });
        
        showDialog(builder.create());
    }

    private void addTempleToMyTemples(final RemoteChurch selectedChurch, final Runnable onFinished) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("techranch_sending_request", R.string.techranch_sending_request));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            mService.addMyTemple(selectedChurch.id).enqueue(new Callback<SimpleResponse.AddMyTempleResponse>() {
                @Override
                public void onResponse(final Call<SimpleResponse.AddMyTempleResponse> call, final Response<SimpleResponse.AddMyTempleResponse> response) {
                    dismissDialog(progressDialog);
                    onFinished.run();
                }

                @Override
                public void onFailure(final Call<SimpleResponse.AddMyTempleResponse> call, final Throwable t) {
                    dismissDialog(progressDialog);
                    onFinished.run();
                }
            });

        } catch (Exception e) {
            FileLog.e("tmessages", e);
            onFinished.run();
        }
    }

    void dismissDialog(ProgressDialog dialog) {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    @Override
    public void onLongClicked(final int position, final RemoteChurch selectedChurch) {
        // nothing to do
    }

    @Override
    public void onAddChurchClicked() {
        presentFragment(new AddChurch());
    }

    @Override
    public void onSelectCityClicked() {
        presentFragment(new SelectCity(OpenScenario.Common, new PravzhiznConfig(getParentActivity()).getSelectedCountry()));
    }

    @Override
    public void onSelectCountryClicked() {
        presentFragment(new SelectCountry(OpenScenario.Common));
    }

    private void gotoDialogsActivity() {
        presentFragment(new DialogsActivity(null), true);
    }

    @Override
    public void onBackClicked() {
        if (mOpenScenario == OpenScenario.InitialLaunch) {
            gotoDialogsActivity();
        } else {
            finishFragment();
        }
    }

    @Override
    public void onSearchStarted() {

    }

    @Override
    public void onSearchFinished() {
        swapPresenter(mPresenter);
    }

    @Override
    public void onSearchWithTerm(final String term) {
        final ChurchesSearchPresenter newPresenter = new ChurchesSearchPresenter(getParentActivity(), mService, ChurchesActivity.this);
        swapPresenter(newPresenter);
        newPresenter.query(term);
    }
}
