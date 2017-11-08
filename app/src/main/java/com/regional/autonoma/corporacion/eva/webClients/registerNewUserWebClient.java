package com.regional.autonoma.corporacion.eva.webClients;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nestor on 03-Nov-17.
 * this defines the behaviour for the web view that handles the external user
 * registration process
 */

public class registerNewUserWebClient extends WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(Uri.parse(url).getHost().contains("evacar.azurewebsites.net")){
            //all the internal navigation is done on the webview
            return false;
        }
        //loads any external URL on the device browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }
}
