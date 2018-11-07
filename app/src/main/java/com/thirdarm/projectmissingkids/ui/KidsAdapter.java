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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.R;
import com.thirdarm.projectmissingkids.data.model.MissingKid;

import java.util.Date;

public class KidsAdapter extends PagedListAdapter<MissingKid, RecyclerView.ViewHolder> {

    private final Context mContext;
    final private KidsAdapterOnClickHandler mClickHandler;

    /**
     * The id is the NCMC number to identify the kid.
     */
    public interface KidsAdapterOnClickHandler {
        void onClick(String orgPrefix, String caseNum);
    }

    /**
     * ItemCallback used for paging, which basically refreshes pertinent rows/items based on whether
     * items are the same or different
     */
    private static DiffUtil.ItemCallback<MissingKid> DIFF_CALLBACK = new DiffUtil.ItemCallback<MissingKid>() {
        @Override
        public boolean areItemsTheSame(@NonNull MissingKid oldItem, @NonNull MissingKid newItem) {
            boolean same = oldItem.uid == newItem.uid;
            //Log.d("KidsAdapter", String.format("Are items the same? %s old %s - new %s", same, oldItem.firstName, newItem.firstName));
            return same;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MissingKid oldItem, @NonNull MissingKid newItem) {
            boolean same = oldItem.orgPrefixCaseNumber.equals(newItem.orgPrefixCaseNumber);
            //Log.d("KidsAdapter", String.format("Are contents the same? %s old %s - new %s", same, oldItem.firstName, newItem.firstName));
            // if this is false, then the items are rewritten and the pertinent views are refreshed
            // So we need to properly define when two items are "the same", as returning false
            // may overload the adapter with unnecessary view refreshes
            return same;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.kids_list_item, parent, false);
        return new KidsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MissingKid kid = getItem(position);
        if (kid != null) {
            ((KidsAdapterViewHolder) holder).bind(kid);
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
