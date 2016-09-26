package org.telegram.pravzhizn.pravzhizn.responses;

/**
 * Created by matelskyvv on 6/27/16.
 */
public class SimpleResponse {

    public static class AddChatResponse extends SimpleResponse {  }

    public static class AddMyTempleResponse extends SimpleResponse {  }

    public static class RemoveMyTempleResponse extends SimpleResponse {  }

    public static class SendCreateChatRequest extends SimpleResponse {
        public String message;
    }

    public boolean success;

}
