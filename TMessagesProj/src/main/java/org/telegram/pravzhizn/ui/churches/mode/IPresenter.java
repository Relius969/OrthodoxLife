package org.telegram.pravzhizn.ui.churches.mode;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by vlad on 6/6/16.
 */
public interface IPresenter {

    void activate(Context context, final ViewGroup root);

    void deactivate(final ViewGroup root);
    void onLocationPermissionGranted();

    void onLoadMore(int page, int totalItemsCount);

    void onRefreshRequested();
}
