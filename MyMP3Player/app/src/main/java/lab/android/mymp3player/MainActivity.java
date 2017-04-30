package lab.android.mymp3player;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getName();

    Button btnPlay, btnStop, btnPause,
            btnLoud, btnWisper, btnFile, btnRaw;

    MediaPlayer mplayer;

    Handler UIhandler;

    ProgressBar timeBar;

    TextView tim, songname;

    Thread timthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

        btnPlay = (Button) findViewById(R.id.play);
        btnStop = (Button) findViewById(R.id.stop);
        btnPause = (Button) findViewById(R.id.pause);
        btnLoud = (Button) findViewById(R.id.loud);
        btnWisper = (Button) findViewById(R.id.whispered);
        btnFile = (Button) findViewById(R.id.file);
        btnRaw = (Button) findViewById(R.id.raw);

        timeBar = (ProgressBar) findViewById(R.id.TimeBar);

        tim = (TextView) findViewById(R.id.timtxt);
        songname = (TextView) findViewById(R.id.songname);


        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);
        btnPause.setEnabled(false);
        btnLoud.setEnabled(true);
        btnWisper.setEnabled(true);
        btnFile.setEnabled(true);
        btnRaw.setEnabled(true);


        UIhandler = new Handler() {

            @Override
            public  void handleMessage (Message message) {
                int currtime = message.arg1;

                Date date = new Date();
                date.setTime(currtime);

                tim.setText((new SimpleDateFormat("mm:ss")).format(date));
            }
        };
        timthread = new Thread(){
            @Override
            public void run() {
                int milli;
                Message msg;

                while (true) {
                    if (mplayer == null || !mplayer.isPlaying()) {
                        try {
                            sleep(Long.MAX_VALUE);
                        } catch (InterruptedException e) {
                            Log.i(TAG, e.toString());
                        }
                    }

                    milli = mplayer.getCurrentPosition();
                    timeBar.setProgress(milli);

                    msg = new Message();
                    msg.arg1 = milli;
                    UIhandler.sendMessage(msg);

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        Log.i(TAG, e.toString());
                    }
                }
            }
        };

        timthread.start();


        btnRaw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (mplayer != null && mplayer.isPlaying())
                    stop(mplayer);

                mplayer = MediaPlayer.create(MainActivity.this, R.raw.sample);

                songname.setText("RAW file !");

                timeBar.setMax(mplayer.getDuration());

                btnPlay.setEnabled(true);
            }
        });


        btnFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("audio/mpeg");
                picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

                Intent chosenIntent = Intent.createChooser(picker, "Chose an audio file.");
                startActivityForResult(chosenIntent, 0);

            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mplayer.isPlaying())
                    return;

                play(mplayer);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mplayer.isPlaying())
                    return;

                stop(mplayer);

                songname.setText("");
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mplayer.isPlaying())
                    return;

                pause(mplayer);
            }
        });

        btnLoud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 5);
            }
        });

        btnWisper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, 5);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 0 || resultCode != RESULT_OK)
            return;

        if (mplayer != null && mplayer.isPlaying())
            stop(mplayer);

        Log.i(TAG, "resultCode　:　" + resultCode);
        Log.i(TAG, "URI : " + data.getData());

        try {
            mplayer = MediaPlayer.create(MainActivity.this, data.getData());

            songname.setText( getRealPathFromURI(MainActivity.this, data.getData()) );
            //songname.setText( (new File("" + data.getData())).getPath() );

            timeBar.setMax(mplayer.getDuration());
            btnPlay.setEnabled(true);

        } catch (Exception e) {
            Log.w(TAG, e.toString());
            e.printStackTrace();
            songname.setText("File Path Error !");
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     *
     * 執行播放動作 初始化界面
     *
     * @param player
     */
    private void play (MediaPlayer player) {
        player.start();

        btnStop.setEnabled(true);
        btnPause.setEnabled(true);

        timthread.interrupt();
    }

    private void stop (MediaPlayer player) {
        player.stop();

        timthread.interrupt();

        timeBar.setProgress(0);
        Message msg = new Message();
        msg.arg1 = 0;
        UIhandler.sendMessage(msg);


        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
    }

    private void pause (MediaPlayer player) {
        player.pause();

        timthread.interrupt();

        timeBar.setProgress(player.getCurrentPosition());
        Message msg = new Message();
        msg.arg1 = player.getCurrentPosition();
        UIhandler.sendMessage(msg);

        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
    }


}
