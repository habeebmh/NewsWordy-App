package com.hmh.newswords;

import android.net.Uri;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by habeeb on 7/15/15.
 */
public class GoogleResults {

    private ResponseData responseData;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String toString() {
        return "ResponseData[" + responseData + "]";
    }

    static class ResponseData {
        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String toString() {
            return "Results[" + results + "]";
        }
    }

    static class Result {
        private String url;
        private String title;
        private String lead;

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLead() {
            return lead;
        }

        public void setLead() {
            String decodedURL = Uri.decode(getUrl());
            try {
                Document document = Jsoup.connect(decodedURL).get();
                String lead = document.getElementsByTag("p").text();
                if (lead != null)
                    if (lead.length() > 150)
                        this.lead = lead.substring(0, 150) + "...";
                    else this.lead = lead + "...";
                else this.lead = "Read More...";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String toString() {
            return "Result[url:" + url + ",title:" + title + "]";
        }
    }
}