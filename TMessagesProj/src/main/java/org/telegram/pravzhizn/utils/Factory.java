package org.telegram.pravzhizn.utils;

/**
 * Created by vlad on 9/5/16.
 */
public interface Factory<Item> {

    Item create(int id, String value);
}
