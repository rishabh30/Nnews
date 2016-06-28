package com.rj.android.nnews;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.sync.SyncAdapter;
import com.rj.android.nnews.view.SlidingTabLayout;
import com.rj.android.nnews.view.SmartFragmentStatePagerAdapter;

public class MainFragment extends Fragment  {



    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;


    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_KEY_ID = 1;
    public static final int COL_ARTICLE_TITLE = 2;
    public static final int COL_ARTICLE_URL = 3;
    public static final int COL_ARTICLE_ABSTRACT = 4;
    public static final int COL_ARTICLE_SOURCE = 5;
    public static final int COL_ARTICLE_PHOTO_HEADING = 6;
    public static final int COL_ARTICLE_PHOTO_URL_HIGH = 7;
    public static final int COL_ARTICLE_PUBLISH_DATE = 8;
    public static final int COL_ARTICLE_PHOTO_URL = 9;
    private static final int FORECAST_LOADER = 0;

    boolean mTwoPane;
    int mPosition;
    String SELECTED_KEY="POSITION";
    ListView main_list;

    ArticleListAdapter madapter;
    String[] textinfo = new String[15];
    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.TABLE_NAME + "." +Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.ABSTRACT,
            Contract.Article.SOURCE,
            Contract.Article.PHOTO_HEADING,
            Contract.Article.PHOTO_URL_HIGH,
            Contract.Article.PUBLISH_DATE,
            Contract.Article.PHOTO_URL
    };



    @Override
    public void onResume() {
        super.onResume();
    }

    public void setTwoPane(boolean twoPane)
    {
        mTwoPane = twoPane;
        if(madapter!=null)
        {
            madapter.setUseMainLayout(mTwoPane);
        }
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.rj.android.nnews.R.menu.main_fragment_menu, menu);



    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            updateArticle();
            /*String JsonData = getJsonData();

            parseJson(JsonData);*/
            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateArticle()
    {
        SyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter(getChildFragmentManager()));



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int pos) {

/*

                    Toast.makeText(getContext(),
                        "Selected page position: " + pos, Toast.LENGTH_SHORT).show();*/
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.rj.android.nnews.R.layout.fragment_main, container, false);
        return rootView;
    }

     void setCurrentTab(int pos)
    {
        mViewPager.setCurrentItem(pos);
    }



    class SamplePagerAdapter extends SmartFragmentStatePagerAdapter {



        private  int NUM_ITEMS = 4;
        String str[]={"HOME","Top Stories","Newswire","Movie Reviews"};
        public SamplePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NestedHome.newInstance(0, "Home");
                case 1: // Fragment # 0 - This will show FirstFragment
                    return NestedFragment.newInstance(0, "Top Stories");
                case 2: // Fragment # 0 - This will show FirstFragment different title
                    return NestedFragment2.newInstance(1, "Newswire");
                case 3: // Fragment # 1 - This will show SecondFragment
                    return NestedFragment3.newInstance(2, "Movie Reviews");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return str[position];
        }

    }



/*
        *//**
         * @return the number of pages to display
         *//*
        @Override
        public int getCount() {
            return 3;
        }

        *//**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         *//*
        @Override
        public boolean isViewFromObject(View view, Object o) {


            return o == view;
        }


        @Override
        public CharSequence getPageTitle(int position) {

           return str[position];
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        *//**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         *//*
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);


            String tag=null;
            FragmentManager fm = getChildFragmentManager();
            Fragment fragment = new NestedFragment();

            FragmentTransaction ft  = fm.beginTransaction();
            ft.addToBackStack(tag);
            ft.replace(R.id.fragmentNested, fragment);
            ft.commit();


            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        *//**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         *//*
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }*/

}
