package com.example.webview1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView webView;
    View view;
    static Camera camera = null;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebViewClient(new CustomWebClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public  void onPermissionRequest(final PermissionRequest request){
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request){
                Log.d(TAG, "onPermissionRequestcanceled");
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl("https://rapidtest.elcomindo.com");
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            Log.d(TAG, "has camera permission :"+ hasCameraPermission);
            List<String> permission = new ArrayList<>();
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED){
                permission.add(Manifest.permission.CAMERA);
            }
            if (!permission.isEmpty()) {
                requestPermissions(permission.toArray(new String[permission.size()]), 111);
            }
        }
    }

    public class CustomWebClient extends WebViewClient{
        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon){
            super.onPageStarted(view, url, favicon);
            showDialog();
        }

        @Override
        public void onPageFinished(final WebView view, final String url){
            super.onPageFinished(view, url);
            dismissDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }

    Vector<ProgressDialog> progress = new Vector<>();
    public void showDialog(String message, boolean canCancelled){
        try {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(message);
            dialog.setCancelable(canCancelled);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.show();
            progress.add(dialog);
        }catch (Exception ignored){

        }
    }

    public void dismissDialog(){
        try {
            if (progress != null)
                for (ProgressDialog prog : progress)
                    prog.dismiss();
        }catch (Exception ignored){

        }
    }

    public void showDialog(){
        showDialog("Loading please wait", true);
    }

}
