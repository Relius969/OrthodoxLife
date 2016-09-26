package org.telegram.pravzhizn.ui.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.AddMyTempleResponse;
import org.telegram.pravzhizn.pravzhizn.responses.TempleDetailsResponse;
import org.telegram.pravzhizn.ui.chat.ChurchChat;
import org.telegram.pravzhizn.ui.chat.ChurchChat.Tab;
import org.telegram.pravzhizn.ui.details.ChurchDetailsAdapter.Listener;
import org.telegram.pravzhizn.ui.photos.PhotosActivity;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by vlad on 5/29/16.
 */
public class ChurchDetails extends BaseFragment {

    private static final String SCREEN_NAME = "Church details";

    public static int add_to_my_churches = 10;
    public static int share_church = 11;

    private RemoteChurch mChurch;
    private final PravzhiznService mService;
    private RecyclerListView mListView;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mBottomButtonsContainer;
    private Button mGroup;
    private Button mChannel;
    private ActionBarMenuItem mMyChurchesItem;

    public ChurchDetails(RemoteChurch church) {
        this(church.id);
    }

    public ChurchDetails(int churchId) {
        mService = PravzhiznService.instance.create(PravzhiznService.class);
        mService.templeDetails(churchId).enqueue(new Callback<TempleDetailsResponse>() {
            @Override
            public void onResponse(final Call<TempleDetailsResponse> call, final Response<TempleDetailsResponse> response) {
                if (response.isSuccessful() && response.body().success) {
                    mChurch = response.body().data;
                    initWithChurch(mChurch);
                }
            }

            @Override
            public void onFailure(final Call<TempleDetailsResponse> call, final Throwable t) {
                finishFragment(true);
            }
        });

        trackScreenName(SCREEN_NAME);
    }

    private void initWithChurch(final RemoteChurch church) {
        mListView.setAdapter(new ChurchDetailsAdapter(church, new Listener() {
            @Override
            public void onPhotosClicked() {
                presentFragment(new PhotosActivity(mChurch.images));
            }
        }));

        mGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                presentFragment(new ChurchChat(church, Tab.Group));
            }
        });

        mChannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                presentFragment(new ChurchChat(church, Tab.Channel));
            }
        });

        updateIsMyTempleIcon(mMyChurchesItem);
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Techranch_Church_Details", R.string.Techranch_Church_Details));

        final ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(share_church, R.drawable.share);
        mMyChurchesItem = menu.addItem(add_to_my_churches, R.drawable.plus);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == add_to_my_churches) {
                    if (mChurch.is_my_temple) {
                        askForRemovingChurchFromMyTemple(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                removeChurchFromMyTemples(mMyChurchesItem);
                            }
                        });
                    } else {
                        addChurchToMyTemples(mMyChurchesItem);
                    }
                } else if (id == share_church) {
                    if (mChurch != null) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, "https://pravzhizn.ru/hramy/join_temple/" + mChurch.id);
                            final String message = LocaleController.getString("InviteToChurchByLink", R.string.InviteToChurchByLink);
                            startActivityForResult(Intent.createChooser(intent, message), 500);
                        } catch (Exception e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                }
            }
        });

        FrameLayout topLayout = new FrameLayout(context);
        fragmentView = topLayout;

        configureListView(context);

        FrameLayout listViewContainer = new FrameLayout(context);
        listViewContainer.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        initBottomButtonsContainer(context);


        final int bottomMargin = 48;
        final FrameLayout.LayoutParams bottomButtonsLp = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, bottomMargin, Gravity.BOTTOM);
        topLayout.addView(mBottomButtonsContainer, bottomButtonsLp);

        final FrameLayout.LayoutParams lp = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        lp.setMargins(0, 0, 0, AndroidUtilities.dp(bottomMargin));
        topLayout.addView(listViewContainer, lp);

        return fragmentView;
    }

    private void addChurchToMyTemples(final ActionBarMenuItem menuItem) {
        menuItem.setEnabled(false);
        mService.addMyTemple(mChurch.id).enqueue(new Callback<AddMyTempleResponse>() {
            @Override
            public void onResponse(final Call<AddMyTempleResponse> call, final Response<AddMyTempleResponse> response) {
                menuItem.setEnabled(true);
                mChurch.is_my_temple = true;
                updateIsMyTempleIcon(menuItem);
                Toast.makeText(getParentActivity(), LocaleController.getString("techranch_temple_is_added", R.string.techranch_church_is_added), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(final Call<AddMyTempleResponse> call, final Throwable t) {
                menuItem.setEnabled(true);
            }
        });
    }

    private void askForRemovingChurchFromMyTemple(final DialogInterface.OnClickListener onConfirmClicked) {
        if (getParentActivity() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("Techranch_Remove_Church", R.string.Techranch_Remove_Church));
        builder.setNegativeButton(LocaleController.getString("Techranch_Cancel", R.string.Techranch_Cancel), null);
        builder.setPositiveButton(LocaleController.getString("Techranch_Confirm", R.string.Techranch_Confirm), onConfirmClicked);

        showDialog(builder.create());
    }

    private void removeChurchFromMyTemples(final ActionBarMenuItem menuItem) {
        menuItem.setEnabled(false);
        mService.removeMyTemple(mChurch.id).enqueue(new Callback<SimpleResponse.RemoveMyTempleResponse>() {
            @Override
            public void onResponse(final Call<SimpleResponse.RemoveMyTempleResponse> call, final Response<SimpleResponse.RemoveMyTempleResponse> response) {
                // menuItem.setEnabled(true);
                mChurch.is_my_temple = false;
                updateIsMyTempleIcon(menuItem);
                Toast.makeText(getParentActivity(), LocaleController.getString("techranch_temple_is_removed", R.string.techranch_temple_is_removed), Toast.LENGTH_SHORT).show();

                NotificationCenter.getInstance().postNotificationName(NotificationCenter.churchIsRemovedFromMyChurches, mChurch);
                finishFragment(true);
            }

            @Override
            public void onFailure(final Call<SimpleResponse.RemoveMyTempleResponse> call, final Throwable t) {
                menuItem.setEnabled(true);
            }
        });
    }

    private void updateIsMyTempleIcon(final ActionBarMenuItem menuItem) {
        if (mChurch.is_my_temple) {
            menuItem.setIcon(R.drawable.delete_reply);
        } else {
            menuItem.setIcon(R.drawable.plus);
        }
    }

    void initBottomButtonsContainer(final Context context) {
        mBottomButtonsContainer = new LinearLayout(context);
        mBottomButtonsContainer.setOrientation(LinearLayout.HORIZONTAL);

        mGroup = new Button(context);
        mGroup.setGravity(Gravity.CENTER);
        mGroup.setText(LocaleController.getString("techranch_group", R.string.techranch_group));
        mGroup.setBackgroundColor(context.getResources().getColor(R.color.techranch_primary_color));
        mGroup.setTextColor(Color.WHITE);

        mChannel = new Button(context);
        mChannel.setGravity(Gravity.CENTER);
        mChannel.setText(LocaleController.getString("techranch_channel", R.string.techranch_channel));
        mChannel.setBackgroundColor(context.getResources().getColor(R.color.techranch_primary_color));
        mChannel.setTextColor(Color.WHITE);

        mBottomButtonsContainer.addView(mGroup, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 1.f));
        mBottomButtonsContainer.addView(mChannel, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 1.f));

    }

    void configureListView(final Context context) {
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
    }
}
