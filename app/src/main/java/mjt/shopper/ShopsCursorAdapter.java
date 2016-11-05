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
 *
 */
class ShopsCursorAdapter extends CursorAdapter {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    // Variables to store shops table offsets as obtained via the defined column names by
    // call to setShopsOffsets (shops_shopid_offset set -1 to act as notdone flag )
    private static int shops_shopid_offset = -1;
    private static int shops_shopname_offset;
    private static int shops_shoporder_offset;
    private static int shops_shopstreet_offset;
    private static int shops_shopcity_offset;
    private static int shops_shopstate_offset;
    private static int shops_shopphone_offset;
    private static int shops_shopnotes_offset;

    ShopsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setShopsOffsets(cursor);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();
        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        return view;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_shop_list_entry, parent, false);
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        TextView textViewShopName = (TextView) view.findViewById(R.id.shop_name_entry);
        TextView textViewShopOrder = (TextView) view.findViewById(R.id.shop_order_entry);
        TextView textViewShopStreet = (TextView) view.findViewById(R.id.shop_street_entry);
        TextView textViewShopCity = (TextView) view.findViewById(R.id.shop_city_entry);
        TextView textViewShopState = (TextView) view.findViewById(R.id.shop_state_entry);
        TextView textViewShopPhone = (TextView) view.findViewById(R.id.shop_phone_entry);
        TextView textViewShopNotes = (TextView) view.findViewById(R.id.shop_notes_entry);

        textViewShopName.setText(cursor.getString(shops_shopname_offset));
        textViewShopOrder.setText(cursor.getString(shops_shoporder_offset));
        textViewShopStreet.setText(cursor.getString(shops_shopstreet_offset));
        textViewShopCity.setText(cursor.getString(shops_shopcity_offset));
        textViewShopState.setText(cursor.getString(shops_shopstate_offset));
        textViewShopPhone.setText(cursor.getString(shops_shopphone_offset));
        textViewShopNotes.setText(cursor.getString(shops_shopnotes_offset));
    }

    // Set Shops Table query offsets into returned cursor, if not already set
    private void setShopsOffsets(Cursor cursor) {
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
