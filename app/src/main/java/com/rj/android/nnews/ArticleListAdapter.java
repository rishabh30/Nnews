package com.rj.android.nnews;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rj.android.nnews.data.Contract;
import com.squareup.picasso.Picasso;

public class ArticleListAdapter extends CursorAdapter {


    private static final String LOG_TAG = ArticleListAdapter.class.getSimpleName();
    private final int MAIN_STORY = 0;
    private final int SIDE_STORY = 1;
    private boolean useMainLayout;

    public ArticleListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public int getItemViewType(int position) {
      return (position==0) ? MAIN_STORY : SIDE_STORY ;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if(viewType ==MAIN_STORY && !useMainLayout)
        {
            layoutId = R.layout.main_list_item;
        }else
        {
            layoutId = R.layout.list_item_layout2;
        }

        View view = LayoutInflater.from(context).inflate(layoutId  ,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title = cursor.getString(MainFragment.COL_ARTICLE_TITLE);
        String imageUrl;
        int viewType = getItemViewType(cursor.getPosition());
        if (viewType == MAIN_STORY && !useMainLayout) {
            imageUrl = cursor.getString(cursor.getColumnIndex(Contract.Article.PHOTO_URL_HIGH));
        } else {
            imageUrl = cursor.getString(cursor.getColumnIndex(Contract.Article.PHOTO_URL));
        }

        // Log.d(LOG_TAG, "  "+String.valueOf(cursor.getLong(cursor.getColumnIndex(Contract.Article._id))));
     /*   Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(viewHolder.imageView);
        */

        if (imageUrl.matches("no")) {
            viewHolder.imageView.setImageResource(R.drawable.noblogo);
        } else {
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(com.rj.android.nnews.R.drawable.loading)
                    .fit().centerCrop()
                    .noFade()
                    .into(viewHolder.imageView);
            viewHolder.titleView.setText(title);
        }
    }

    public void setUseMainLayout(boolean useMainLayout) {
        this.useMainLayout = useMainLayout;
    }


    public static class ViewHolder{
        TextView titleView ;
        ImageView imageView ;

        public ViewHolder(View view)
        {
             titleView = (TextView)view.findViewById(com.rj.android.nnews.R.id.list_item_title);
             imageView = (ImageView)view.findViewById(com.rj.android.nnews.R.id.list_item_image);
        }
    }
}
