package org.telegram.pravzhizn.analytics;

/**
 * Created by vlad on 9/20/16.
 */
public interface AnalyticsParams {

    enum UserAction {
        OpenMolitvoslov(TrackCategory.External, TrackAction.ClickedOnMolitvoslov),
        OpenShop(TrackCategory.External, TrackAction.ClickedOnShop),
        OpenPrayerOnDemand(TrackCategory.External, TrackAction.ClickedOnPrayerOnDemand);

        private final TrackCategory mCategory;
        private final TrackAction mAction;

        UserAction(TrackCategory category, TrackAction action) {
            mCategory = category;
            mAction = action;
        }

        public TrackCategory getCategory() {
            return mCategory;
        }

        public TrackAction getAction() {
            return mAction;
        }
    }

    enum TrackCategory {
        External("external apps");

        private final String mValue;

        TrackCategory(final String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    enum TrackAction {
        ClickedOnMolitvoslov("Clicked on Molitvoslov"),
        ClickedOnShop("Clicked on Pravzhizn shop"),
        ClickedOnPrayerOnDemand("Clicked on Prayer on demand");

        private final String mValue;

        TrackAction(final String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

}
