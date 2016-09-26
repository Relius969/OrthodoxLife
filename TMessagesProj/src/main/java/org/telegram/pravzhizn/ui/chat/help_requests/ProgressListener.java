package org.telegram.pravzhizn.ui.chat.help_requests;

/**
 * Created by Vlad on 8/20/16.
 */
public interface ProgressListener {

    void onStart(int reqId);
    void onFinished();

    // if activity is finishing
    boolean isFinishing();

}
