package lab.android.mygooglemap;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String MAP_URL = "file:///android_asset/Map.html";
    private WebView webView;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        webView = (WebView)findViewById(R.id.MapView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(MAP_URL);

        button = (Button)findViewById(R.id.MapModeButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }


}
