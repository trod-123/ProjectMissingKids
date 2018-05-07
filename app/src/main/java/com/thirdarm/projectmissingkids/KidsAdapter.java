package com.thirdarm.projectmissingkids;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.projectmissingkids.data.MissingKid;

import java.util.Date;
import java.util.List;

public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.KidsAdapterViewHolder> {

    private final Context mContext;

    final private KidsAdapterOnClickHandler mClickHandler;
    List<MissingKid> kids;

    /**
     * The id is the NCMC number to identify the kid.
     */
    public interface KidsAdapterOnClickHandler {
        void onClick(String id, String orgPrefix);
    }


    public KidsAdapter(List<MissingKid> kids, Context context, KidsAdapterOnClickHandler clickHandler) {
        this.kids = kids;
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public KidsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kids_list_item, parent, false);
        return new KidsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KidsAdapterViewHolder holder, int position) {

        // Grab all the MissingKid objects by the position they're in.
        MissingKid kid = kids.get(position);

        /****************
         * KID IMAGE    *
         ****************/
        String picUrl = "http://api.missingkids.org" + kid.originalPhotoUrl;
        Picasso.get().load(picUrl).fit().into(holder.thumbnailView);

        /********
         * NAME *
         ********/
        String name = kid.name.firstName + " " +
                        kid.name.middleName + " " +
                        kid.name.lastName;

        // Below gets rid of double spaces if there's no middle name
        String formatedName = name.replaceAll("\\s{2,}", " ").trim();
        holder.nameView.setText(formatedName);

        /******************
         * NCMC ID NUMBER *
         ******************/
        holder.idView.setText(kid.orgPrefix + kid.caseNum);

        /*******
         * AGE *
         *******/
        if (kid.date.age > -1) {
            String age = Long.toString(kid.date.age);
            holder.ageView.setText("Age: " + age);
        } else {
            holder.ageView.setVisibility(View.INVISIBLE);
        }

        /****************
         * MISSING DATE *
         ****************/
        long millisecond2 = kid.date.dateMissing;
        String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
        holder.missingDateView.setText("Missing: " + missingDateString);

        /************
         * LOCATION *
         ************/
        String location = kid.address.locCity + ", " +
                            kid.address.locState + ", " +
                            kid.address.locCountry + " ";
        holder.locationView.setText("Location: " + location);
    }

    @Override
    public int getItemCount() {
        if (kids == null) return 0;
        return kids.size();
    }

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
                MissingKid kid = kids.get(adapterPosition);
                String uId = kid.caseNum;
                String orgPrefix = kid.orgPrefix;
                mClickHandler.onClick(uId, orgPrefix);
            } else {
                Toast.makeText(view.getContext(), "Click error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is used to set the data on a KidsAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new KidsAdapter to display it.
     *
     * @param newKids The new list data to be displayed.
     */
    void swapList(List<MissingKid> newKids) {
        kids = newKids;
        notifyDataSetChanged();
    }
}
