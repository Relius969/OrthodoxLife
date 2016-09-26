package org.telegram.pravzhizn.ui.chat.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.deeplinking.ActionViewParsed;
import org.telegram.pravzhizn.deeplinking.ActionViewParser;
import org.telegram.pravzhizn.deeplinking.PravzhiznDeepLinkParseError;
import org.telegram.pravzhizn.deeplinking.PravzhiznDeepLinkParsed;
import org.telegram.pravzhizn.deeplinking.TelegramDeepLinkParsed;
import org.telegram.pravzhizn.ui.chat.help_requests.CheckIfJoined;
import org.telegram.pravzhizn.ui.chat.help_requests.JoinToGroup;
import org.telegram.pravzhizn.ui.chat.help_requests.ResolveUsername;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.LaunchActivity;

import java.util.concurrent.Semaphore;

import static org.telegram.pravzhizn.ui.chat.ApiHelper.checkIfUserAlreadyJoined;
import static org.telegram.pravzhizn.ui.chat.ApiHelper.joinToGroup;
import static org.telegram.pravzhizn.ui.chat.ApiHelper.resolveUsername;

/**
 * One tab on church chats view
 *
 * Created by matelskyvv on 8/20/16.
 */
public class ChatTabConfig implements TabConfig,
    ResolveUsername.Listener,
        CheckIfJoined.Listener,
        JoinToGroup.Listener,
        NotificationCenter.NotificationCenterDelegate {

    private final HeaderConfig mConfig;
    private final View mNoGroupView;
    private final Listener mListener;

    private Chat mChat;
    private ChatFull mInfo;
    private Uri mInviteLink;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;

    private boolean isActive;
    private ChatActivity mChatActivity;
    private boolean mOpeningErrorFlag = false;

    public ChatTabConfig(
            HeaderConfig config,
            View noGroupView,
            Listener listener) {
        mConfig = config;
        mNoGroupView = noGroupView;
        mListener = listener;
    }

    public void invoke(Uri inviteLink, Activity activity) {
        mInviteLink = inviteLink;
        mActivity = activity;

        if (mInviteLink == null) {
            return;
        }

        ActionViewParsed result = new ActionViewParser(inviteLink).invoke();
        result.apply(new ActionViewParsed.Visitor() {
            @Override
            public void apply(final TelegramDeepLinkParsed value) {
                if (canRunLinkRequest(value)) {
                    runLinkRequest(value);
                }
            }

            @Override
            public void apply(final PravzhiznDeepLinkParsed value) {

            }

            @Override
            public void apply(final PravzhiznDeepLinkParseError value) {

            }
        });
    }

    private void runLinkRequest(final TelegramDeepLinkParsed result) {
        if (result.username != null) {
            resolveUsername(result, this);
        } else if (result.group != null) {
            checkIfUserAlreadyJoined(result, this);
        }
    }

    @Override
    public HeaderConfig headerConfig() {
        return mConfig;
    }

    @Override
    public void enter() {
        isActive = true;
        setSelected(headerConfig());
        if ((mInviteLink != null) && !mOpeningErrorFlag) {
            switchToChatPart();
        } else {
            switchToNoGoupView();
        }
    }

    void switchToNoGoupView() {
        mNoGroupView.setVisibility(View.VISIBLE);
    }

    void switchToChatPart() {
        if (mChat != null) {
            presentChatActivity(mChat);
        }
        mNoGroupView.setVisibility(View.GONE);
    }

    @Override
    public void exit() {
        isActive = false;
        setDeselected(headerConfig());
        mNoGroupView.setVisibility(View.GONE);
        if (mChatActivity != null) {
            mChatActivity.finishFragment(false);
            mChatActivity = null;
        }

    }

    @Override
    public void invokeOpenSettingsClicked() {
        if (mInviteLink == null) {
            mListener.showSendRequestDialog(
                    LocaleController.getString("techranch_no_group_added_send_request_to_create_one", R.string.techranch_no_group_added_send_request_to_create_one),
                    LocaleController.getString("AppName", R.string.AppName),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            mListener.sendRequestToCreateGroup();
                        }
                    }
            );
        } else {
            if (mChat != null) {
                mListener.openChatSettings(mChat.id, mInfo);
            }
        }
    }

    public void setChat(final Chat chat) {
        mChat = chat;

        if (mChat != null) {
            Semaphore semaphore = null;
            if (mChat.broadcast) {
                semaphore = new Semaphore(0);
            }
            MessagesController.getInstance().loadChatInfo(mChat.id, semaphore, ChatObject.isChannel(mChat));
            if (mChat.broadcast && semaphore != null) {
                try {
                    semaphore.acquire();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    @Override
    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    private boolean canRunLinkRequest(final TelegramDeepLinkParsed result) {
        return result.username != null || result.group != null || result.sticker != null || result.message != null;
    }

    @Override
    public void onJoined(final Chat chat) {
        setChat(chat);

        if (isActive) {
            presentChatActivity(mChat);
        }
    }

    private void presentChatActivity(final Chat chat) {
        mChatActivity = mListener.createChatActivityWithChatId(chat.id);
        mListener.presentChatActivity(mChatActivity);
    }

    @Override
    public void onNotInTheChat(final TelegramDeepLinkParsed data, final ChatInvite response) {
        joinToGroup(data, this);
    }

    @Override
    public void onError(final TL_error error) {
        if (mActivity != null && !isFinishing()) {
            Builder builder = new Builder(mActivity);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (error.text.startsWith("FLOOD_WAIT")) {
                builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
            } else if (error.text.equals("USERS_TOO_MUCH")) {
                builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
            } else {
                builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            LaunchActivity.sActivity.showAlertDialog(builder);

            mOpeningErrorFlag = true;
            switchToNoGoupView();
        }
    }

    @Override
    public void sendToast(final String message) {
        try {
            Toast.makeText(mActivity, LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    @Override
    public void onStart(final int reqId) {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConnectionsManager.getInstance().cancelRequest(reqId, true);
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
        mProgressDialog.show();
    }

    @Override
    public void onFinished() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    @Override
    public boolean isFinishing() {
        if (mActivity != null) {
            return mActivity.isFinishing();
        }
        return true;
    }

    private void setSelected(HeaderConfig toSelect) {
        toSelect.tab().setSelected(true);
        toSelect.underline().setVisibility(View.VISIBLE);
        toSelect.unreadMessagesLabel().setVisibility(View.INVISIBLE); // TODO: fill unreawardsd count value and display it after
    }

    private void setDeselected(HeaderConfig toDeselect) {
        toDeselect.tab().setSelected(false);
        toDeselect.underline().setVisibility(View.INVISIBLE);
        toDeselect.unreadMessagesLabel().setVisibility(View.INVISIBLE);
    }

    @Override
    public void didReceivedNotification(int id, final Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (mChat != null && chatFull.id == mChat.id) {
                if (chatFull instanceof TLRPC.TL_channelFull) {
                    if (mChat.megagroup) {
                        int lastDate = 0;
                        if (chatFull.participants != null) {
                            for (int a = 0; a < chatFull.participants.participants.size(); a++) {
                                lastDate = Math.max(chatFull.participants.participants.get(a).date, lastDate);
                            }
                        }
                        if (lastDate == 0 || Math.abs(System.currentTimeMillis() / 1000 - lastDate) > 60 * 60) {
                            MessagesController.getInstance().loadChannelParticipants(mChat.id);
                        }
                    }
                    
                    if (chatFull.participants == null && mInfo != null) {
                        chatFull.participants = mInfo.participants;
                    }
                }
                mInfo = chatFull;

                if (mChat.broadcast) {
                    SendMessagesHelper.getInstance().setCurrentChatInfo(mInfo);
                }
            }
        } else if (id == NotificationCenter.chatInfoCantLoad) {
            int chatId = (Integer) args[0];
            if (mChat != null && mChat.id == chatId) {
                int reason = (Integer) args[1];
                if (mActivity == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (reason == 0) {
                    builder.setMessage(LocaleController.getString("ChannelCantOpenPrivate", R.string.ChannelCantOpenPrivate));
                } else if (reason == 1) {
                    builder.setMessage(LocaleController.getString("ChannelCantOpenNa", R.string.ChannelCantOpenNa));
                } else if (reason == 2) {
                    builder.setMessage(LocaleController.getString("ChannelCantOpenBanned", R.string.ChannelCantOpenBanned));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                LaunchActivity.sActivity.showAlertDialog(builder);
            }
        }
    }
}
