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

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    // Variables to store shops table offsets as obtained via the defined column names by
    // call to setShopsOffsets (shops_shopid_offset set -1 to act as notdone flag )
    public static int shops_shopid_offset = -1;
    public static int shops_shopname_offset;
    public static int shops_shoporder_offset;
    public static int shops_shopstreet_offset;
    public static int shops_shopcity_offset;
    public static int shops_shopstate_offset;
    public static int shops_shopphone_offset;
    public static int shops_shopnotes_offset;

    public Database_Inspector_ShopsDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setShopsOffsets(cursor);
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
        TextView textviewshopid = (TextView) view.findViewById(R.id.adise_shopsdb_id);
        TextView textviewshoporder = (TextView) view.findViewById(R.id.adise_shopsdb_order);
        TextView textviewshopname = (TextView) view.findViewById(R.id.adise_shopsdb_shopname);
        TextView textviewshopstreet = (TextView) view.findViewById(R.id.adise_shopsdb_street);
        TextView textviewshopcity = (TextView) view.findViewById(R.id.adise_shopsdb_city);
        TextView textviewshopstate = (TextView) view.findViewById(R.id.adise_shopsdb_state);
        TextView textviewshopphone = (TextView) view.findViewById(R.id.adise_shopsdb_phone);
        TextView textviewshopnotes = (TextView) view.findViewById(R.id.adise_shopsdb_notes);

        textviewshopid.setText(cursor.getString(shops_shopid_offset));
        textviewshoporder.setText(cursor.getString(shops_shoporder_offset));
        textviewshopname.setText(cursor.getString(shops_shopname_offset));
        textviewshopstreet.setText(cursor.getString(shops_shopstreet_offset));
        textviewshopcity.setText(cursor.getString(shops_shopcity_offset));
        textviewshopstate.setText(cursor.getString(shops_shopstate_offset));
        textviewshopphone.setText(cursor.getString(shops_shopphone_offset));
        textviewshopnotes.setText(cursor.getString(shops_shopnotes_offset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shopsdb_entry, parent, false);
    }

    // Set Shops Table query offsets into returned cursor, if not already set
    public void setShopsOffsets(Cursor cursor) {
        // If not -1 then already done
        if(shops_shopid_offset != -1) {
            return;
        }
        shops_shopid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID);
        shops_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        shops_shoporder_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
        shops_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        shops_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        shops_shopstate_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
        shops_shopphone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
        shops_shopnotes_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
    }
}