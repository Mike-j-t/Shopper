package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 5/02/2016.
 */
public class ShopListSpinnerAdapter extends CursorAdapter{

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

    public ShopListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setShopsOffsets(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_aisle_shop_list_entry, parent, false);
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        TextView textViewShopName = (TextView) view.findViewById(R.id.aasletv01);
        TextView textViewShopStreet = (TextView) view.findViewById(R.id.aasletv03);
        TextView textViewShopCity = (TextView) view.findViewById(R.id.aasletv02);

        textViewShopName.setText(cursor.getString(shops_shopname_offset));
        textViewShopStreet.setText(cursor.getString(shops_shopstreet_offset));
        textViewShopCity.setText(cursor.getString(shops_shopcity_offset));
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
