package org.telegram.pravzhizn.ui.profile;

import android.content.Context;

import org.telegram.messenger.FileLog;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.ProfileObject;
import org.telegram.pravzhizn.pravzhizn.responses.ProfileResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 9/7/16.
 */
public class UpdatePravzhiznProfileStep {
    private final Context mContext;

    public interface Listener {
        void onSuccess();

        void onError(final Throwable t);
    }

    public UpdatePravzhiznProfileStep(final Context context) {
        mContext = context;
    }

    public void invoke(final Listener listener) {
        final PravzhiznService service = PravzhiznService.instance.create(PravzhiznService.class);
        service.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(final Call<ProfileResponse> call, final Response<ProfileResponse> response) {
                if (response.body().success) {
                    ProfileObject profileObject = response.body().data;

                    PravzhiznConfig config = new PravzhiznConfig(mContext);
                    SimpleDateFormat format = new SimpleDateFormat(PravzhiznConfig.PRAVZHIZN_DATE_FORMAT, Locale.US);
                    try {
                        if (profileObject.birthday != null) {
                            Date date = format.parse(profileObject.birthday);
                            config.setBirthdayDate(date);
                        } else {
                            config.clearBirthdayDate();
                        }
                    } catch (ParseException e) {
                        FileLog.e("tmessages", e);
                    }

                    config.setProfession(profileObject.profession);
                    config.setSaint(profileObject.saint);

                    listener.onSuccess();
                } else {
                    listener.onError(new Throwable("an error"));
                }
            }

            @Override
            public void onFailure(final Call<ProfileResponse> call, final Throwable t) {
                listener.onError(t);
            }
        });
    }
}
