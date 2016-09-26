package org.telegram.pravzhizn.deeplinking;

import android.net.Uri;
import android.support.annotation.NonNull;

import org.telegram.messenger.Utilities;

import java.util.List;

/**
 * Created by vlad on 9/19/16.
 */
public class ActionViewParser {

    public static final int ID_INDEX = 2;
    public static final int JOIN_TEMPLE_INDEX = 1;
    private Uri mData;

    public ActionViewParser(final Uri data) {
        mData = data;
    }

    public ActionViewParsed invoke() {
        String scheme = mData.getScheme();

        if (scheme != null) {
            if ((scheme.equals("http") || scheme.equals("https"))) {
                String host = mData.getHost().toLowerCase();
                if (host.equals("pravzhizn.ru")) {
                    String path = mData.getPath();

                    List<String> segments = mData.getPathSegments();
                    if (segments.size() >= 3) {
                        if (segments.get(JOIN_TEMPLE_INDEX).equals("join_temple")) {
                            int templeId = Utilities.parseInt(segments.get(ID_INDEX));
                            if (templeId > 0) {
                                return new PravzhiznDeepLinkParsed(templeId);
                            } else {
                                return new PravzhiznDeepLinkParseError();
                            }
                        }
                    }
                }
            }
        }

        String username = null;
        String group = null;
        String sticker = null;
        String botUser = null;
        String botChat = null;
        String message = null;
        Integer messageId = null;
        boolean hasUrl = false;
        if (scheme != null) {
            if ((scheme.equals("http") || scheme.equals("https"))) {
                String host = mData.getHost().toLowerCase();
                if (host.equals("telegram.me") || host.equals("telegram.dog")) {
                    String path = mData.getPath();
                    if (path != null && path.length() > 1) {
                        path = path.substring(1);
                        if (path.startsWith("joinchat/")) {
                            group = path.replace("joinchat/", "");
                        } else if (path.startsWith("addstickers/")) {
                            sticker = path.replace("addstickers/", "");
                        } else if (path.startsWith("msg/") || path.startsWith("share/")) {
                            message = mData.getQueryParameter("url");
                            if (message == null) {
                                message = "";
                            }
                            if (mData.getQueryParameter("text") != null) {
                                if (message.length() > 0) {
                                    hasUrl = true;
                                    message += "\n";
                                }
                                message += mData.getQueryParameter("text");
                            }
                        } else if (path.length() >= 1) {
                            List<String> segments = mData.getPathSegments();
                            if (segments.size() > 0) {
                                username = segments.get(0);
                                if (segments.size() > 1) {
                                    messageId = Utilities.parseInt(segments.get(1));
                                    if (messageId == 0) {
                                        messageId = null;
                                    }
                                }
                            }
                            botUser = mData.getQueryParameter("start");
                            botChat = mData.getQueryParameter("startgroup");
                        }
                    }
                }
            } else if (scheme.equals("tg")) {
                String url = mData.toString();
                if (url.startsWith("tg:resolve") || url.startsWith("tg://resolve")) {
                    url = url.replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org");
                    mData = Uri.parse(url);
                    username = mData.getQueryParameter("domain");
                    botUser = mData.getQueryParameter("start");
                    botChat = mData.getQueryParameter("startgroup");
                    messageId = Utilities.parseInt(mData.getQueryParameter("post"));
                    if (messageId == 0) {
                        messageId = null;
                    }
                } else if (url.startsWith("tg:join") || url.startsWith("tg://join")) {
                    url = url.replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org");
                    mData = Uri.parse(url);
                    group = mData.getQueryParameter("invite");
                } else if (url.startsWith("tg:addstickers") || url.startsWith("tg://addstickers")) {
                    url = url.replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org");
                    mData = Uri.parse(url);
                    sticker = mData.getQueryParameter("set");
                } else if (url.startsWith("tg:msg") || url.startsWith("tg://msg") || url.startsWith("tg://share") || url.startsWith("tg:share")) {
                    url = url.replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org");
                    mData = Uri.parse(url);
                    message = mData.getQueryParameter("url");
                    if (message == null) {
                        message = "";
                    }
                    if (mData.getQueryParameter("text") != null) {
                        if (message.length() > 0) {
                            hasUrl = true;
                            message += "\n";
                        }
                        message += mData.getQueryParameter("text");
                    }
                }
            }
        }

        return new TelegramDeepLinkParsed.Builder()
                .setUsername(username)
                .setGroup(group)
                .setSticker(sticker)
                .setBotUser(botUser)
                .setBotChat(botChat)
                .setMessage(message)
                .setMessageId(messageId)
                .setHasUrl(hasUrl)
                .build();
    }

    @NonNull
    private ActionViewParsed parsedPravzhiznResult() {
        final int churchId = Integer.valueOf(mData.getQueryParameter("id"));
        return new PravzhiznDeepLinkParsed(churchId);
    }

}
