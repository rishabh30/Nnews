package com.rj.android.nnews;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rj.android.nnews.data.Contract;
import com.squareup.picasso.Picasso;

public class ArticleListAdapterCompact extends CursorAdapter {


    private static final String LOG_TAG = ArticleListAdapterCompact.class.getSimpleName();
    private final int SIDE_STORY = 1;
    private boolean useMainLayout;

    public ArticleListAdapterCompact(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public int getItemViewType(int position) {
      return SIDE_STORY ;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = -1;


            layoutId = R.layout.list_item_layout;


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
        imageUrl = cursor.getString(cursor.getColumnIndex(Contract.Article.PHOTO_URL));

        String date = cursor.getString(
                cursor.getColumnIndex(Contract.Article.PUBLISH_DATE));

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
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.progress_animation)
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
        TextView friendlyday;

        public ViewHolder(View view)
        {

            friendlyday = (TextView) view.findViewById(R.id.friendlyday);
             titleView = (TextView)view.findViewById(R.id.list_item_title);
             imageView = (ImageView)view.findViewById(R.id.list_item_image);
        }
    }
}
