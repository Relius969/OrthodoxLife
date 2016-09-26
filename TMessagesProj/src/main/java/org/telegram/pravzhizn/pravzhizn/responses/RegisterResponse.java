package org.telegram.pravzhizn.pravzhizn.responses;

/**
 * Created by matelskyvv on 6/20/16.
 */
public class RegisterResponse {

    public boolean success;
    public RegisterResponseData data;
    public String message;

    public static class RegisterResponseData {
        public String token;
    }
}
