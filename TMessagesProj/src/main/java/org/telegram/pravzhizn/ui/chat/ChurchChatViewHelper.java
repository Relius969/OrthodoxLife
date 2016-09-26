package org.telegram.pravzhizn.ui.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.Components.LayoutHelper;

/**
 * Class to create some views from code
 *
 * Created by Vlad on 8/20/16.
 */
public class ChurchChatViewHelper {

    public static View createNoGroupView(final Context context, final ChurchChatListener listener) {
        View noGroupView = LayoutInflater.from(context).inflate(R.layout.techranch_no_church_chat, null);
        TextView noChatFoundMessage = (TextView) noGroupView.findViewById(R.id.techranch_no_chat_found);
        noChatFoundMessage.setText(LocaleController.getString("Techranch_Church_Chat_No_Chat_Found", R.string.Techranch_Church_Chat_No_Chat_Found));

        Button createChatButton = (Button) noGroupView.findViewById(R.id.techranch_add_chat);
        createChatButton.setText(LocaleController.getString("Techranch_Create_Group", R.string.Techranch_Create_Group));
        createChatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.sendRequestToCreateGroup();
            }
        });

        return noGroupView;
    }

    public static View createNoChannelView(final Context context, final ChurchChatListener listener) {
        View noGroupView = LayoutInflater.from(context).inflate(R.layout.techranch_no_church_chat, null);
        TextView noChatFoundMessage = (TextView) noGroupView.findViewById(R.id.techranch_no_chat_found);
        noChatFoundMessage.setText(LocaleController.getString("Techranch_Church_Chat_No_Channel_Found", R.string.Techranch_Church_Chat_No_Channel_Found));

        Button createChatButton = (Button) noGroupView.findViewById(R.id.techranch_add_chat);
        createChatButton.setText(LocaleController.getString("Techranch_Create_Channel", R.string.Techranch_Create_Channel));
        createChatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.sendRequestToCreateChannel();
            }
        });

        return noGroupView;
    }


    private static final int settings_of_group = 5;
    private static final int settings_of_channel = 6;
    public static LinearLayout configureActionBarLayout(Context context, ActionBar actionBar, RemoteChurch mChurch, final ChurchChat chat) {
        actionBar.setAddToContainer(false);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);

        final ActionBarMenu menu = actionBar.createMenu();

        final ChurchChatHeaderContainer churchThumb = new ChurchChatHeaderContainer(context, mChurch);

        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_other);
        item.addSubItem(settings_of_group, LocaleController.getString("Techranch_Group_Settings", R.string.Techranch_Group_Settings), 0);
        item.addSubItem(settings_of_channel, LocaleController.getString("Techranch_Channel_Settings", R.string.Techranch_Channel_Settings), 0);

        actionBar.addView(churchThumb, 0, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 56, 0, 40, 0));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    chat.finishFragment();
                } else if (id == settings_of_group) {
                    chat.onOpenGroupSettingsOptionClicked();
                } else if (id == settings_of_channel) {
                    chat.onChannelSettingsClicked();
                }
            }
        });

        LinearLayout layout = new LinearLayout(context);

        layout.addView(actionBar);

        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

}
