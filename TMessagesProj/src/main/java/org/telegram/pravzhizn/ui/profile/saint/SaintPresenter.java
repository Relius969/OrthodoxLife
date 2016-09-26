package org.telegram.pravzhizn.ui.profile.saint;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.SaintObject;
import org.telegram.pravzhizn.pravzhizn.responses.ProfileResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SaintsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 9/8/16.
 */
public class SaintPresenter {

    public void onResume() {
        mView.resume();
    }

    public interface Listener {
        void onSaintSaved();

        void onError();
    }

    private final Context mContext;
    private final Listener mListener;
    private final SaintView mView;
    private final PravzhiznService mService;

    public View view() {
        return mView;
    }


    public SaintPresenter(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
        mView = new SaintView(context, this);
        mService = PravzhiznService.instance.create(PravzhiznService.class);
    }

    public void saintFilterChanged(final String text) {
        if (!TextUtils.isEmpty(text)) {
            final Call<SaintsResponse> call = mService.saintsByName(text);

            call.enqueue(new Callback<SaintsResponse>() {
                @Override
                public void onResponse(final Call<SaintsResponse> call, final Response<SaintsResponse> response) {
                    if (response.body().success) {
                        mView.displaySaints(response.body().data.items);
                    }
                }

                @Override
                public void onFailure(final Call<SaintsResponse> call, final Throwable t) {

                }
            });
        }

    }

    public void saintClicked(final SaintObject saint) {
        final SaintObject currentSaint = new PravzhiznConfig(mContext).getSaint();
        if (currentSaint != null) {
            if (currentSaint.id == saint.id) {
                mListener.onSaintSaved();
            }
        }

        mView.setSaintEditTextEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        mService.saveSaint(saint.id).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(final Call<ProfileResponse> call, final Response<ProfileResponse> response) {
                if (response.body().success) {
                    new PravzhiznConfig(mContext).setSaint(saint);
                    mListener.onSaintSaved();
                } else {
                    mListener.onError();
                }

                dismissDialog();
            }

            @Override
            public void onFailure(final Call<ProfileResponse> call, final Throwable t) {
                mView.setSaintEditTextEnabled(true);

                mListener.onError();

                dismissDialog();
            }

            void dismissDialog() {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

}
