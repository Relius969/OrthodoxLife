package org.telegram.pravzhizn.ui.profile.saint;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.SaintObject;

/**
 * Created by vlad on 9/8/16.
 */
public class SaintViewHolder extends ViewHolder {

    final TextView mSaintNameField;

    public SaintViewHolder(final View itemView) {
        super(itemView);
        mSaintNameField = (TextView) itemView.findViewById(R.id.saint_name);
    }

    public void bind(final SaintObject saint, final SaintsAdapter.Listener listener) {
        mSaintNameField.setText(saint.name);

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.onSaintClicked(saint);
            }
        });
    }
}
