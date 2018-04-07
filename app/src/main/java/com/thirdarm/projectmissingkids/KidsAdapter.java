package com.thirdarm.projectmissingkids;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.KidsAdapterViewHolder> {

    private final Context mContext;

    final private KidsAdapterOnClickHandler mClickHandler;

    private Cursor mCursor;

    /**
     * The id is the NCMC number to identify the kid.
     */
    public interface KidsAdapterOnClickHandler {
        void onClick(long id);
    }


    public KidsAdapter(Context context, KidsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public KidsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(null, parent, false);
        view.setFocusable(true);

        return new KidsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KidsAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);



    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
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
            mCursor.moveToPosition(adapterPosition);
            long idInLong = mCursor.getLong(MainActivity.INDEX_NCMC_ID);
            mClickHandler.onClick(idInLong);
        }
    }

}
