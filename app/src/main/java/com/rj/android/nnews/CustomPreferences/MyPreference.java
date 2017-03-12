package com.rj.android.nnews.CustomPreferences;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rj.android.nnews.R;
import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.Sync.SyncAdapter;

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class MyPreference extends Preference {

    // This is the constructor called by the inflater
    public MyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    protected void onClick() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // Data has changed, notify so UI can be refreshed!
                getContext().getContentResolver().delete(Contract.Key_Type.CONTENT_URI,null,null);

                getContext().getContentResolver().delete(Contract.Article.CONTENT_URI,null,null);
                Toast.makeText(getContext(),"Cache cleared ",Toast.LENGTH_SHORT).show();
                Glide.get(getContext()).clearMemory();
                new UploadImageAsyncTask().execute();
                SyncAdapter.syncImmediately( getContext());
                notifyChanged();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class UploadImageAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Glide.get(getContext()).clearMemory();
                    }
                }
            }).start();

            return null;
        }
    }

}