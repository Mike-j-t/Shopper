package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 *
 */
class ShopListSpinnerAdapter extends CursorAdapter{

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

    private Context context;
    private static final int aaslsid = R.id.aasls;
    private static final int aasleid = R.id.aasle;

    ShopListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setShopsOffsets(cursor);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.activity_aisle_shop_list_selector,
                parent,
                false
        );
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {

        TextView sel_shopname = (TextView) view.findViewById(R.id.aaslstv01);
        TextView sel_shopstreet = (TextView) view.findViewById(R.id.aaslstv03);
        TextView sel_shopcity = (TextView) view.findViewById(R.id.aaslstv02);

        sel_shopname.setText(cursor.getString(shops_shopname_offset));
        sel_shopstreet.setText(cursor.getString(shops_shopstreet_offset));
        sel_shopcity.setText(cursor.getString(shops_shopcity_offset));
    }

    public View getDropDownView(int position, View convertview, ViewGroup parent) {
        View v = convertview;
        if( v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_aisle_shop_list_entry,
                    parent,
                    false
            );
            //return v;
        }
        Context context = v.getContext();
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);


        TextView shopname = (TextView) v.findViewById(R.id.aasletv01);
        TextView shopstreet = (TextView) v.findViewById(R.id.aasletv03);
        TextView shopcity = (TextView) v.findViewById(R.id.aasletv02);
        //getCursor().moveToPosition(position);

        shopname.setText(cursor.getString(shops_shopname_offset));
        shopstreet.setText(cursor.getString(shops_shopstreet_offset));
        shopcity.setText(cursor.getString(shops_shopcity_offset));

        if(position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewroweven));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewrowodd));
        }
        return v;
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

    public void determineViewBeingProcessed(View view, String tag, int pv) {
        if(tag.length() > 8) {
            tag = tag.substring(0,7);
        }
        if(view == null) {
            Log.i("DVBP_" + tag,"View passed is NULL");
            return;
        }
        switch (view.getId()) {
            case aaslsid:
                Log.i("DVBP_" + tag,"Processing the Selector View from the Spinner.");
                break;
            case aasleid:
                Log.i("DVBP_" + tag, "Processing a Dropdown Entry View from the Spinner." +
                " Position passed = " + Integer.toString(pv));
                break;
            default:
                Log.i("DVBP" + tag, "???? Not sure what is being processed by the Spinner.");
        }
    }
}
