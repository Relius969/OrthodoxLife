package org.telegram.pravzhizn.ui.chat;

import org.telegram.pravzhizn.deeplinking.TelegramDeepLinkParsed;
import org.telegram.pravzhizn.ui.chat.help_requests.CheckIfJoined;
import org.telegram.pravzhizn.ui.chat.help_requests.JoinToGroup;
import org.telegram.pravzhizn.ui.chat.help_requests.ResolveUsername;

/**
 * Created by vlad on 8/20/16.
 */
public class ApiHelper {

    public static void checkIfUserAlreadyJoined(TelegramDeepLinkParsed result, CheckIfJoined.Listener listener) {
        new CheckIfJoined().invoke(result, listener);
    }

    public static void joinToGroup(TelegramDeepLinkParsed result, JoinToGroup.Listener listener) {
        new JoinToGroup().invoke(result, listener);
    }

    public static void resolveUsername(TelegramDeepLinkParsed result, ResolveUsername.Listener listener) {
        new ResolveUsername().invoke(result, listener);
    }

}
