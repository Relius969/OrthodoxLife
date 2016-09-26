package org.telegram.pravzhizn.pravzhizn;

import android.net.Uri;

import java.util.List;

/**
 * Created by matelskyvv on 5/31/16.
 */
public class RemoteChurch {

    public static class ImageObject {
        public String url;
        public String thumb_url;
        public String big_url;
    }

    public static class TechranchChatObject {
        public int id;
        public int user_id;
        public int temple_id;
        public Uri invitation_link;
        public int type;
    }

    public int id;
    public String title;
    public String content;
    public String address;
    public String contact_phones;
    public String contact_emails;
    public String site;
    public Double map_lat;
    public Double map_lng;
    public List<ImageObject> images;
    public List<TechranchChatObject> chats;

    public boolean is_my_temple;

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RemoteChurch))
            return false;
        if (obj == this)
            return true;

        RemoteChurch rhs = (RemoteChurch) obj;
        return rhs.id == id;
    }

}
