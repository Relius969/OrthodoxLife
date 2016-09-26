package org.telegram.pravzhizn.ui.select_city;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 5/23/16.
 */
public class CitiesAdapter extends Adapter<CityViewHolder> {

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    protected final Context mContext;
    protected final List<CityObject> mData;
    protected final OnCityClickedListener mListener;

    public CitiesAdapter(Context context, OnCityClickedListener listener) {
        mContext = context;
        mData = new ArrayList<>();
        mListener = listener;
    }

    public void setData(List<CityObject> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<CityObject> data) {
        int curSize = getItemCount();
        mData.addAll(data);
        notifyItemRangeInserted(curSize, data.size() - 1);
    }

    @Override
    public CityViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == HEADER) {

            TextView header = new TextView(parent.getContext());
            header.setText(LocaleController.getString("techranch_cities", R.string.techranch_cities));
            header.setTextColor(parent.getResources().getColor(android.R.color.darker_gray));
            header.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            header.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(5), 0, AndroidUtilities.dp(5));

            return new CityViewHolder(header, null, null);
        } else {
            FrameLayout cell = new FrameLayout(parent.getContext());
            cell.setClickable(true);

            TextView cityField = new TextView(parent.getContext());
            final FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            cell.addView(cityField, layoutParams);

            ImageView nearMeField = new ImageView(parent.getContext());
            nearMeField.setImageResource(R.drawable.ic_near_me);

            final FrameLayout.LayoutParams layoutParams1 = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            cell.addView(nearMeField, layoutParams1);

            final RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(40));
            params.setMargins(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10), 0);
            cell.setLayoutParams(params);
            return new CityViewHolder(cell, cityField, nearMeField);
        }
    }

    @Override
    public void onBindViewHolder(final CityViewHolder holder, final int position) {
        if (position > 0) {
            holder.bind(mContext, mData.get(position - 1), mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return HEADER;
        }
        return ITEM;
    }
}
