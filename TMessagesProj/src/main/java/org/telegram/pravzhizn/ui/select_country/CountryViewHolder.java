package org.telegram.pravzhizn.ui.select_country;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.CountryObject;

/**
 * Created by vlad on 8/29/16.
 */
public class CountryViewHolder extends ViewHolder {

    private final TextView mCityField;
    private final View mNearMeField;

    public CountryViewHolder(final View itemView, final TextView cityField, final View nearMeField) {
        super(itemView);
        mCityField = cityField;
        mNearMeField = nearMeField;
    }

    public void bind(final Context context, final CountryObject country, final OnCountryClickedListener listener) {
        mCityField.setText(country.name);

        final PravzhiznConfig config = new PravzhiznConfig(context);
        CountryObject selectedCity = config.getSelectedCountry();
        final boolean isSelected = (int)country.id == selectedCity.id;

        if (isSelected) {
            mCityField.setTextColor(context.getResources().getColor(R.color.techranch_selected));
        } else {
            mCityField.setTextColor(context.getResources().getColor(R.color.techranch_unselected));
        }

        int nearMeVisibility = isSelected ? View.VISIBLE : View.INVISIBLE;
        mNearMeField.setVisibility(nearMeVisibility);

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                listener.onCountrySelected(country);
            }
        });
    }
}
