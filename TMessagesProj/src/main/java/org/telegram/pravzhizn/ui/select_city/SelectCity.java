package org.telegram.pravzhizn.ui.select_city;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.pravzhizn.pravzhizn.CountryObject;
import org.telegram.pravzhizn.ui.churches.ChurchesActivity;
import org.telegram.pravzhizn.ui.churches.mode.EndlessScrollListener;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

public class SelectCity extends BaseFragment implements OnCityClickedListener {

    private static final java.lang.String SCREEN_NAME = "Select city";
    private final OpenScenario mOpenScenario;
    private final CountryObject mCountry;
    private RecyclerListView mListView;
    private LinearLayoutManager mLayoutManager;

    TextView mSelectedCity;

    Button mClosestToMe;

    PravzhiznConfig mConfig;

    private CitiesEndlessAdapter mCitiesAdapter;

    private CitiesEndlessAdapter mCurrentAdapter;

    public SelectCity(final OpenScenario openScenario, final CountryObject country) {
        mOpenScenario = openScenario;
        mCountry = country;
    }

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(final Context context) {

        actionBar.setTitle(LocaleController.getString("techrunch_select_city", R.string.techrunch_select_city));
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
                mClosestToMe.setVisibility(View.GONE);
            }

            @Override
            public boolean canCollapseSearch() {
                return true;
            }

            @Override
            public void onSearchCollapse() {
                mClosestToMe.setVisibility(View.VISIBLE);
                updateAdapter(mCitiesAdapter);
            }

            @Override
            public void onTextChanged(EditText editText) {
                final String text = editText.getText().toString().trim();
                updateAdapter(new CitiesEndlessAdapter(mCountry, text, context, SelectCity.this));
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

        mCitiesAdapter = new CitiesEndlessAdapter(context, mCountry, this);
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

        mSelectedCity = new TextView(context);
        mSelectedCity.setGravity(Gravity.CENTER_HORIZONTAL);
        mSelectedCity.setTextColor(Color.WHITE);
        mSelectedCity.setTextSize(context.getResources().getDimension(R.dimen.techranch_selected_city_text_cize));
        mConfig = new PravzhiznConfig(context);

        if (mConfig.isCountrySelected()) {
            if ((int)mConfig.getSelectedCountry().id == mCountry.id) {
                setCityFromConfig();
            } else {
                mSelectedCity.setText(context.getString(R.string.techrunch_no_city));
            }
        } else {
            mSelectedCity.setText(context.getString(R.string.techrunch_no_city));
        }

        TextView selectedCityHeader = new TextView(context);
        selectedCityHeader.setGravity(Gravity.CENTER_HORIZONTAL);
        selectedCityHeader.setTextColor(Color.WHITE);
        selectedCityHeader.setText(LocaleController.getString("techrunch_your_city", R.string.techrunch_your_city));

        subheader.addView(selectedCityHeader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        subheader.addView(mSelectedCity, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        layout.addView(subheader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        
        mClosestToMe = new Button(context);
        mClosestToMe.setGravity(Gravity.CENTER);
        mClosestToMe.setText(LocaleController.getString("techranch_closest_to_me", R.string.techranch_closest_to_me));
        mClosestToMe.setBackgroundColor(context.getResources().getColor(R.color.techranch_primary_color));
        mClosestToMe.setTextColor(Color.WHITE);

        mClosestToMe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mConfig.setUseClosestToMe(true);
                navigateFurther();
            }
        });

        RelativeLayout container = new RelativeLayout(context);

        View shadowView = new View(context);
        shadowView.setBackgroundResource(R.drawable.header_shadow);
        container.addView(shadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 3));

        container.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        final int bottomMargin = 48;
        FrameLayout bottomButtonContainer = new FrameLayout(context);
        final FrameLayout.LayoutParams bottomButtonLp = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, bottomMargin, Gravity.BOTTOM);
        bottomButtonContainer.addView(mClosestToMe, bottomButtonLp);

        final FrameLayout.LayoutParams lp = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, AndroidUtilities.dp(bottomMargin));
        bottomButtonContainer.addView(container, lp);

        layout.addView(bottomButtonContainer);

        return fragmentView;
    }

    void updateAdapter(CitiesEndlessAdapter adapter) {
        mCurrentAdapter = adapter;
        mListView.setAdapter(adapter);
    }

    private void setCityFromConfig() {
        mSelectedCity.setText(mConfig.getSelectedCity().name);
    }

    @Override
    public void onCitySelected(final CityObject city) {
        mConfig.setSelectedCity(city);
        mConfig.setSelectedCountry(mCountry);

        setCityFromConfig();
        mConfig.setUseClosestToMe(false);

        mListView.getAdapter().notifyDataSetChanged();

        navigateFurther();
    }

    void navigateFurther() {
        if (mOpenScenario == OpenScenario.InitialLaunch) {
            presentFragment(new ChurchesActivity(mOpenScenario), true);
        } else if (mOpenScenario == OpenScenario.LocationWasNotSelectedOnInitialLaunch) {
            presentFragment(new ChurchesActivity(OpenScenario.Common), true);
        } else {
            finishFragment(true);
        }
    }
}
