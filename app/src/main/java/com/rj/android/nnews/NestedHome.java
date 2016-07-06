package com.rj.android.nnews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.sync.SyncAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.ActivityInfo.*;


public class NestedHome extends Fragment {

    public static NestedHome newInstance() {
        NestedHome fragmentFirst = new NestedHome();

        Bundle args = new Bundle();


        String saveUrl = "", saveKeyName = "";


        saveUrl = "https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
        saveKeyName = "top_stories";

        args.putString("saveUrl", saveUrl);
        args.putString("saveKeyName", saveKeyName);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nested_home, container, false);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
 /*   public void animate(View view) {
        TextView imageView = (TextView)view;
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.hyperspace_jump);
        imageView.startAnimation(hyperspaceJumpAnimation);
    }*/

}