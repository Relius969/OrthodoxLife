package org.telegram.pravzhizn.ui.my_churches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;

/**
 * Created by vlad on 5/20/16.
 */
public class MyChurchesActivityHelper {

    public static View createSelectLocationHeader(Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.selected_location_header, null);

        TextView label = (TextView) view.findViewById(R.id.selected_city_label);
        label.setText(LocaleController.getString("Techranch_Select_City", R.string.Techranch_Select_City));

        TextView countryLabel = (TextView) view.findViewById(R.id.selected_country_label);
        countryLabel.setText(LocaleController.getString("Techranch_Select_Country", R.string.Techranch_Select_Country));


        return view;
    }
}
