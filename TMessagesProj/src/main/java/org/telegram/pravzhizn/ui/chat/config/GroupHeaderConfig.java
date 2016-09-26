package org.telegram.pravzhizn.ui.chat.config;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;

/**
 * Created by matelskyvv on 8/20/16.
 */
public class GroupHeaderConfig implements HeaderConfig {

    private FrameLayout mGroupTab;
    private TextView mGroupUnreadMessages;
    private TextView mGroupLabel;
    private View mGroupSelectedMark;

    public GroupHeaderConfig(View chatHeader) {
        mGroupTab = (FrameLayout) chatHeader.findViewById(R.id.techranch_group_tab);
        mGroupUnreadMessages = (TextView) chatHeader.findViewById(R.id.techranch_unread_group_messages_count);
        mGroupLabel = (TextView) chatHeader.findViewById(R.id.techranch_ic_tabbar_group_label);
        mGroupLabel.setText(LocaleController.getString("Techranch_Church_Chat_Group_Label", R.string.Techranch_Church_Chat_Group_Label));

        mGroupSelectedMark = chatHeader.findViewById(R.id.techranch_group_selected_mark);
    }

    @Override
    public FrameLayout tab() {
        return mGroupTab;
    }

    @Override
    public View underline() {
        return mGroupSelectedMark;
    }

    @Override
    public TextView unreadMessagesLabel() {
        return mGroupUnreadMessages;
    }
}
