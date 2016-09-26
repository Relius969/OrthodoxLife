package org.telegram.pravzhizn.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.telegram.messenger.ApplicationLoader;

/**
 * Created by vlad on 9/20/16.
 */
public class AnalyticsUtils {

    public static void trackScreenName(final String screenName) {
        final Tracker tracker = ApplicationLoader.getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    public static void trackUserAction(final AnalyticsParams.UserAction userAction) {
        final Tracker tracker = ApplicationLoader.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(userAction.getCategory().getValue())
                .setAction(userAction.getAction().getValue())
                .build());

    }



}
