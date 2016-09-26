package org.telegram.pravzhizn.config;

import android.content.SharedPreferences;

import org.telegram.pravzhizn.utils.Factory;
import org.telegram.pravzhizn.pravzhizn.PravzhiznProfesstion;
import org.telegram.pravzhizn.pravzhizn.SaintObject;

import java.util.Date;

/**
 * Created by vlad on 9/5/16.
 */
public class ConfigUtils {

    public static void toSharedPreferences(
            SharedPreferences preferences,
            String idKey,
            String valueKey,
            PravzhiznConfigItem item) {
        final SharedPreferences.Editor editor = preferences.edit();
        toSharedPreferences(editor, idKey, valueKey, item);
        editor.apply();
    }

    public static void toSharedPreferences(
            SharedPreferences.Editor editor,
            String idKey,
            String valueKey,
            PravzhiznConfigItem item) {
        if (item == null) {
            editor.remove(idKey);
            editor.remove(valueKey);
        } else {
            editor.putInt(idKey, item.getId());
            editor.putString(valueKey, item.getValue());
        }
    }

    public static <Item> Item fromSharedPreferences(
            SharedPreferences preferences,
            String idKey,
            String valueKey,
            Factory<Item> creator) {
        if (preferences.contains(idKey)) {
            final int id = preferences.getInt(idKey, -1);
            final String value = preferences.getString(valueKey, "");
            return creator.create(id, value);
        }

        return null;
    }

    public static Date getDate(final SharedPreferences preferences, final String key) {
        return new Date(preferences.getLong(key, 0));
    }

    public static void storeDate(
            final SharedPreferences preferences,
            final String key,
            final Date date) {
        final SharedPreferences.Editor editor = preferences.edit();
        storeDate(editor, key, date);
        editor.apply();
    }

    public static void storeDate(
            final SharedPreferences.Editor editor,
            final String key,
            final Date date) {

        if (date == null) {
            editor.remove(key);
        } else {
            editor.putLong(key, date.getTime());
        }
    }

    public static PravzhiznConfigItem toConfigItem(final PravzhiznProfesstion value) {
        if (value == null) {
            return null;
        }

        return new PravzhiznConfigItem() {
            @Override
            public int getId() {
                return value.id;
            }

            @Override
            public String getValue() {
                return value.name;
            }
        };
    }

    public static PravzhiznConfigItem toConfigItem(final SaintObject value) {
        if (value == null) {
            return null;
        }

        return new PravzhiznConfigItem() {
            @Override
            public int getId() {
                return value.id;
            }

            @Override
            public String getValue() {
                return value.name;
            }
        };
    }
}
