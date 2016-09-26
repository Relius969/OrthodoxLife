package org.telegram.pravzhizn.ui.details;

/**
 * Created by matelskyvv on 6/15/16.
 */
public enum  SubItem {

    Photos {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Photos;
        }
    },
    Phone {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Phone;
        }
    },
    Site {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Site;
        }
    },
    Separator {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Separator;
        }
    },
    Information {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Information;
        }
    },
    Map {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Map;
        }
    },
    Description {
        @Override
        public SubAdapter toSubAdapter() {
            return SubAdapter.Description;
        }
    };

    public abstract SubAdapter toSubAdapter();

}
