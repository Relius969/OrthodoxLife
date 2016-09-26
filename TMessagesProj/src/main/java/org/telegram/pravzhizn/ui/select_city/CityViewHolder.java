package org.telegram.pravzhizn.ui.select_city;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.CityObject;

/**
 * Created by vlad on 5/23/16.
 */
public class CityViewHolder extends ViewHolder {

    private final TextView mCityField;
    private final View mNearMeField;

    public CityViewHolder(final View itemView, final TextView cityField, final View nearMeField) {
        super(itemView);
        mCityField = cityField;
        mNearMeField = nearMeField;
    }

    public void bind(final Context context, final CityObject city, final OnCityClickedListener listener) {
        mCityField.setText(city.name);

        final PravzhiznConfig config = new PravzhiznConfig(context);
        CityObject selectedCity = config.getSelectedCity();
        final boolean isSelected = city.id == selectedCity.id;

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
                listener.onCitySelected(city);
            }
        });
    }
}
