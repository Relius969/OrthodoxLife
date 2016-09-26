package org.telegram.pravzhizn.utils;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;

import java.util.List;

/**
 * Created by matelskyvv on 7/18/16.
 */
public class PhotosCount {

    public static String photosLabelText(List list) {
        final int resId;
        final String resStr;
        if (list.size() == 1) {
            resId = R.string.Techranch_Church_To_Add_Photo_Label;
            resStr = "Techranch_Church_To_Add_Photo_Label";
        } else {
            resId = R.string.Techranch_Church_To_Add_Photos_Label;
            resStr = "Techranch_Church_To_Add_Photos_Label";
        }

        return String.format("%d %s", list.size(), LocaleController.getString(resStr, resId));
    }
}
