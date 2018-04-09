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
        void onClick(long id);
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
        Picasso.get().load(kid.originalPhotoUrl).fit().into(holder.thumbnailView);

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
        String ncId = Long.toString(kid.ncmcId);
        holder.idView.setText("NCMC" + ncId);

        /*******
         * DOB *
         *******/
        long millisecond = kid.date.dateOfBirth;
        String dateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond)).toString();
        holder.dobView.setText("DOB: " + dateString);

        /*******
         * AGE *
         *******/
        String age = Long.toString(kid.date.age);
        holder.ageView.setText("Age: " + age);

        /****************
         * MISSING DATE *
         ****************/
        long millisecond2 = kid.date.dateMissing;
        String missingDateString = DateFormat.format("MMM dd, yyyy", new Date(millisecond2)).toString();
        holder.missingDateView.setText("Missing: " + missingDateString);

        /********
         * RACE *
         ********/
        String race = kid.race;
        holder.raceView.setText("Race: " + race);


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
        final TextView dobView;
        final TextView ageView;
        final TextView missingDateView;
        final TextView raceView;
        final TextView locationView;

        KidsAdapterViewHolder(View view) {
            super(view);

            thumbnailView = (ImageView) view.findViewById(R.id.image_small);

            nameView = (TextView) view.findViewById(R.id.name);
            idView = (TextView) view.findViewById(R.id.ncmc_id);
            dobView = (TextView) view.findViewById(R.id.dob);
            ageView = (TextView) view.findViewById(R.id.age);
            missingDateView = (TextView) view.findViewById(R.id.missing_date);
            raceView = (TextView) view.findViewById(R.id.race);
            locationView = (TextView) view.findViewById(R.id.location);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//            long idInLong = mCursor.getLong(MainActivity.INDEX_NCMC_ID);
//            mClickHandler.onClick(idInLong);
        }
    }

    void swapList(List<MissingKid> newKids) {
        kids = newKids;
        notifyDataSetChanged();
    }
}
