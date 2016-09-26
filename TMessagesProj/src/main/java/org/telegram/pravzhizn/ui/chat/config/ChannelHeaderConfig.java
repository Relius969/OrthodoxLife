package org.telegram.pravzhizn.ui.chat.config;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;

/**
 * Created by matelskyvv on 8/20/16.
 */
public class ChannelHeaderConfig implements HeaderConfig {

    private FrameLayout mChannelTab;
    private TextView mChannelUnreadMessages;
    private TextView mChannelLabel;
    private View mChannelSelectedMark;

    public ChannelHeaderConfig(View chatHeader) {
        mChannelTab = (FrameLayout) chatHeader.findViewById(R.id.techranch_channel_tab);
        mChannelUnreadMessages = (TextView) chatHeader.findViewById(R.id.techranch_unread_channel_messages_count);
        mChannelLabel = (TextView) chatHeader.findViewById(R.id.techranch_ic_tabbar_channel_label);
        mChannelLabel.setText(LocaleController.getString("Techranch_Church_Chat_Channel_Label", R.string.Techranch_Church_Chat_Channel_Label));
        mChannelSelectedMark = chatHeader.findViewById(R.id.techranch_channel_selected_mark);
    }

    @Override
    public FrameLayout tab() {
        return mChannelTab;
    }

    @Override
    public View underline() {
        return mChannelSelectedMark;
    }

    @Override
    public TextView unreadMessagesLabel() {
        return mChannelUnreadMessages;
    }
}
