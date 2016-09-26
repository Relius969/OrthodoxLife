package org.telegram.pravzhizn.deeplinking;

/**
 * Created by vlad on 9/19/16.
 */
public class PravzhiznDeepLinkParsed implements ActionViewParsed {

    private final int mChurchId;

    public PravzhiznDeepLinkParsed(final int churchId) {
        mChurchId = churchId;
    }

    public int getChurchId() {
        return mChurchId;
    }

    @Override
    public void apply(final Visitor visitor) {
        visitor.apply(this);
    }
}
