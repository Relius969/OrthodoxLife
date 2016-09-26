package org.telegram.pravzhizn.ui.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.pravzhizn.ui.details.ChurchDetailsAdapter.Holder;
import org.telegram.pravzhizn.ui.details.ChurchDetailsAdapter.Listener;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.utils.YandexMapsStaticAPI;


import static org.telegram.pravzhizn.utils.PhotosCount.photosLabelText;

/**
 * Created by matelskyvv on 6/15/16.
 */
public enum SubAdapter {

    Photos {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_photos_cell, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.techranch_church_album_view);
            String urlToLoad = getUrlToLoad(church);
            if (urlToLoad != null) {
                Glide.with(holder.itemView.getContext())
                        .load(urlToLoad)
                        .centerCrop()
                        .placeholder(R.drawable.img_gag_tample)
                        .crossFade()
                        .into(image);
            }

            TextView photosLabel = (TextView) holder.itemView.findViewById(R.id.techranch_photos_label);
            photosLabel.setText(photosLabelText(church.images));

            if (!church.images.isEmpty()) {
                image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        listener.onPhotosClicked();
                    }
                });
            }
        }

        private String getUrlToLoad(final RemoteChurch church) {
            if (church.images.isEmpty()) return null;

            return church.images.get(0).big_url;
        }
    },
    Phone {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_church_details_phone, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            final TextView phone = (TextView) holder.itemView.findViewById(R.id.church_phone);
            phone.setText("");
            if (!TextUtils.isEmpty(church.contact_phones)) {
                phone.setText(church.contact_phones);
                phone.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + church.contact_phones));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    },
    Site {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_church_details_link, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            final TextView site = (TextView) holder.itemView.findViewById(R.id.church_link);
            site.setText("");
            if (!TextUtils.isEmpty(church.site)) {
                site.setText(church.site);
                site.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(church.site));
                            String title = LocaleController.getString("Techranch_Churche_Chooser_Title", R.string.Techranch_My_Churches);
                            Intent chooser = Intent.createChooser(intent, title);

                            // Verify the intent will resolve to at least one activity
                            if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                                view.getContext().startActivity(chooser);
                            }
                        } catch (Exception ex) {
                            // TODO: log exception
                        }
                    }
                });
            }
        }
    },
    Separator {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_church_details_separator, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            // nothing to do
        }
    },
    Information {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_information_cell, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            setTextToTextView(holder, R.id.techranch_church_name, church.title);
            setTextToTextView(holder, R.id.church_address, "Адрес: " + church.address);
        }
    },
    Map {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_map_cell, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            ImageView mapPlace = (ImageView) holder.itemView.findViewById(R.id.map_place);

            WindowManager wm = (WindowManager) holder.itemView.getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);

            final String mapLink = YandexMapsStaticAPI.buildMapLink(church.map_lat, church.map_lng, 650, 450);
            Glide.with(holder.itemView.getContext())
                    .load(mapLink)
                    .centerCrop()
                    .placeholder(R.drawable.img_gag_tample)
                    .crossFade()
                    .into(mapPlace);

            holder.itemView.findViewById(R.id.scale_up_button).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {

                    double latitude = church.map_lat;
                    double longitude = church.map_lng;
                    String label = church.title;
                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude + "(" + label + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    if (intent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                        holder.itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    },
    Description {
        @Override
        public ViewHolder toViewHolder(final ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.techranch_description_cell, parent, false));
        }

        @Override
        public void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.church_description);

            String htmlBody = church.content.replaceAll("<img.+?>", "");
            textView.setText(Html.fromHtml(htmlBody));
        }
    };

    public int toViewType() {
        return ordinal();
    }

    public abstract ViewHolder toViewHolder(final ViewGroup parent);

    public abstract void bind(final ViewHolder holder, final int position, final RemoteChurch church, final Listener listener);

    private static void setTextToTextView(final ViewHolder holder, int id, String text) {
        TextView textView = (TextView) holder.itemView.findViewById(id);
        textView.setText(text);
    }

}
