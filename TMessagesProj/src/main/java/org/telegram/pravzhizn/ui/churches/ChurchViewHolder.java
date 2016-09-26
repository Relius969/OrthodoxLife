package org.telegram.pravzhizn.ui.churches;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;

/**
 * Created by vlad on 5/20/16.
 */
public class ChurchViewHolder extends ViewHolder {

    private final ImageView mChurchImage;
    private final TextView mChurchName;

    public ChurchViewHolder(final View itemView) {
        super(itemView);

        mChurchImage = (ImageView) itemView.findViewById(R.id.techranch_church_image);
        mChurchName = (TextView) itemView.findViewById(R.id.techranch_church_name);
    }

    public void bind(final RemoteChurch church, final int position, final ChurchesPresenterListener listener) {
        mChurchName.setText(church.title);
        mChurchImage.setImageResource(R.drawable.img_gag_tample);

        String urlToLoad = getUrlToLoad(church);
        if (urlToLoad != null) {
            Glide.with(mChurchImage.getContext())
                    .load(urlToLoad)
                    .centerCrop()
                    .placeholder(R.drawable.img_gag_tample)
                    .crossFade()
                    .into(mChurchImage);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                listener.onItemSelected(church);
            }
        });

        itemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                listener.onLongClicked(position, church);
                return true;
            }
        });
    }

    private String getUrlToLoad(final RemoteChurch church) {
        if (church.images.isEmpty()) return null;

        return church.images.get(0).big_url;
    }
}
