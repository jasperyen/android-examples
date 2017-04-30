package lab.android.gotchafinal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 2017/1/18.
 */

public class ListViewAdapter extends BaseAdapter {
    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";
    public static final String FOURTH_COLUMN="Fourth";
    public ArrayList<HashMap<String, Integer>> list;
    Activity activity;
    ImageView info_img;
    TextView info_1, info_2, info_3;


    public ListViewAdapter(Activity activity,ArrayList<HashMap<String, Integer>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater=activity.getLayoutInflater();

        convertView=inflater.inflate(R.layout.list_view, null);

        info_img=(ImageView) convertView.findViewById(R.id.listImage);
        info_1=(TextView) convertView.findViewById(R.id.list_1);
        info_2=(TextView) convertView.findViewById(R.id.list_2);
        info_3=(TextView) convertView.findViewById(R.id.list_3);



        HashMap<String, Integer> map = list.get(position);

        if (map.get(FIRST_COLUMN).equals(-1)) {
            info_1.setText("\n攻擊");
            info_2.setText("\n血量");
            info_3.setText("\n防禦");
            return convertView;
        }
        else if (map.get(FIRST_COLUMN).equals(0))
            info_img.setImageResource(R.drawable.pok01);
        else if (map.get(FIRST_COLUMN).equals(1))
            info_img.setImageResource(R.drawable.pok02);
        else if (map.get(FIRST_COLUMN).equals(2))
            info_img.setImageResource(R.drawable.pok03);

        info_1.setText("\n" + map.get(SECOND_COLUMN).toString());
        info_2.setText("\n" + map.get(THIRD_COLUMN).toString());
        info_3.setText("\n" + map.get(FOURTH_COLUMN).toString());

        return convertView;
    }

}
