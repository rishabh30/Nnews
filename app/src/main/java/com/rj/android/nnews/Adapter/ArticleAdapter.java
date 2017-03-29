package com.rj.android.nnews.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rj.android.nnews.MainFragment;
import com.rj.android.nnews.R;
import com.rj.android.nnews.Utility;
import com.rj.android.nnews.data.Contract;
import com.squareup.picasso.Picasso;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private static final String LOG_TAG = ArticleAdapter.class.getSimpleName();
    private final int MAIN_STORY = 0;
    private final int SIDE_STORY = 1;
    final private Context mContext;
    final private ListItemClickListener mOnClickListener;
    View mEmptyView;
    private boolean mUseMainLayout = true;
    private Cursor mCursor;

    public ArticleAdapter(Context context, ListItemClickListener listener, View emptyView) {
        mContext = context;
        mOnClickListener = listener;
        mEmptyView = emptyView;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && false) ? MAIN_STORY : SIDE_STORY;
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        if (getItemCount() == 0) {
            int message = R.string.no_info_available;
            if (mEmptyView != null) {
                if (!Utility.isNetworkAvailable(mContext)) {
                    message = R.string.no_network;
                }
                TextView temp = (TextView) mEmptyView;
                temp.setText(message);
            }
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewGroup instanceof RecyclerView) {
            int layoutId = -1;

            if (viewType == MAIN_STORY && !mUseMainLayout) {
                layoutId = R.layout.main_list_item;
            } else {
                layoutId = R.layout.card_view_professional;
            }

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("Not Bound to Recycle View");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(MainFragment.COL_ARTICLE_TITLE);
        if (title == "null")
            title = "";
        String imageUrl;
        int viewType = getItemViewType(mCursor.getPosition());
        imageUrl = mCursor.getString(mCursor.getColumnIndex(Contract.Article.PHOTO_URL_HIGH));


        String date = mCursor.getString(
                mCursor.getColumnIndex(Contract.Article.PUBLISH_DATE));

        try {
            date = Utility.getDatabaseDate(date);
        } catch (Exception e) {
            e.printStackTrace();
            date = "";
        }

        viewHolder.friendlyday.setText(date);
        if (imageUrl.matches("no")) {
            viewHolder.imageView.setImageResource(R.drawable.noblogo);
        } else {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.progress_animation)
                    .fit().centerCrop()
                    .noFade()
                    .into(viewHolder.imageView);
            viewHolder.titleView.setText(title);
        }
    }

    public void setUseMainLayout(boolean useMainLayout) {
        mUseMainLayout = useMainLayout;
    }

    public interface ListItemClickListener {
        void onListItemClick(long clickedItemIndex, ViewHolder vh, ImageView imageView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView titleView;
        TextView friendlyday;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            friendlyday = (TextView) view.findViewById(R.id.friendlyday);
            imageView = (ImageView) view.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);
            int id = mCursor.getColumnIndex(Contract.Article._id);
            mOnClickListener.onListItemClick((long) mCursor.getInt(id), this, this.imageView);
        }
    }
}