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
 * Created by Mike092015 on 6/02/2016.
        */
class AislesCursorAdapter extends CursorAdapter {

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

    public AislesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setAislesOffsets(cursor);
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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textviewaisleid = (TextView) view.findViewById(R.id.aisle_id_entry);
        TextView textviewaislename = (TextView) view.findViewById(R.id.aisle_name_entry);
        TextView textviewaisleorder = (TextView) view.findViewById(R.id.aisle_order_entry);
        TextView textviewaisleshopref = (TextView) view.findViewById(R.id.aisle_shopref_entry);

        textviewaisleid.setText(cursor.getString(aisles_aisleid_offset));
        textviewaislename.setText(cursor.getString(aisles_aislename_offset));
        textviewaisleorder.setText(cursor.getString(aisles_aisleorder_offset));
        textviewaisleshopref.setText(cursor.getString(aisles_aisleshopref_offset));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_aisle_list_entry, parent, false);
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