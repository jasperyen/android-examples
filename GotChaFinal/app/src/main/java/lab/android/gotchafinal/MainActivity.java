package lab.android.gotchafinal;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String MAP_URL = "file:///android_asset/map.html";
    private static final String JS_INTERFACE = "JsInterface";

    private WebView webView;
    private Button btnPok;
    private JavaScriptInterface javascriptInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPok = (Button)findViewById(R.id.pokBtn);

        webView = (WebView)findViewById(R.id.mapView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(MAP_URL);

        javascriptInterface = new JavaScriptInterface(this, webView, btnPok);
        webView.addJavascriptInterface(javascriptInterface, JS_INTERFACE);
    }

}
