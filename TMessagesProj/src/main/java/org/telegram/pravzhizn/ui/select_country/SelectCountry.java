package org.telegram.pravzhizn.ui.select_country;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.CountryObject;
import org.telegram.pravzhizn.ui.churches.mode.EndlessScrollListener;
import org.telegram.pravzhizn.ui.select_city.OpenScenario;
import org.telegram.pravzhizn.ui.select_city.SelectCity;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by Vlad on 8/29/16.
 */
public class SelectCountry extends BaseFragment implements OnCountryClickedListener {

    private static final java.lang.String SCREEN_NAME = "Select country";
    private final OpenScenario mOpenScenario;
    private RecyclerListView mListView;
    private LinearLayoutManager mLayoutManager;

    TextView mSelectedCountry;

    PravzhiznConfig mConfig;

    private CountriesEndlessAdapter mCitiesAdapter;

    private CountriesEndlessAdapter mCurrentAdapter;

    public SelectCountry(final OpenScenario openScenario) {
        mOpenScenario = openScenario;
    }

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(final Context context) {

        actionBar.setTitle(LocaleController.getString("techrunch_select_country", R.string.techrunch_select_country));
        actionBar.setAddToContainer(false);
        ActionBarMenu menu = actionBar.createMenu();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (mOpenScenario == OpenScenario.InitialLaunch) {
                        presentFragment(new DialogsActivity(null), true);
                    } else {
                        finishFragment();
                    }
                }
            }
        });
        final ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
            }

            @Override
            public boolean canCollapseSearch() {
                return true;
            }

            @Override
            public void onSearchCollapse() {
                updateAdapter(mCitiesAdapter);
            }

            @Override
            public void onTextChanged(EditText editText) {
                final String text = editText.getText().toString().trim();
                updateAdapter(new CountriesEndlessAdapter(text, context, SelectCountry.this));
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));


        LinearLayout layout = new LinearLayout(context) {
            @Override
            protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
                if (child == mListView) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (parentLayout != null) {
                        int actionBarHeight = 0;
                        int childCount = getChildCount();
                        for (int a = 0; a < childCount; a++) {
                            View view = getChildAt(a);
                            if (view == child) {
                                continue;
                            }
                            if (view instanceof ActionBar && view.getVisibility() == VISIBLE) {
                                if (((ActionBar) view).getCastShadows()) {
                                    actionBarHeight = view.getMeasuredHeight();
                                }
                                break;
                            }
                        }
                        parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                    }
                    return result;
                } else {
                    return super.drawChild(canvas, child, drawingTime);
                }
            }
        };

        layout.addView(actionBar);

        layout.setOrientation(LinearLayout.VERTICAL);
        fragmentView = layout;

        mListView = new RecyclerListView(context);
        mListView.setVerticalScrollBarEnabled(true);
        mListView.setItemAnimator(null);
        mListView.setLayoutAnimation(null);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(mLayoutManager);
        if (Build.VERSION.SDK_INT >= 11) {
            mListView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }

        mCitiesAdapter = new CountriesEndlessAdapter(context, this);
        updateAdapter(mCitiesAdapter);
        mListView.setOnScrollListener(new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                mCurrentAdapter.loadMore(page, totalItemsCount);
            }
        });

        LinearLayout subheader = new LinearLayout(context);
        subheader.setOrientation(LinearLayout.VERTICAL);
        subheader.setBackgroundColor(context.getResources().getColor(R.color.techranch_primary_color));

        mSelectedCountry = new TextView(context);
        mSelectedCountry.setGravity(Gravity.CENTER_HORIZONTAL);
        mSelectedCountry.setTextColor(Color.WHITE);
        mSelectedCountry.setTextSize(context.getResources().getDimension(R.dimen.techranch_selected_city_text_cize));
        mConfig = new PravzhiznConfig(context);
        setCountryFromConfig();

        TextView selectedCityHeader = new TextView(context);
        selectedCityHeader.setGravity(Gravity.CENTER_HORIZONTAL);
        selectedCityHeader.setTextColor(Color.WHITE);
        selectedCityHeader.setText(LocaleController.getString("techrunch_your_country", R.string.techrunch_your_country));

        subheader.addView(selectedCityHeader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        subheader.addView(mSelectedCountry, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        layout.addView(subheader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        RelativeLayout container = new RelativeLayout(context);

        View shadowView = new View(context);
        shadowView.setBackgroundResource(R.drawable.header_shadow);
        container.addView(shadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 3));

        container.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        layout.addView(container);

        return fragmentView;
    }

    void updateAdapter(CountriesEndlessAdapter adapter) {
        mCurrentAdapter = adapter;
        mListView.setAdapter(adapter);
    }

    private void setCountryFromConfig() {
        mSelectedCountry.setText(mConfig.getSelectedCountry().name);
    }

    @Override
    public void onCountrySelected(final CountryObject country) {
        presentFragment(new SelectCity(mOpenScenario, country), true);
    }
}