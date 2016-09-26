package org.telegram.pravzhizn.ui.chat.help_requests;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.deeplinking.TelegramDeepLinkParsed;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/**
 * Class to get chat for public channel
 * <p/>
 * Created by matelskyvv on 8/20/16.
 */
public class ResolveUsername {

    public interface Listener extends ProgressListener {

        void onJoined(Chat args);

        void onError(TL_error error);

        void sendToast(String message);

    }

    public int invoke(final TelegramDeepLinkParsed data, final Listener listener) {
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = data.username;
        final int requestId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!listener.isFinishing()) {
                            listener.onFinished();

                            if (error == null) {
                                final TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
                                MessagesController.getInstance().putUsers(res.users, false);
                                MessagesController.getInstance().putChats(res.chats, false);
                                MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, false, true);

                                if (data.botChat != null) {
                                    final User user = !res.users.isEmpty() ? res.users.get(0) : null;
                                    if (user == null || user.bot && user.bot_nochats) {
                                        listener.sendToast(LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups));
                                        listener.onError(new TL_error());
                                    }
                                } else {
                                    if (!res.chats.isEmpty()) {
                                        final Chat chat = res.chats.get(0);
                                        listener.onJoined(chat);
                                    } else {
                                        listener.onError(new TL_error());
                                    }
                                }
                            } else {
                                listener.sendToast(LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound));
                            }
                        }
                    }
                });
            }
        });
        listener.onStart(requestId);
        return requestId;
    }
}
