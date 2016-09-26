package org.telegram.pravzhizn.ui.chat.config;

import android.content.DialogInterface;

import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.ui.ChatActivity;

/**
 * Created by matelskyvv on 8/20/16.
 */
public interface TabConfig {

    interface Listener {
        void openChatSettings(int chatId, ChatFull info);

        void sendRequestToCreateGroup();

        void showSendRequestDialog(String message, String title, DialogInterface.OnClickListener onConfirmClicked);

        ChatActivity createChatActivityWithChatId(int chatId);

        void presentChatActivity(ChatActivity chatActivity);
    }

    void invokeOpenSettingsClicked();

    HeaderConfig headerConfig();

    void onCreate();

    void onDestroy();

    void enter();

    void exit();


}
