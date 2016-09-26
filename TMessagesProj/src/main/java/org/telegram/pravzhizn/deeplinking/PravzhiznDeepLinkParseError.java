package org.telegram.pravzhizn.deeplinking;

/**
 * Created by vlad on 9/19/16.
 */
public class PravzhiznDeepLinkParseError implements ActionViewParsed {
    @Override
    public void apply(final Visitor visitor) {
        visitor.apply(this);
    }
}
