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
class AisleListSpinnerAdapter extends CursorAdapter {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.


    // Variables to store aisles table offsets as obtained via the defined column names by
    // call to setAislesOffsets (aisles_aisleid_offset set -1 to act as notdone flag )
    private static int aisles_aisleid_offset = -1;
    private static int aisles_aislename_offset;
    private static int aisles_aisleorder_offset;
    private static int aisles_aisleshopref_offset;

    public AisleListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setAislesOffsets(cursor);

    }
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.activity_aisle_list_selector
                , parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvaisleid = (TextView) view.findViewById(R.id.aisle_id_selector);
        TextView tvaislename = (TextView) view.findViewById(R.id.aisle_name_selector);
        TextView tvaisleorder = (TextView) view.findViewById(R.id.aisle_order_selector);
        TextView tvaisleshopref = (TextView) view.findViewById(R.id.aisle_shopref_selector);

        tvaisleid.setText(cursor.getString(aisles_aisleid_offset));
        tvaislename.setText(cursor.getString(aisles_aislename_offset));
        tvaisleorder.setText(cursor.getString(aisles_aisleorder_offset));
        tvaisleshopref.setText(cursor.getString(aisles_aisleshopref_offset));
    }
    @Override
    public View getDropDownView(int position, View convertview, ViewGroup parent) {
        View v = convertview;
        if(v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_aisle_list_entry,
                    parent,
                    false
            );
        }
        Context context = v.getContext();
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        TextView aisleid = (TextView) v.findViewById(R.id.aisle_id_entry);
        TextView aislename = (TextView) v.findViewById(R.id.aisle_name_entry);
        TextView aisleorder = (TextView) v.findViewById(R.id.aisle_order_entry);
        TextView aisleshopref = (TextView) v.findViewById(R.id.aisle_shopref_entry);

        aisleid.setText(cursor.getString(aisles_aisleid_offset));
        aislename.setText(cursor.getString(aisles_aislename_offset));
        aisleorder.setText(cursor.getString(aisles_aisleorder_offset));
        aisleshopref.setText(cursor.getString(aisles_aisleshopref_offset));

        if(position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewroweven));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewrowodd));
        }

        return v;
    }


    // Set Aisles Table query offsets into returned cursor, if not already set
    private void setAislesOffsets(Cursor cursor) {
        if(aisles_aisleid_offset != -1) {
            return;
        }
        aisles_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID);
        aisles_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        aisles_aisleorder_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
        aisles_aisleshopref_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
    }
}