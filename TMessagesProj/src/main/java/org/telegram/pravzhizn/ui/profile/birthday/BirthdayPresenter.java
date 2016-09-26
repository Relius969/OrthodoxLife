package org.telegram.pravzhizn.ui.profile.birthday;

import android.app.ProgressDialog;
import android.content.Context;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.responses.ProfileResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 9/7/16.
 */
public class BirthdayPresenter {

    private final Context mContext;
    private final BirthdayView mView;
    private final Listener mListener;
    private final PravzhiznService mService;

    public BirthdayView view() {
        return mView;
    }

    public void onBirthdayDateSelected() {
        final Date date = mView.getCurrentDate();

        if (date != null) {
            onBirthdayDateSelected(date);
        } else {
            mListener.onSuccess();
        }
    }

    public void onBirthdayDateSelected(final Date date) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mService.saveBirthday(df.format(date)).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(final Call<ProfileResponse> call, final Response<ProfileResponse> response) {
                if (response.body().success) {
                    new PravzhiznConfig(mContext).setBirthdayDate(date);
                    mListener.onSuccess();
                } else {
                    mListener.onError();
                }

                dismissDialog();
            }

            @Override
            public void onFailure(final Call<ProfileResponse> call, final Throwable t) {
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

    public interface Listener {
        void onSuccess();
        void onError();
    }

    public BirthdayPresenter(Context context, final Listener listener) {
        mContext = context;

        mListener = listener;

        mView = new BirthdayView(context, this);

        mService = PravzhiznService.instance.create(PravzhiznService.class);
    }

}
