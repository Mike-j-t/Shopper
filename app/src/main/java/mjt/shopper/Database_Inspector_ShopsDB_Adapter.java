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
        TextView textviewshopid = (TextView) view.findViewById(R.id.adise_shopsdb_id);
        TextView textviewshoporder = (TextView) view.findViewById(R.id.adise_shopsdb_order);
        TextView textviewshopname = (TextView) view.findViewById(R.id.adise_shopsdb_shopname);
        TextView textviewshopstreet = (TextView) view.findViewById(R.id.adise_shopsdb_street);
        TextView textviewshopcity = (TextView) view.findViewById(R.id.adise_shopsdb_city);
        TextView textviewshopstate = (TextView) view.findViewById(R.id.adise_shopsdb_state);
        TextView textviewshopphone = (TextView) view.findViewById(R.id.adise_shopsdb_phone);
        TextView textviewshopnotes = (TextView) view.findViewById(R.id.adise_shopsdb_notes);

        textviewshopid.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
        textviewshoporder.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
        textviewshopname.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
        textviewshopstreet.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
        textviewshopcity.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
        textviewshopstate.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
        textviewshopphone.setText(cursor.getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
        textviewshopnotes.setText(cursor.getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shopsdb_entry, parent, false);
    }
}