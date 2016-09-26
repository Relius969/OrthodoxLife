package org.telegram.pravzhizn.utils;

import java.util.Locale;

/**
 * Created by matelskyvv on 6/6/16.
 */
public class YandexMapsStaticAPI {

    private static final String linkTemplate = "http://static-maps.yandex.ru/1.x/?lang=ru-RU&ll=%f,%f&size=%d,%d&z=14&l=map&pt=%f,%f,pm2rdl1";

    public static String buildMapLink(Double lat, Double lng, int width, int height) {
        return String.format(Locale.ENGLISH, linkTemplate, lat, lng, width, height, lat, lng);
    }
}
