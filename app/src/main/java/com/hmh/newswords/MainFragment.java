package com.hmh.newswords;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
// import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.wefika.flowlayout.FlowLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * habeeb
 * 9/3/15
 */
public class MainFragment extends Fragment {

    public static final String STRING_WORD = "word_to_find";

    protected HashMap<String, Integer> mWordsList;
    private int totalWords = 0;
    private FlowLayout mWordsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_main, null);
        mWordsContainer = (FlowLayout) view.findViewById(R.id.container);
        // ScrollView mScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        if (mWordsList == null || mWordsList.isEmpty()) {
            mWordsList = new HashMap<>();
        }

        new GetWordsTask().execute();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new GetWordsTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private View createWordCircle(String word, double number, double total) {
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(getActivity()).inflate(R.layout.word_circle, null);
        RelativeLayout circleContainer = (RelativeLayout) view.findViewById(R.id.circleContainer);
        ImageView circleBg = (ImageView) view.findViewById(R.id.circleImg);
        TextView wordView = (TextView) view.findViewById(R.id.wordTextView);
        double ratio = (number / total) * 10;

        int dimen = (int) (ratio * 800);

        circleContainer.setLayoutParams(new FlowLayout.LayoutParams(dimen + 30, dimen + 20));
        circleContainer.setOnClickListener(new OnWordClicked());

        circleBg.setMaxWidth(circleContainer.getWidth());
        circleBg.setMaxHeight(circleContainer.getHeight());
        int r = new Random().nextInt(225);
        int g = new Random().nextInt(225);
        int b = new Random().nextInt(225);
        int randomColor = Color.rgb(r, g, b);
        circleBg.setColorFilter(randomColor);

        wordView.setWidth(circleBg.getWidth());
        wordView.setText(word);
        wordView.setTextSize((float) (wordView.getTextSize() * ratio) - 20);

//        final int direction = new Random().nextInt(100) >= 50 ? -1 : 1;
//
//        TranslateAnimation animation =
//                new TranslateAnimation(view.getX(), direction * view.getX() + 5, view.getY(), direction * view.getY() + 5);
//        animation.setDuration(1000);
//        animation.setFillAfter(false);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation a) {
//                a.setAnimationListener(this);
//                view.startAnimation(a);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        view.startAnimation(animation);

        return view;
    }


    private void populate() {
        mWordsContainer.removeAllViews();
        List<String> keys = new ArrayList<>(mWordsList.keySet());
        Collections.shuffle(keys);
        for (String o : keys) {
            View v = createWordCircle(o, mWordsList.get(o), totalWords);
            mWordsContainer.addView(v);
        }

//        new CountDownTimer(mScrollView.getHeight(), 20) {
//
//            public void onTick(long millisUntilFinished) {
//
//                mScrollView.scrollTo(0, (int) (mScrollView.getHeight() - millisUntilFinished));
//            }
//
//            public void onFinish() {
//            }
//
//        }.start();
    }

    private class GetWordsTask extends AsyncTask<Void, Void, HashMap<String, Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            totalWords = 0;
            // mProgressDialog = ProgressDialog.show(getActivity(), "Refreshing", "Getting new Words...", true, true);
        }

        @Override
        protected HashMap<String, Integer> doInBackground(Void... params) {
            HashMap<String, Integer> list = new HashMap<>();

            try {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(
                        new URL("http://feeds.feedburner.com/newswordywords?format=xml")));
                SyndEntry entry = (SyndEntry) feed.getEntries().get(0);
                Document doc = Jsoup.connect(entry.getLink()).get();
                String s = doc.getElementsByAttributeValue("class", "post-body entry-content").text();
                String[] split = s.split(" ");
                for (String word : split) {
                    String[] parts = word.split("::");
                    int i = Integer.parseInt(parts[1]);
                    list.put(parts[0], i);
                    totalWords += i;
                }
            } catch (FeedException | IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Can't reach Server", Toast.LENGTH_SHORT).show();
            }

            list.remove("");

            return list;
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> strings) {
            mWordsList = strings;
            Toast.makeText(getActivity(), "Getting new Words", Toast.LENGTH_SHORT).show();
            populate();
        }
    }

    public class OnWordClicked implements View.OnClickListener, View.OnLongClickListener {
        @Override
        public void onClick(View v) {
            String word = ((TextView) v.findViewById(R.id.wordTextView)).getText().toString();
            if (!word.contentEquals("Hello")) {
                NewsActivity.mNewsFragment = null;
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtra(STRING_WORD, word);
                startActivity(intent);
            }
        }


        @Override
        public boolean onLongClick(View v) {
            // Collections.sort(mWordsList);
            new AlertDialog.Builder(getActivity())
                    .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(mWordsList.keySet())),
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String word = new ArrayList<>(mWordsList.keySet()).get(which);
                                    if (!word.contentEquals("Hello")) {
                                        Intent intent = new Intent(getActivity(), NewsActivity.class);
                                        intent.putExtra(STRING_WORD, word);
                                        startActivity(intent);
                                    }
                                    dialog.cancel();
                                }
                            }).create().show();
            return false;
        }
    }
}