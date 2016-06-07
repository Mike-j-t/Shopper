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
    public AislesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
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

        textviewaisleid.setText(cursor.getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
        textviewaislename.setText(cursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
        textviewaisleorder.setText(cursor.getString(ShopperDBHelper.AISLES_COLUMN_ORDER_INDEX));
        textviewaisleshopref.setText(cursor.getString(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_aisle_list_entry, parent, false);
    }
}