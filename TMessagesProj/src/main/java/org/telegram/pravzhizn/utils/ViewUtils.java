package org.telegram.pravzhizn.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;

/**
 * Created by vlad on 9/8/16.
 */
public class ViewUtils {

    public static TextView createNoResultsView(Context context, String message) {
        final TextView emptyView = new TextView(context);
        emptyView.setText(message);
        emptyView.setVisibility(View.INVISIBLE);
        emptyView.setTextColor(Color.BLACK);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        emptyView.setHintTextColor(0xff979797);
        emptyView.setTextColor(Color.GRAY);
        emptyView.setMaxLines(1);
        emptyView.setLines(1);
        emptyView.setPadding(0, 0, 0, 0);
        emptyView.setSingleLine(true);
        emptyView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

        return emptyView;
    }

}
