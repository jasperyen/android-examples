package lab.android.mymp4player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.VideoView;
import android.net.Uri;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    static final int RequestCode = 0;
    VideoView videoView;
    Intent chosenIntent;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnPreparedListener(this);

        Intent videopicker = new Intent(Intent.ACTION_GET_CONTENT);
        videopicker.setType("video/*");
        videopicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        chosenIntent = Intent.createChooser(videopicker, "Chose an audio file.");


        if (savedInstanceState != null && savedInstanceState.getString("uri") != null) {
            Log.i("onCreate()", "uri : " + savedInstanceState.getString("uri"));
            Log.i("onCreate()", "pos : " + savedInstanceState.getInt("currPos"));

            uri = Uri.parse(savedInstanceState.getString("uri"));


            videoView.setVideoURI(uri);
            videoView.seekTo(savedInstanceState.getInt("currPos"));
        }
        else {
            startActivityForResult(chosenIntent, RequestCode);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("onActivityResult()", "resultCode : " + resultCode);

        if (requestCode != RequestCode)
            return;


        if (resultCode == RESULT_CANCELED) {
            startActivityForResult(chosenIntent, RequestCode);
            return;
        }


        try {
            uri = data.getData();

            Log.i("onActivityResult()", "uri : " + uri.toString());

            videoView.setVideoURI(uri);

        } catch (Exception e) {
            Log.w("onActivityResult()", e.toString());
            e.printStackTrace();
        }

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK :
                videoView.suspend();
                startActivityForResult(chosenIntent, RequestCode);
                break;

        }
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){

        if (uri != null) {
            Log.i("onSaveInstanceState()", "uri : " + uri.toString());
            Log.i("onSaveInstanceState()", "pos : " + videoView.getCurrentPosition());

            savedInstanceState.putInt("currPos", videoView.getCurrentPosition());
            savedInstanceState.putString("uri", uri.toString());
        }

        super.onSaveInstanceState(savedInstanceState);
    }
}
