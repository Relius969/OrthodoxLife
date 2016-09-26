package org.telegram.pravzhizn.ui.chat.help_requests;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.pravzhizn.deeplinking.TelegramDeepLinkParsed;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.TL_error;

import java.util.ArrayList;

/**
 * Created by Vlad on 8/20/16.
 */
public class CheckIfJoined {

    public interface Listener extends ProgressListener {
        void onJoined(Chat chat);

        void onNotInTheChat(final TelegramDeepLinkParsed tlObject, ChatInvite response);

        void onError(TL_error error);
    }

    public int invoke(final TelegramDeepLinkParsed data, final Listener listener) {
        final int requestId;
        final TLRPC.TL_messages_checkChatInvite req = new TLRPC.TL_messages_checkChatInvite();
        req.hash = data.group;


        requestId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!listener.isFinishing()) {
                            listener.onFinished();
                            if (error == null) {
                                final TLRPC.ChatInvite invite = (TLRPC.ChatInvite) response;
                                if (invite.chat != null && !ChatObject.isLeftFromChat(invite.chat)) {

                                    MessagesController.getInstance().putChat(invite.chat, false);
                                    ArrayList<Chat> chats = new ArrayList<>();
                                    chats.add(invite.chat);
                                    MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);

                                    listener.onJoined(invite.chat);
                                } else {
                                    listener.onNotInTheChat(data, invite);
                                }
                            } else {
                                listener.onError(error);
                            }
                        }
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);

        listener.onStart(requestId);
        return requestId;
    }


}
