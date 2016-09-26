package org.telegram.pravzhizn.ui.churches.mode.list;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.churches.mode.EndlessScrollListener;
import org.telegram.pravzhizn.ui.my_churches.MyChurchesActivityHelper;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

/**
 * Created by vlad on 6/5/16.
 */
public class ChurchesView {

    private final SwipeRefreshLayout mRefreshLayout;

    public View content() {
        return mContent;
    }

    private final LinearLayout mContent;

    private View noChurchesView;
    private TextView selectedCityView;
    private TextView selectedCountryView;
    private RecyclerListView mListView;
    private LinearLayoutManager mLayoutManager;

    private ProgressBar mProgressView;
    private ImageView mFloatingButton;
    private int mPrevTop;
    private boolean mScrollUpdated;
    private boolean mFloatingHidden;
    private final AccelerateDecelerateInterpolator mFloatingInterpolator = new AccelerateDecelerateInterpolator();

    private int mPrevPosition;

    private ChurchesPresenter mPresenter;

    public ChurchesView(final Context context, final ChurchesPresenter presenter) {
        mPresenter = presenter;

        mContent = new LinearLayout(context);
        mContent.setOrientation(LinearLayout.VERTICAL);

        /****** selected location header ********/
        final View selectLocationHeader = MyChurchesActivityHelper.createSelectLocationHeader(context);
        selectedCityView = (TextView) selectLocationHeader.findViewById(R.id.selected_city);
        selectedCityView.setPaintFlags(selectedCityView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        selectedCityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPresenter.onSelectCityClicked();
            }
        });

        selectedCountryView = (TextView) selectLocationHeader.findViewById(R.id.selected_country);
        selectedCountryView.setPaintFlags(selectedCountryView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        selectedCountryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPresenter.onSelectCountryClicked();
            }
        });

        mContent.addView(selectLocationHeader);
        /***********************************/

        /***** content layout **************/
        final FrameLayout contentLayout = new FrameLayout(context);
        mRefreshLayout = new SwipeRefreshLayout(context);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefreshRequested();
            }
        });
        addListView(context, mRefreshLayout);
        contentLayout.addView(mRefreshLayout);

        noChurchesView = configureEmptyView(context);
        contentLayout.addView(noChurchesView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        addFloatingActionButton(context, contentLayout);
        addProgressView(context, contentLayout);

        mContent.addView(contentLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void addListView(final Context context, final ViewGroup contentLayout) {
        mListView = new RecyclerListView(context);

        mListView.setVerticalScrollBarEnabled(true);
        mListView.setItemAnimator(null);
        mListView.setInstantClick(true);
        mListView.setLayoutAnimation(null);
        mLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(mLayoutManager);
        if (Build.VERSION.SDK_INT >= 11) {
            mListView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }

        mListView.setOnScrollListener(new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (mFloatingButton.getVisibility() != View.GONE) {
                    final View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean goingDown;
                    boolean changed = true;
                    if (mPrevPosition == firstVisibleItem) {
                        final int topDelta = mPrevTop - firstViewTop;
                        goingDown = firstViewTop < mPrevTop;
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > mPrevPosition;
                    }
                    if (changed && mScrollUpdated) {
                        hideFloatingButton(goingDown);
                    }
                    mPrevPosition = firstVisibleItem;
                    mPrevTop = firstViewTop;
                    mScrollUpdated = true;
                }
            }

            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                if (mPresenter != null) {
                    mPresenter.onLoadMore(page, totalItemsCount);
                }
            }
        });

        contentLayout.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    public View getNoChurchesView() {
        return noChurchesView;
    }

    public TextView getSelectedCityView() {
        return selectedCityView;
    }

    public TextView getSelectedCountryView() {
        return selectedCountryView;
    }

    public RecyclerListView getListView() {
        return mListView;
    }

    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public ProgressBar getProgressView() {
        return mProgressView;
    }

    public ImageView getFloatingButton() {
        return mFloatingButton;
    }

    public boolean isFloatingHidden() {
        return mFloatingHidden;
    }

    public RecyclerListView listView() {
        return mListView;
    }

    public void hideListView() {
        mListView.setVisibility(View.GONE);
    }

    public void showListView() {
        mListView.setVisibility(View.VISIBLE);
    }

    public void hideFAB() {
        mFloatingButton.setVisibility(View.GONE);
    }

    public void showFAB() {
        mFloatingButton.setVisibility(View.VISIBLE);
    }

    public void showProgressView() {
        mProgressView.setVisibility(View.VISIBLE);
    }

    public void hideProgressView() {
        mProgressView.setVisibility(View.GONE);
    }

    public void hideNoChurchesView() {
        noChurchesView.setVisibility(View.GONE);
    }

    public void showNoChurchesView() {
        noChurchesView.setVisibility(View.VISIBLE);
    }

    public void displaySelectedCity(String value) {
        selectedCityView.setText(value);
    }

    public void displaySelectedCountry(final String newHeaderCountryText) {
        selectedCountryView.setText(newHeaderCountryText);
    }

    /********************
     * private section
     *************************/

    private void hideFloatingButton(boolean hide) {
        if (mFloatingHidden == hide) {
            return;
        }
        mFloatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFloatingButton, "translationY", mFloatingHidden ? AndroidUtilities.dp(100) : 0).setDuration(300);
        animator.setInterpolator(mFloatingInterpolator);
        mFloatingButton.setClickable(!hide);
        animator.start();
    }

    public View configureEmptyView(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.no_churches_found, null);

        TextView no_churches_found = (TextView) view.findViewById(R.id.no_churches_found);
        no_churches_found.setText(LocaleController.getString("Techranch_No_Churches", R.string.Techranch_No_Churches));

        Button add_church = (Button) view.findViewById(R.id.add_church);
        add_church.setText(LocaleController.getString("Techranch_Add_Church", R.string.Techranch_Add_Church));

        add_church.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPresenter.onAddChurchClicked();
            }
        });

        return view;
    }

    void addFloatingActionButton(
            final Context context,
            final FrameLayout contentLayout) {
        mFloatingButton = new ImageView(context);
        mFloatingButton.setScaleType(ImageView.ScaleType.CENTER);
        mFloatingButton.setImageResource(R.drawable.button_tample);
        contentLayout.addView(mFloatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, 14));
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPresenter.onAddChurchClicked();
            }
        });
    }

    void addProgressView(final Context context, final FrameLayout contentLayout) {
        mProgressView = new ProgressBar(context);
        contentLayout.addView(mProgressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
    }

    public void clearRefreshFlag() {
        mRefreshLayout.setRefreshing(false);
    }
}
