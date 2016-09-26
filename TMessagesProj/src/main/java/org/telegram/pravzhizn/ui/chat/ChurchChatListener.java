package org.telegram.pravzhizn.ui.chat;

/**
 * Interface to interact with ChurchChat
 *
 * Created by Vlad on 8/20/16.
 */
public interface ChurchChatListener {

    void sendRequestToCreateGroup();
    void sendRequestToCreateChannel();

    void onOpenGroupSettingsOptionClicked();
    void onChannelSettingsClicked();

}
