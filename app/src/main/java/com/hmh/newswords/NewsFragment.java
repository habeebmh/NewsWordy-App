package com.hmh.newswords;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * habeeb
 * 9/3/15
 */
public class NewsFragment extends Fragment {


    static List<GoogleResults.Result> mResults;
    ResultsAdapter mAdapter;
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_news, null);

        mListView = (ListView) view.findViewById(R.id.results_list);

        Intent intent = getActivity().getIntent();
        String word = "Hello";

        if (intent.getStringExtra(MainFragment.STRING_WORD) != null)
            word = intent.getStringExtra(MainFragment.STRING_WORD);

        new GetResultsTask().execute(word);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("News for \"" + word + "\"");
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mResults == null) mResults = new ArrayList<>();

        mAdapter = new ResultsAdapter(getActivity(), mResults);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoogleResults.Result result = mAdapter.getItem(position);
                String url = Uri.decode(result.getUrl());
                // Log.d("Tag", url);
                Intent i = new Intent(getActivity(), ReadActivity.class);
                i.putExtra("URL", url);
                i.putExtra("TITLE", result.getTitle());
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class GetResultsTask extends AsyncTask<String, Void, List<GoogleResults.Result>> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(),
                    "Refreshing", "Getting News...", true, true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    getActivity().finish();
                }
            });
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected List<GoogleResults.Result> doInBackground(String... params) {
            List<GoogleResults.Result> results = new ArrayList<>();

            for (int i = 0; i < 20; i = i + 4) {
                String address = "http://ajax.googleapis.com/ajax/services/search/news?v=1.0&q=";
                String charset = "UTF-8";
                try {
                    URL url = new URL(address + URLEncoder.encode(params[0], charset));
                    Reader reader = new InputStreamReader(url.openStream(), charset);
                    GoogleResults result = new Gson().fromJson(reader, GoogleResults.class);
                    results.addAll(result.getResponseData().getResults());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<GoogleResults.Result> results) {
            mResults.clear();
            mResults.addAll(results);
            mAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
        }
    }

    class ResultsAdapter extends ArrayAdapter<GoogleResults.Result> {

        List<GoogleResults.Result> mList;

        public ResultsAdapter(Context context, List<GoogleResults.Result> list) {
            super(context, R.layout.item_result, list);
            mList = list;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.item_result, null);

            GoogleResults.Result result = mList.get(position);
            TextView title = (TextView) convertView.findViewById(R.id.text1);
            TextView text = (TextView) convertView.findViewById(R.id.text2);

            title.setText(Html.fromHtml(result.getTitle().replace("...", "")));
            // text.setText(result.getLead());
            text.setVisibility(View.GONE);

            return convertView;
        }
    }
}
