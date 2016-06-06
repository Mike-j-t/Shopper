package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 11/02/2016.
 */
class AisleListSpinnerAdapter extends CursorAdapter {
    public static int aisleidoffset;
    public static int aislenameoffset;
    public static int aisleorderoffset;
    public static int aisleshoprefoffset;

    public AisleListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get column offsets from cursor (once to reduce overheads)
        if(cursor.getPosition() == 0) {
            aisleidoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID);
            aislenameoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            aisleorderoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
            aisleshoprefoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        }
        TextView tvaisleid = (TextView) view.findViewById(R.id.aisle_id_entry);
        TextView tvaislename = (TextView) view.findViewById(R.id.aisle_name_entry);
        TextView tvaisleorder = (TextView) view.findViewById(R.id.aisle_order_entry);
        TextView tvaisleshopref = (TextView) view.findViewById(R.id.aisle_shopref_entry);

        tvaisleid.setText(cursor.getString(aisleidoffset));
        tvaislename.setText(cursor.getString(aislenameoffset));
        tvaisleorder.setText(cursor.getString(aisleorderoffset));
        tvaisleshopref.setText(cursor.getString(aisleshoprefoffset));
    }
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_aisle_list_entry, parent,false);
    }
}