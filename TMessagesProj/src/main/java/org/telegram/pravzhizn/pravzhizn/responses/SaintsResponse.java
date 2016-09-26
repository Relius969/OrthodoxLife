package org.telegram.pravzhizn.pravzhizn.responses;

import org.telegram.pravzhizn.pravzhizn.SaintObject;

import java.util.List;

/**
 * Created by vlad on 9/8/16.
 */
public class SaintsResponse {

    public boolean success;
    public SaintsResponseData data;
    public String message;

    public static class SaintsResponseData {
        public List<SaintObject> items;
    }
}
