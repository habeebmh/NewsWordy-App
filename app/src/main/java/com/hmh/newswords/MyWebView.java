package com.hmh.newswords;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * habeeb
 * 9/5/15
 */
public class MyWebView extends WebView {

    private ActionBar mActionBar;

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setActionBar(ActionBar actionBar) {
        mActionBar = actionBar;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (getScrollY() < 50) mActionBar.show();
        else if (Math.abs(oldt - t) > 30) {
            if (oldt - t < 0) mActionBar.hide();
            else mActionBar.show();
        }
    }


}
