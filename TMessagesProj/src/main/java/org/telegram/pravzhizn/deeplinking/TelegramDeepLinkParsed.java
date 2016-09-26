package org.telegram.pravzhizn.deeplinking;

/**
 * Created by vlad on 9/19/16.
 */
public class TelegramDeepLinkParsed implements ActionViewParsed {

    public final String username;
    public final String group;
    public final String sticker;
    public final String botUser;
    public final String botChat;
    public final String message;
    public final Integer messageId;
    public final boolean hasUrl;

    public TelegramDeepLinkParsed(final String username, final String group, final String sticker, final String botUser, final String botChat, final String message, final Integer messageId, final boolean hasUrl) {
        this.username = username;
        this.group = group;
        this.sticker = sticker;
        this.botUser = botUser;
        this.botChat = botChat;
        this.message = message;
        this.messageId = messageId;
        this.hasUrl = hasUrl;
    }

    private TelegramDeepLinkParsed(final Builder builder) {
        username = builder.username;
        group = builder.group;
        sticker = builder.sticker;
        botUser = builder.botUser;
        botChat = builder.botChat;
        message = builder.message;
        messageId = builder.messageId;
        hasUrl = builder.hasUrl;
    }

    @Override
    public void apply(final Visitor visitor) {
        visitor.apply(this);
    }

    public static final class Builder {
        private String username = null;
        private String group = null;
        private String sticker = null;
        private String botUser = null;
        private String botChat = null;
        private String message = null;
        private Integer messageId = null;
        private boolean hasUrl;

        public Builder() {
        }

        public Builder(final TelegramDeepLinkParsed copy) {
            this.username = copy.username;
            this.group = copy.group;
            this.sticker = copy.sticker;
            this.botUser = copy.botUser;
            this.botChat = copy.botChat;
            this.message = copy.message;
            this.messageId = copy.messageId;
            this.hasUrl = copy.hasUrl;
        }

        public Builder setUsername(final String val) {
            username = val;
            return this;
        }

        public Builder setGroup(final String val) {
            group = val;
            return this;
        }

        public Builder setSticker(final String val) {
            sticker = val;
            return this;
        }

        public Builder setBotUser(final String val) {
            botUser = val;
            return this;
        }

        public Builder setBotChat(final String val) {
            botChat = val;
            return this;
        }

        public Builder setMessage(final String val) {
            message = val;
            return this;
        }

        public Builder setMessageId(final Integer val) {
            messageId = val;
            return this;
        }

        public Builder setHasUrl(final boolean val) {
            hasUrl = val;
            return this;
        }

        public TelegramDeepLinkParsed build() {
            return new TelegramDeepLinkParsed(this);
        }

    }

}
