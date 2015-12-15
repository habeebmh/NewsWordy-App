package com.hmh.newswords;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class NewsActivity extends AppCompatActivity {

    static Fragment mNewsFragment;

    InterstitialAd interstitialAd;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if (mNewsFragment == null) {
            mNewsFragment = new NewsFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_placeholder, mNewsFragment).commit();
        }


        AdRequest adRequest = new AdRequest.Builder().build();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int opens = preferences.getInt("opens", 0);
        preferences.edit().putInt("opens", ++opens).apply();

        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5073946130489310/9507592786");
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                int o = preferences.getInt("opens", 0);
                if (o > 0 && o % 10 == 0)
                    displayInterstitial();
            }
        });
    }

    public void displayInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mNewsFragment = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
