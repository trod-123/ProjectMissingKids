package com.thirdarm.projectmissingkids.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.R;
import com.thirdarm.projectmissingkids.data.model.MissingKid;
import com.thirdarm.projectmissingkids.util.NetworkState;

import java.util.Date;

public class KidsAdapter extends PagedListAdapter<MissingKid, RecyclerView.ViewHolder> {

    private final Context mContext;
    final private KidsAdapterOnClickHandler mClickHandler;
    private NetworkState mNetworkState;

    // 2 layout types

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * The id is the NCMC number to identify the kid.
     */
    public interface KidsAdapterOnClickHandler {
        void onClick(String orgPrefix, String caseNum);
    }

    public static DiffUtil.ItemCallback<MissingKid> DIFF_CALLBACK = new DiffUtil.ItemCallback<MissingKid>() {
        @Override
        public boolean areItemsTheSame(@NonNull MissingKid oldItem, @NonNull MissingKid newItem) {
            return oldItem.uid == newItem.uid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MissingKid oldItem, @NonNull MissingKid newItem) {
            return oldItem.equals(newItem);
        }
    };

    public KidsAdapter(Context context, KidsAdapterOnClickHandler clickHandler) {
        super(DIFF_CALLBACK);
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.kids_list_item, parent, false);
            return new KidsAdapterViewHolder(view);
        } else if (viewType == TYPE_PROGRESS) {
            View view = inflater.inflate(R.layout.loading_list_item, parent, false);
            return new NetworkIndicatorViewHolder(view);
        } else {
            throw new IllegalArgumentException("Unknown viewType passed: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof KidsAdapterViewHolder) {
            MissingKid kid = getItem(position);
            if (kid != null) {
                ((KidsAdapterViewHolder) holder).bind(kid);
            } else {
                // TODO: Handle null case (clear the viewholder)
            }
        } else if (holder instanceof NetworkIndicatorViewHolder) {
            ((NetworkIndicatorViewHolder) holder).bind(mNetworkState);
        } else {
            throw new IllegalArgumentException("Unknown viewHolder type passed: " + holder.getItemViewType());
        }
    }

    /**
     * For updating the network state, used to show an extra list item or not for the network state
     *
     * @param newNetworkState
     */
    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    /*
     * For classifying view types
     */
    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * Add an extra item for the network state, if available and not loaded
     *
     * @return
     */
    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    /**
     * For displaying network indicators when pages are loading or have failed to load
     * TODO: Consider using SwipeRefreshLayout to house the RecyclerView, and use showRefreshing()
     * instead (all NetworkIndicator stuff in here will need to be done in MainActivity instead,
     * and deleted here
     */
    class NetworkIndicatorViewHolder extends RecyclerView.ViewHolder {
        final ProgressBar pb;
        final TextView tvNetworkStatus;

        public NetworkIndicatorViewHolder(View view) {
            super(view);
            pb = view.findViewById(R.id.pb_loading_list_item);
            tvNetworkStatus = view.findViewById(R.id.tv_loading_list_item);
        }

        private void bind(NetworkState networkState) {
            if (networkState.getStatus() == NetworkState.Status.FAILED) {
                pb.setVisibility(View.GONE);
            } else {
                pb.setVisibility(View.VISIBLE);
            }
            tvNetworkStatus.setText(networkState.getMsg());
        }
    }

    /**
     * For displaying kid info
     */
    class KidsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView thumbnailView;

        final TextView nameView;
        final TextView idView;
        final TextView ageView;
        final TextView missingDateView;
        final TextView locationView;

        KidsAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            thumbnailView = view.findViewById(R.id.image_small);

            nameView = view.findViewById(R.id.name);
            idView = view.findViewById(R.id.ncmc_id);
            ageView = view.findViewById(R.id.age);
            missingDateView = view.findViewById(R.id.missing_date);
            locationView = view.findViewById(R.id.location);
        }

        /**
         * Binds the view with data
         *
         * @param kid
         */
        private void bind(MissingKid kid) {

            /****************
             * KID IMAGE    *
             ****************/
            String picUrl = "http://api.missingkids.org" + kid.originalPhotoUrl;
            Picasso.get().load(picUrl).fit().into(thumbnailView);

            /********
             * NAME *
             ********/
            String name = kid.firstName + " " +
                    kid.middleName + " " +
                    kid.lastName;

            // Below gets rid of double spaces if there's no middle name
            String formatedName = name.replaceAll("\\s{2,}", " ").trim();
            nameView.setText(formatedName);

            /******************
             * NCMC ID NUMBER *
             ******************/
            idView.setText(kid.orgPrefix + kid.caseNum);

            /*******
             * AGE *
             *******/
            if (kid.age > -1) {
                String age = Long.toString(kid.age);
                ageView.setText("Age: " + age);
                ageView.setVisibility(View.VISIBLE);
            } else {
                ageView.setVisibility(View.INVISIBLE);
            }

            /****************
             * MISSING DATE *
             ****************/
            long millisecond2 = kid.dateMissing;
            String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
            missingDateView.setText("Missing: " + missingDateString);

            /************
             * LOCATION *
             ************/
            String location = kid.locCity + ", " +
                    kid.locState + ", " +
                    kid.locCountry + " ";
            locationView.setText("Location: " + location);
        }

        /**
         * This gets called by the child views during a click.
         * It will get the NCMC ID and pass it to onClick.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            // Ensure that item exists even through list swapping, or else app would crash
            if (adapterPosition != RecyclerView.NO_POSITION) {
                MissingKid kid = getItem(adapterPosition);
                if (kid == null) {
                    return;
                }
                String caseNum = kid.caseNum;
                String orgPrefix = kid.orgPrefix;
                mClickHandler.onClick(orgPrefix, caseNum);
            } else {
                Toast.makeText(view.getContext(), "Click error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
