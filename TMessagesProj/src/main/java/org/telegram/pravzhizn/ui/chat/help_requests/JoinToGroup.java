package org.telegram.pravzhizn.ui.chat.help_requests;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.pravzhizn.deeplinking.TelegramDeepLinkParsed;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;

/**
 * Created by matelskyvv on 8/20/16.
 */
public class JoinToGroup {

    public interface Listener extends ProgressListener {

        void onJoined(Chat chat);

        void onError(TL_error error);

    }

    public int invoke(final TelegramDeepLinkParsed data, final Listener listener) {
        TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
        req.hash = data.group;
        final int requestId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TL_error error) {
                if (error == null) {
                    Updates updates = (Updates) response;
                    MessagesController.getInstance().processUpdates(updates, false);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!listener.isFinishing()) {
                            listener.onFinished();
                            if (error == null) {
                                Updates updates = (Updates) response;
                                if (!updates.chats.isEmpty()) {
                                    Chat chat = updates.chats.get(0);
                                    chat.left = false;
                                    chat.kicked = false;
                                    MessagesController.getInstance().putUsers(updates.users, false);
                                    MessagesController.getInstance().putChats(updates.chats, false);

                                    listener.onJoined(chat);
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
