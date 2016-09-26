package org.telegram.pravzhizn.ui.select_country;

import android.content.Context;
import android.text.TextUtils;

import org.telegram.pravzhizn.pravzhizn.CountryObject;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.responses.CountriesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 8/29/16.
 */
public class CountriesEndlessAdapter extends CountriesAdapter {

    private final String mName;

    public static final int LIMIT = 20;

    private final PravzhiznService mService;

    public CountriesEndlessAdapter(Context context, OnCountryClickedListener listener) {
        this("", context, listener);
    }

    public CountriesEndlessAdapter(String name, Context context, OnCountryClickedListener listener) {
        super(context, listener);

        mService = PravzhiznService.instance.create(PravzhiznService.class);
        mName = name;

        final Call<CountriesResponse> call;
        call = makeCall(name, 0);

        call.enqueue(new Callback<CountriesResponse>() {
            @Override
            public void onResponse(final Call<CountriesResponse> call, final Response<CountriesResponse> response) {
                if (response.body() == null) {
                    return;
                }

                if (response.body().success) {
                    final List<CountryObject> items = response.body().data.items;
                    setData(items);
                }
            }

            @Override
            public void onFailure(final Call<CountriesResponse> call, final Throwable t) {

            }
        });
    }

    private Call<CountriesResponse> makeCall(final String name, int page) {
        final Call<CountriesResponse> call;
        if (TextUtils.isEmpty(name)) {
            call = mService.countriesList(LIMIT, page);
        } else {
            call = mService.countriesList(name, LIMIT, page);
        }
        return call;
    }

    public void loadMore(final int page, final int totalItemsCount) {
        makeCall(mName, mData.size()).enqueue(new Callback<CountriesResponse>() {
            @Override
            public void onResponse(final Call<CountriesResponse> call, final Response<CountriesResponse> response) {
                if (response.body().success) {
                    final List<CountryObject> items = response.body().data.items;
                    addData(items);
                }
            }

            @Override
            public void onFailure(final Call<CountriesResponse> call, final Throwable t) {

            }
        });
    }

}
