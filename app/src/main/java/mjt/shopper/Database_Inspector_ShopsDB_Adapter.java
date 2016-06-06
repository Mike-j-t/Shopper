package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 17/02/2016.
 */
class Database_Inspector_ShopsDB_Adapter extends CursorAdapter {
    public static int storeidfoffset;
    public static int storenameoffset;
    public static int storeorderoffset;
    public static int storestreetofffset;
    public static int storecityoffset;
    public static int storestateoffset;
    public static int storephoneoffset;
    public static int storenotesoffset;

    public Database_Inspector_ShopsDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();

        if(position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get column offsets from cursor (once to reduce overheads)
        if(cursor.getPosition() == 0 ) {
            storeidfoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID);
            storenameoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            storeorderoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
            storestreetofffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
            storecityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            storestateoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
            storephoneoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
            storenotesoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
        }

        TextView textviewshopid = (TextView) view.findViewById(R.id.adise_shopsdb_id);
        TextView textviewshoporder = (TextView) view.findViewById(R.id.adise_shopsdb_order);
        TextView textviewshopname = (TextView) view.findViewById(R.id.adise_shopsdb_shopname);
        TextView textviewshopstreet = (TextView) view.findViewById(R.id.adise_shopsdb_street);
        TextView textviewshopcity = (TextView) view.findViewById(R.id.adise_shopsdb_city);
        TextView textviewshopstate = (TextView) view.findViewById(R.id.adise_shopsdb_state);
        TextView textviewshopphone = (TextView) view.findViewById(R.id.adise_shopsdb_phone);
        TextView textviewshopnotes = (TextView) view.findViewById(R.id.adise_shopsdb_notes);

        textviewshopid.setText(cursor.getString(storeidfoffset));
        textviewshoporder.setText(cursor.getString(storeorderoffset));
        textviewshopname.setText(cursor.getString(storenameoffset));
        textviewshopstreet.setText(cursor.getString(storestreetofffset));
        textviewshopcity.setText(cursor.getString(storecityoffset));
        textviewshopstate.setText(cursor.getString(storestateoffset));
        textviewshopphone.setText(cursor.getString(storephoneoffset));
        textviewshopnotes.setText(cursor.getString(storenotesoffset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shopsdb_entry, parent, false);
    }
}