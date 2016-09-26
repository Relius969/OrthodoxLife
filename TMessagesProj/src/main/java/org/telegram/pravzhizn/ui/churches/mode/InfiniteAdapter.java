package org.telegram.pravzhizn.ui.churches.mode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.ui.churches.ChurchViewHolder;
import org.telegram.pravzhizn.ui.churches.ChurchesPresenterListener;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByCity;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByLocation;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByName;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.MyTemples;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.Visitor;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.pravzhizn.pravzhizn.responses.TemplesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vlad on 6/5/16.
 */
public class InfiniteAdapter extends RecyclerView.Adapter<ChurchViewHolder> {

    private int RUS_ID = 149;

    public interface Listener extends ChurchesPresenterListener {
        void onStartLoading();
        void onFinishLoading();
    }

    public static final int LIMIT = 20;

    private final Context mContext;
    private final PravzhiznService mService;
    private final Listener mListener;

    private List<RemoteChurch> mData = new ArrayList<>();
    private int mTotalItemsCount;
    private Call<TemplesResponse> mCurrentRequest;
    private ChurchQuery mCurrentQuery;

    public InfiniteAdapter(
            Context context,
            final PravzhiznService service,
            Listener listener ) {
        mContext = context;
        mService = service;
        mListener = listener;
    }

    public void removeChurch(RemoteChurch church) {
        int position = mData.indexOf(church);
        if (position > -1) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public ChurchViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.techranch_church_list_item, parent, false);
        return new ChurchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChurchViewHolder holder, final int position) {
        RemoteChurch church = mData.get(position);
        holder.bind(church, position, mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void query(final ChurchQuery query) {
        mCurrentQuery = query;
        mCurrentQuery.accept(new Visitor() {
            @Override
            public void visit(final ByLocation byLocation) {
                queryByLocation(byLocation.mLat, byLocation.mLng, LIMIT, 0, false);
            }

            @Override
            public void visit(final ByName byName) {
                queryByName(byName.mName, LIMIT, 0, false);
            }

            @Override
            public void visit(final ByCity byCity) {
                queryByCity(byCity.mCity, LIMIT, 0, false);
            }

            @Override
            public void visit(final MyTemples myTemples) {
                queryMyTemples(LIMIT, 0, false);
            }
        });
    }

    private void queryMyTemples(int limit, int offset, final boolean isLoadMoreRequest) {
        if (!isLoadMoreRequest) {
            mListener.onStartLoading();
        }
        cancelCurrent();
        mCurrentRequest = mService.myTemples(limit, offset);
        enqueue(mCurrentRequest, isLoadMoreRequest);
    }

    private void queryByName(String term, int limit, int offset, final boolean isLoadMoreRequest) {
        if (!isLoadMoreRequest) {
            mListener.onStartLoading();
        }
        cancelCurrent();
        mCurrentRequest = mService.templesByName(term, limit, offset);
        enqueue(mCurrentRequest, isLoadMoreRequest);
    }

    private void queryByCity(CityObject city, int limit, int offset, final boolean isLoadMoreRequest) {
        if (!isLoadMoreRequest) {
            mListener.onStartLoading();
        }
        cancelCurrent();

        PravzhiznConfig config = new PravzhiznConfig(mContext);
        final int countryId;
        if (config.isCountrySelected()) {
            countryId = config.getSelectedCountry().id;
        } else {
            countryId = RUS_ID;
        }

        mCurrentRequest = mService.templesByCity(countryId, city.id, limit, offset);
        enqueue(mCurrentRequest, isLoadMoreRequest);
    }

    private void queryByLocation(Double lat, Double lng, int limit, int offset, final boolean isLoadMoreRequest) {
        cancelCurrent();
        mCurrentRequest = mService.templesByRadius(lat, lng, limit, offset);
        enqueue(mCurrentRequest, isLoadMoreRequest);
    }

    private void enqueue(final Call<TemplesResponse> request, final boolean isLoadMoreRequest) {
        request.enqueue(new Callback<TemplesResponse>() {
            @Override
            public void onResponse(final Call<TemplesResponse> call, final Response<TemplesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().success) {
                        final List<RemoteChurch> items = response.body().items;

                        if (isLoadMoreRequest) {
                            int curSize = getItemCount();
                            mData.addAll(items);
                            notifyItemRangeInserted(curSize, items.size() - 1);
                        } else {
                            mData = new ArrayList<>(items);
                            mTotalItemsCount = response.body().count;
                            notifyDataSetChanged();
                        }
                    }
                }

                mListener.onFinishLoading();
            }

            @Override
            public void onFailure(final Call<TemplesResponse> call, final Throwable t) {
                mListener.onFinishLoading();
                notifyDataSetChanged();
            }
        });
    }

    private void cancelCurrent() {
        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
            mCurrentRequest = null;
        }
    }

    public void queryNextPage(final int totalItemsCount) {
        if (mTotalItemsCount <= totalItemsCount) return;

        if (mCurrentQuery != null) {
            mCurrentQuery.accept(new Visitor() {
                @Override
                public void visit(final ByLocation byLocation) {
                    queryByLocation(byLocation.mLat, byLocation.mLng, LIMIT, getItemCount(), true);
                }

                @Override
                public void visit(final ByName byName) {
                    queryByName(byName.mName, LIMIT, getItemCount(), true);
                }

                @Override
                public void visit(final ByCity byCity) {
                    queryByCity(byCity.mCity, LIMIT, getItemCount(), true);
                }

                @Override
                public void visit(final MyTemples myTemples) {
                    queryMyTemples(LIMIT, getItemCount(), true);
                }
            });
        }
    }

}
