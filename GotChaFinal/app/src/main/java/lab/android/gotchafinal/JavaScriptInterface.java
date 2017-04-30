package lab.android.gotchafinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 2017/1/10.
 */

public class JavaScriptInterface {

    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";
    public static final String FOURTH_COLUMN="Fourth";
    private static final int JS_UPDATE_TIME = 1;

    private Activity activity;
    private View alertLayout, listLayout;
    private ImageView pokImage;
    private TextView pokText;
    private AlertDialog alertDialog, pokDialog;

    private ListView listTable;
    private SQLiteDatabase db = null;

    public JavaScriptInterface (Activity activity, final WebView webView, Button btnPok) {
        //Log.i("JavaScriptInterface", "thread ready start ~ !");

        this.activity = activity;

        try {

            db = activity.openOrCreateDatabase("sqlite.db", MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE pokemon " +
                    "( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " info_0 INTEGER, info_1 INTEGER, info_2 INTEGER, info_3 INTEGER );");

        }catch (SQLiteException ex) {
            Log.w("onCreate()", ex.toString());
            db.execSQL("DROP TABLE pokemon");
            db.execSQL("CREATE TABLE pokemon " +
                    "( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " info_0 INTEGER, info_1 INTEGER, info_2 INTEGER, info_3 INTEGER );");
        }catch (Exception ex) {
            Log.w("onCreate()", ex.toString());
            ex.printStackTrace();
        }

        btnPok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPok(db.rawQuery("SELECT * FROM pokemon", null));
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i("thread", "thread start ~ !");

                while (true) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:randPok()");
                        }
                    });

                    try {
                        Thread.sleep(JS_UPDATE_TIME * 1000);
                    } catch (InterruptedException ex){
                        Log.i("thread interr", ex.toString());
                    }
                }
            }
        };
        thread.start();

    }


    @JavascriptInterface
    public void catchPok (String string) {
        Toast.makeText(activity, "Call by WebView ~ " + string, Toast.LENGTH_SHORT).show();

        alertLayout = LayoutInflater.from(activity).inflate(R.layout.alertlayout, null);
        pokImage = (ImageView)alertLayout.findViewById(R.id.pokView);
        pokText = (TextView)alertLayout.findViewById(R.id.pokInfo);

        String pokStr = "", pokName = "";
        final int[] pokInfo = new int[4];
        pokInfo[0] = Integer.parseInt(string);
        pokInfo[1] = (int) (Math.random()*14 + 1);
        pokInfo[2] = (int) (Math.random()*14 + 1);
        pokInfo[3] = (int) (Math.random()*14 + 1);

        if (pokInfo[1] + pokInfo[2] + pokInfo[3] < 15)
            pokStr = "幫QQ 他的體質沒有很好\n";
        else if (pokInfo[1] + pokInfo[2] + pokInfo[3] < 30)
            pokStr = "這是一隻普通的寶可夢\n";
        else if (pokInfo[1] + pokInfo[2] + pokInfo[3] < 45)
            pokStr = "挖 他的體質好像還不錯\n";
        else if (pokInfo[1] + pokInfo[2] + pokInfo[3] == 45)
            pokStr = "太神喇 他已經封頂拉~~~\n";

        pokStr += "\n攻擊 : " + pokInfo[1] + " / 15\n" +
                    "血量 : " + pokInfo[2] + " / 15\n" +
                    "防禦 : " + pokInfo[3] + " / 15";

        Log.w("pok",pokStr);

        pokText.setText(pokStr);

        switch (pokInfo[0]) {
            case 0 :
                pokImage.setImageResource(R.drawable.pok1);
                pokName = "\t\t皮卡丘";
                break;
            case 1 :
                pokImage.setImageResource(R.drawable.pok2);
                pokName = "\t\t小火龍";
                break;
            case 2 :
                pokImage.setImageResource(R.drawable.pok3);
                pokName = "\t\t傑尼龜";
                break;
            default:
                break;
        }

        alertDialog = new AlertDialog.Builder(activity)
            .setTitle(pokName)
            .setView(alertLayout)
            .setPositiveButton("收藏到圖鑑中", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(activity, "Catch", Toast.LENGTH_SHORT).show();
                    ContentValues cv = new ContentValues();
                    cv.put("info_0", pokInfo[0]);
                    cv.put("info_1", pokInfo[1]);
                    cv.put("info_2", pokInfo[2]);
                    cv.put("info_3", pokInfo[3]);
                    db.insert("pokemon", null, cv);
                }
            })
            .setNegativeButton("丟棄牠", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
                }
            })
            .create();


        alertDialog.show();

    }

    private void showPok (Cursor cursor) {
        listLayout = LayoutInflater.from(activity).inflate(R.layout.list_layout, null);
        listTable = (ListView)listLayout.findViewById(R.id.listTable);


        ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String,Integer>>();
        HashMap<String,Integer> temp = new HashMap<String, Integer>();
        temp.put(FIRST_COLUMN, -1);
        list.add(temp);

        while (cursor.moveToNext()) {
            Log.w("cur0", cursor.getInt(0) + "");
            Log.w("cur1", cursor.getInt(1) + "");
            Log.w("cur2", cursor.getInt(2) + "");
            Log.w("cur3", cursor.getInt(3) + "");
            Log.w("cur4", cursor.getInt(4) + "");

            temp = new HashMap<String, Integer>();
            temp.put(FIRST_COLUMN, cursor.getInt(1));
            temp.put(SECOND_COLUMN,  cursor.getInt(2));
            temp.put(THIRD_COLUMN,  cursor.getInt(3));
            temp.put(FOURTH_COLUMN,  cursor.getInt(4));
            list.add(temp);
        }
        ListViewAdapter adapter = new ListViewAdapter(activity, list);
        listTable.setAdapter(adapter);

        pokDialog = new AlertDialog.Builder(activity)
                .setTitle("寶貝圖鑑")
                .setView(listLayout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .create();

        pokDialog.show();

    }

}
