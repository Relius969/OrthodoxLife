package org.telegram.pravzhizn.pravzhizn.responses;

import org.telegram.pravzhizn.pravzhizn.CountryObject;

import java.util.List;

/**
 * Created by vlad on 8/29/16.
 */
public class CountriesResponse {

    public boolean success;
    public CountriesResponseData data;
    public String message;

    public static class CountriesResponseData {
        public List<CountryObject> items;

        public int count;
    }

}
