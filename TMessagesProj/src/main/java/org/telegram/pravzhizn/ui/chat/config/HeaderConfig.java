package org.telegram.pravzhizn.ui.chat.config;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by matelskyvv on 8/20/16.
 */
public interface HeaderConfig {

    FrameLayout tab();

    View underline();

    TextView unreadMessagesLabel();
}
