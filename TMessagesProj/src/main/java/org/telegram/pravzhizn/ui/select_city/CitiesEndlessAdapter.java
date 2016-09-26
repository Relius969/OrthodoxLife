package org.telegram.pravzhizn.ui.select_city;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.pravzhizn.pravzhizn.CountryObject;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.responses.CitiesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matelskyvv on 8/26/16.
 */
public class CitiesEndlessAdapter extends CitiesAdapter {

    private final String mName;
    private final CountryObject mCountry;

    public static final int LIMIT = 20;

    private final PravzhiznService mService;

    public CitiesEndlessAdapter(Context context, CountryObject country, OnCityClickedListener listener) {
        this(country, "", context, listener);
    }

    public CitiesEndlessAdapter(CountryObject country, String name, Context context, OnCityClickedListener listener) {
        super(context, listener);

        mService = PravzhiznService.instance.create(PravzhiznService.class);
        mCountry = country;
        mName = name;

        final Call<CitiesResponse> call;
        call = makeCall(name, 0);

        call.enqueue(new Callback<CitiesResponse>() {
            @Override
            public void onResponse(final Call<CitiesResponse> call, final Response<CitiesResponse> response) {
                if (response.body() == null) {
                    return;
                }

                if (response.body().success) {
                    final List<CityObject> items = response.body().data.items;
                    setData(items);
                }
            }

            @Override
            public void onFailure(final Call<CitiesResponse> call, final Throwable t) {

            }
        });
    }

    private Call<CitiesResponse> makeCall(final String name, int page) {
        final Call<CitiesResponse> call;
        if (TextUtils.isEmpty(name)) {
            if (mCountry.id != null) {
                call = mService.citiesList(mCountry.id, LIMIT, page);
            } else {
                call = mService.citiesList(LIMIT, page);
            }
        } else {
            if (mCountry.id != null) {
                call = mService.citiesList(mCountry.id, name, LIMIT, page);
            } else {
                call = mService.citiesList(name, LIMIT, page);
            }
        }
        return call;
    }

    public void loadMore(final int page, final int totalItemsCount) {
        makeCall(mName, mData.size()).enqueue(new Callback<CitiesResponse>() {
            @Override
            public void onResponse(final Call<CitiesResponse> call, final Response<CitiesResponse> response) {
                if (response.body().success) {
                    final List<CityObject> items = response.body().data.items;
                    addData(items);
                }
            }

            @Override
            public void onFailure(final Call<CitiesResponse> call, final Throwable t) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
