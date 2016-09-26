package org.telegram.pravzhizn.ui.churches;

import org.telegram.pravzhizn.pravzhizn.RemoteChurch;

/**
 * Created by vlad on 5/31/16.
 */
public interface ChurchesPresenterListener {

    void onItemSelected(RemoteChurch selectedChurch);

    void onLongClicked(int position, RemoteChurch selectedChurch);

    void onAddChurchClicked();
    void onSelectCityClicked();
    void onSelectCountryClicked();

}
