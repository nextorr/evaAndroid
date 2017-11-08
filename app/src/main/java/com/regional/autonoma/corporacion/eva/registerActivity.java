package com.regional.autonoma.corporacion.eva;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.support.v7.widget.Toolbar;

import com.regional.autonoma.corporacion.eva.webClients.registerNewUserWebClient;

public class registerActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set up the web view to handle the EVA external user registration
        mWebView = (WebView) findViewById(R.id.register_webview);
        mWebView.setWebViewClient(new registerNewUserWebClient());
        mWebView.loadUrl("http://evacar.azurewebsites.net/ExternalUsers/userRegistration");
        //enabling javascript on the webview
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        //setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        toolbar.setTitle(R.string.register_external_user_title_es);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //enablig back navigation on the webView

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
