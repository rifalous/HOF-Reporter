package chierra.hof_reporter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback {
    private static final String URL = "http://192.168.4.1/";
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;

    private final Handler handler = new Handler(this);

    private WebView myWebView;
    private WebViewClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        myWebView = findViewById(R.id.webview);
        myWebView.setOnTouchListener(this);

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };


        myWebView.setWebViewClient(client);
        myWebView.getSettings().getJavaScriptEnabled();
        myWebView.setVerticalScrollBarEnabled(false);
        myWebView.loadUrl(URL);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == CLICK_ON_URL){
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (message.what == CLICK_ON_WEBVIEW){
            //Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return  false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.webview && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
