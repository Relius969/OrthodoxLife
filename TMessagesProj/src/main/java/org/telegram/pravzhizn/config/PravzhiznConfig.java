package org.telegram.pravzhizn.config;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.pravzhizn.pravzhizn.CountryObject;
import org.telegram.pravzhizn.pravzhizn.SaintObject;
import org.telegram.pravzhizn.utils.Factory;

import java.util.Date;

import static org.telegram.pravzhizn.config.ConfigUtils.fromSharedPreferences;
import static org.telegram.pravzhizn.config.ConfigUtils.getDate;
import static org.telegram.pravzhizn.config.ConfigUtils.storeDate;
import static org.telegram.pravzhizn.config.ConfigUtils.toConfigItem;
import static org.telegram.pravzhizn.config.ConfigUtils.toSharedPreferences;


/**
 * Created by vlad on 5/20/16.
 */
public class PravzhiznConfig {

    public static final String PRAVZHIZN_DATE_FORMAT = "yyyy-MM-dd";

    private static final String SELECTED_CITY_NAME_KEY = "selected city name";
    private static final String SELECTED_COUNTRY_NAME_KEY = "selected country name";
    private static final String USE_CLOSEST_TO_ME_KEY = "use closest to me";
    private static final String AUTH_TOKEN = "auth token";
    private static final String SELECTED_CITY_ID_KEY = "selected city id";
    private static final String SELECTED_COUNTRY_ID_KEY = "selected country id";
    private static final String PRAVZHIZN_PROFILE_PROFESSION = "profession key";
    private static final String PRAVZHIZN_PROFILE_BIRTHDAY_DATE = "birthday date";
    private static final String PRAVZHIZN_PROFILE_SAINT_ID = "saint id";
    private static final String PRAVZHIZN_PROFILE_SAINT_KEY = "saint key";

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public PravzhiznConfig(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences("pravzhizn_config", Context.MODE_PRIVATE);
    }

    public CityObject getSelectedCity() {
        final CityObject city = new CityObject();

        city.name = mPreferences.getString(SELECTED_CITY_NAME_KEY, mContext.getString(R.string.techrunch_no_city));
        city.id = mPreferences.getInt(SELECTED_CITY_ID_KEY, -1);

        return city;
    }

    public CountryObject getSelectedCountry() {
        final CountryObject city = new CountryObject();

        city.name = mPreferences.getString(SELECTED_COUNTRY_NAME_KEY, mContext.getString(R.string.techrunch_no_city));
        city.id = mPreferences.getInt(SELECTED_COUNTRY_ID_KEY, -1);

        return city;
    }

    public void setSelectedCity(CityObject city) {
        mPreferences.edit()
                .putString(SELECTED_CITY_NAME_KEY, city.name)
                .putInt(SELECTED_CITY_ID_KEY, city.id)
                .apply();
    }

    public void setSelectedCountry(CountryObject country) {
        mPreferences.edit()
                .putString(SELECTED_COUNTRY_NAME_KEY, country.name)
                .putInt(SELECTED_COUNTRY_ID_KEY, country.id)
                .apply();
    }

    public boolean isCitySelected() {
        return mPreferences.contains(SELECTED_CITY_ID_KEY);
    }

    public boolean isCountrySelected() {
        return mPreferences.contains(SELECTED_COUNTRY_ID_KEY);
    }

    public void setUseClosestToMe(boolean value) {
        mPreferences.edit().putBoolean(USE_CLOSEST_TO_ME_KEY, value).apply();
    }

    public boolean isUseClosestToMe() {
        return mPreferences.getBoolean(USE_CLOSEST_TO_ME_KEY, false);
    }

    public void setAuthToken(String value) {
        mPreferences.edit().putString(AUTH_TOKEN, value).apply();
    }

    public String authToken() {
        return mPreferences.getString(AUTH_TOKEN, "some value");
    }

    /* Profession */

    public boolean isProfessionSelected() {
        return mPreferences.contains(PRAVZHIZN_PROFILE_PROFESSION);
    }

    public void setProfession(final String profession) {
        mPreferences.edit().putString(PRAVZHIZN_PROFILE_PROFESSION, profession).apply();
    }

    public String getProfession() {
        return mPreferences.getString(PRAVZHIZN_PROFILE_PROFESSION, "");
    }

    /* Birthday date */

    public boolean isBirthdayDateSelected() {
        return mPreferences.contains(PRAVZHIZN_PROFILE_BIRTHDAY_DATE);
    }

    public void setBirthdayDate(Date date) {
        storeDate(mPreferences, PRAVZHIZN_PROFILE_BIRTHDAY_DATE, date);
    }

    public void clearBirthdayDate() {
        mPreferences.edit().remove(PRAVZHIZN_PROFILE_BIRTHDAY_DATE).apply();
    }

    public Date getBirthdayDate() {
        return getDate(mPreferences, PRAVZHIZN_PROFILE_BIRTHDAY_DATE);
    }


    /* Saint */

    public boolean isSaintSelected() {
        return mPreferences.contains(PRAVZHIZN_PROFILE_SAINT_ID);
    }

    public SaintObject getSaint() {
        return fromSharedPreferences(mPreferences,
                PRAVZHIZN_PROFILE_SAINT_ID,
                PRAVZHIZN_PROFILE_SAINT_KEY,
                new Factory<SaintObject>() {
                    @Override
                    public SaintObject create(final int id, final String value) {
                        final SaintObject saint = new SaintObject();
                        saint.id = id;
                        saint.name = value;
                        return saint;
                    }
                });

    }

    public void setSaint(SaintObject saint) {
        toSharedPreferences(mPreferences,
                PRAVZHIZN_PROFILE_SAINT_ID,
                PRAVZHIZN_PROFILE_SAINT_KEY,
                toConfigItem(saint));
    }

}
