package org.telegram.pravzhizn.deeplinking;

/**
 * Created by vlad on 9/19/16.
 */
public interface ActionViewParsed {

    interface Visitor {
        void apply(TelegramDeepLinkParsed value);

        void apply(PravzhiznDeepLinkParsed value);

        void apply(PravzhiznDeepLinkParseError value);
    }

    void apply(Visitor visitor);
}
