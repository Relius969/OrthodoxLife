package org.telegram.pravzhizn.pravzhizn.responses;

import org.telegram.pravzhizn.pravzhizn.CityObject;

import java.util.List;

/**
 * Created by vlad on 8/26/16.
 */
public class CitiesResponse {

    public boolean success;
    public CitiesResponseData data;
    public String message;

    public static class CitiesResponseData {
        public List<CityObject> items;

        public int count;
    }



}
