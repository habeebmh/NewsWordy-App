package com.hmh.newswords;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    static Fragment mFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mFragment == null) {
            mFragment = new MainFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mFragment, TAG_TASK_FRAGMENT).commit();
        }

        if (!isConnectingToInternet())
           Toast.makeText(this, "No Internet!", Toast.LENGTH_LONG).show();
    }


    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            return activeNetwork != null
                    && activeNetwork.isConnected();
        }
        return false;
    }


}
