package popMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.idiancan.R;

import java.util.ArrayList;

/**
 * Created by dongg on 2017/3/15.
 */

public class UserMenu extends PopMenu {
    public UserMenu(Context context) {
        super(context);
    }

    @Override
    protected ListView findListView(View view) {
        return (ListView) view.findViewById(R.id.menu_listview);
    }

    @Override
    protected View onCreateView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_user_1, null);
        return view;
    }

    @Override
    protected ArrayAdapter<Item> onCreateAdapter(Context context, ArrayList<Item> items) {
        return new ArrayAdapter<Item>(context, R.layout.item_menu_user, items);
    }


}
