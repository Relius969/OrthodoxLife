package org.telegram.pravzhizn.ui.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.chat.config.ChannelHeaderConfig;
import org.telegram.pravzhizn.ui.chat.config.ChatTabConfig;
import org.telegram.pravzhizn.ui.chat.config.GroupHeaderConfig;
import org.telegram.pravzhizn.ui.chat.config.TabConfig;
import org.telegram.pravzhizn.ui.chat.config.TabConfig.Listener;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.SendCreateChatRequest;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.ProfileActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.telegram.pravzhizn.ui.chat.ChurchChatViewHelper.configureActionBarLayout;
import static org.telegram.pravzhizn.ui.chat.ChurchChatViewHelper.createNoChannelView;
import static org.telegram.pravzhizn.ui.chat.ChurchChatViewHelper.createNoGroupView;
import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Activity to display group and channel linked with a temple on one activity
 *
 * Created by Vlad on 6/8/16.
 */
public class ChurchChat extends BaseFragment implements ChurchChatListener, Listener {

    private static final String SCREEN_NAME = "Churches chat screen";
    private final RemoteChurch mChurch;
    private final PravzhiznService mService;

    private Tab mCurrentTab = Tab.Group;

    private ActionBarLayout mChatContainer;
    private static ArrayList<BaseFragment> sChatFragmentStack = new ArrayList<>();

    private ChatTabConfig mGroup;
    private ChatTabConfig mChannel;

    public enum Tab {
        Group, Channel
    }

    public ChurchChat(RemoteChurch church, final Tab group) {
        super(new Bundle());
        mChurch = church;
        mCurrentTab = group;
        mService = PravzhiznService.instance.create(PravzhiznService.class);
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        LinearLayout layout = configureActionBarLayout(context, actionBar, mChurch, this);

        View chatHeader = LayoutInflater.from(context).inflate(R.layout.techranch_chat_header, null);
        ChannelHeaderConfig channelHeaderConfig = new ChannelHeaderConfig(chatHeader);
        channelHeaderConfig.tab().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                switchToTab(Tab.Channel);
            }
        });

        GroupHeaderConfig groupHeaderConfig = new GroupHeaderConfig(chatHeader);
        groupHeaderConfig.tab().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                switchToTab(Tab.Group);
            }
        });

        layout.addView(chatHeader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 88));

        final View noGroupView = createNoGroupView(context, this);
        final View noChannelView = createNoChannelView(context, this);

        mChatContainer = new ActionBarLayout(context);
        layout.addView(mChatContainer);
        mChatContainer.init(sChatFragmentStack);

        layout.addView(noGroupView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        layout.addView(noChannelView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        fragmentView = layout;

        mGroup = new ChatTabConfig(groupHeaderConfig, noGroupView, this);
        mGroup.onCreate();
        mGroup.invoke(suitableChatInviteLink(), getParentActivity());

        mChannel = new ChatTabConfig(channelHeaderConfig, noChannelView, this);
        mChannel.onCreate();
        mChannel.invoke(suitableChannelInviteLink(), getParentActivity());

        return fragmentView;
    }

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        mGroup.onDestroy();
        mChannel.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        switchToCurrentTab();
    }

    @Override
    public void onChannelSettingsClicked() {
        Uri inviteLink = suitableChannelInviteLink();
        if (inviteLink != null) {
            openChannelSettings();
        } else {
            showSendRequestDialog(
                    LocaleController.getString("techranch_no_channel_added_send_request_to_create_one", R.string.techranch_no_channel_added_send_request_to_create_one),
                    LocaleController.getString("AppName", R.string.AppName),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            sendRequestToCreateGroup();
                        }
                    }
            );
        }
    }

    @Override
    public void onOpenGroupSettingsOptionClicked() {
        Uri chatInviteLink = suitableChatInviteLink();
        if (chatInviteLink != null) {
            openGroupSettings();
        } else {
            showSendRequestDialog(
                    LocaleController.getString("techranch_no_group_added_send_request_to_create_one", R.string.techranch_no_group_added_send_request_to_create_one),
                    LocaleController.getString("AppName", R.string.AppName),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            sendRequestToCreateGroup();
                        }
                    }
            );
        }
    }

    public void openChatSettings(int chat_id, ChatFull chatFull) {
        Bundle args = new Bundle();
        args.putInt("chat_id", chat_id);

        final ProfileActivity fragment = new ProfileActivity(args);
        fragment.setChatInfo(chatFull);

        presentFragment(fragment);
    }

    private void openChannelSettings() {
        mChannel.invokeOpenSettingsClicked();

    }

    private void openGroupSettings() {
        mGroup.invokeOpenSettingsClicked();
    }

    @Override
    public void sendRequestToCreateGroup() {
        sendRequestToCreateChat(ChatObject.CHAT_TYPE_MEGAGROUP, LocaleController.getString("techranch_send_request_to_create_group_clicked", R.string.techranch_send_request_to_create_group_clicked));
    }

    @Override
    public void sendRequestToCreateChannel() {
        sendRequestToCreateChat(ChatObject.CHAT_TYPE_CHANNEL, LocaleController.getString("techranch_send_request_to_create_channel_clicked", R.string.techranch_send_request_to_create_channel_clicked));
    }

    private void sendRequestToCreateChat(int type, final String confirmationMessage) {
        mService.sendChatRequest(UserConfig.getCurrentUser().phone, mChurch.id, type).enqueue(new Callback<SendCreateChatRequest>() {
            @Override
            public void onResponse(final Call<SendCreateChatRequest> call, final Response<SendCreateChatRequest> response) {
                if (response.body().success) {
                    Toast.makeText(getParentActivity(), confirmationMessage, Toast.LENGTH_LONG).show();
                } else {
                    if (!TextUtils.isEmpty(response.body().message)) {
                        Toast.makeText(getParentActivity(), response.body().message, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(final Call<SendCreateChatRequest> call, final Throwable t) {

            }
        });
    }

    public void showSendRequestDialog(String message, String title, DialogInterface.OnClickListener onConfirmClicked) {
        if (getParentActivity() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), onConfirmClicked);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void switchToTab(Tab tab) {
        if (mCurrentTab == tab) {
            return;
        }

        mCurrentTab = tab;

        switchToCurrentTab();
    }

    private void switchToCurrentTab() {
        if (mCurrentTab == Tab.Group) {
            switchToTab(mGroup, mChannel);
        } else {
            switchToTab(mChannel, mGroup);
        }
    }

    private Uri suitableChatInviteLink() {
        for (RemoteChurch.TechranchChatObject chat : mChurch.chats) {
            if (chat.type == ChatObject.CHAT_TYPE_CHAT) {
                return chat.invitation_link;
            }
        }

        return null;
    }

    private Uri suitableChannelInviteLink() {
        for (RemoteChurch.TechranchChatObject chat : mChurch.chats) {
            if (chat.type == ChatObject.CHAT_TYPE_CHANNEL) {
                return chat.invitation_link;
            }
        }

        return null;
    }

    public ChatActivity createChatActivityWithChatId(int chatId) {
        Bundle args = new Bundle();
        args.putInt("chat_id", chatId);

        return createChatFragmentWithArgs(args);
    }

    private ChatActivity createChatFragmentWithArgs(final Bundle args) {
        args.putBoolean("addActionBarToActivity", false);
        args.putBoolean("canNavigateFurther", false);

        return new ChatActivity(args);
    }

    public void presentChatActivity(ChatActivity chatActivity) {
        mChatContainer.setVisibility(View.VISIBLE);
        mChatContainer.removeAllFragments();
        mChatContainer.presentFragment(chatActivity, false, true, true);
    }

    private void switchToTab(TabConfig toSelect, TabConfig toDeselect) {
        toDeselect.exit();
        toSelect.enter();
    }

}
