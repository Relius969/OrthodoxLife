package org.telegram.pravzhizn.ui.profile.profession;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.responses.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 9/7/16.
 */
public class ProfessionPresenter {

    private final Context mContext;
    private final Listener mListener;
    private final ProfessionView mView;
    private final PravzhiznService mService;

    public View view() {
        return mView;
    }

    public interface Listener {
        void onProfessionSaved();
        void onError();
    }

    public ProfessionPresenter(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
        mView = new ProfessionView(context, this);
        mService = PravzhiznService.instance.create(PravzhiznService.class);
    }


    public void onDoneClicked() {
        saveProfesstion(mView.profession());
    }

    public void onTextChanged() {
        checkProfession(mView.profession(), false);
    }

    private boolean checkProfession(final String profession, boolean alert) {
        if (profession == null) {
            return false;
        }

        if (profession.length() > 0) {
            mView.showCheckTextView();
        } else {
            mView.hideCheckTextView();
        }
        if (alert && profession.length() == 0) {
            return true;
        }

        if (profession.length() > 32) {
            if (alert) {
                mListener.onError();
            } else {
                mView.setCheckTextViewText(LocaleController.getString("pravzhizn_profession_long", R.string.pravzhizn_profession_long));
                mView.setCheckTextColor(0xffcf3030);
            }
            return false;
        }

        return true;
    }

    private void saveProfesstion(final String newProfession) {
        if (!checkProfession(newProfession, true)) {
            return;
        }

        final PravzhiznConfig config = new PravzhiznConfig(mContext);

        String cirrentProfession = "";
        if (config.isProfessionSelected()) {
            cirrentProfession = config.getProfession();
        }

        if (cirrentProfession.equals(newProfession)) {
            mListener.onProfessionSaved();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        final Call<ProfileResponse> call = mService.saveProfession(newProfession);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(final Call<ProfileResponse> call, final Response<ProfileResponse> response) {
                try {
                    config.setProfession(newProfession);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }

                mListener.onProfessionSaved();
            }

            @Override
            public void onFailure(final Call<ProfileResponse> call, final Throwable t) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
                mListener.onError();
            }
        });

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call.cancel();
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
        progressDialog.show();
    }
}
