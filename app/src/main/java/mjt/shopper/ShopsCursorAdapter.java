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
 * Created by Mike092015 on 2/02/2016.
 */
class ShopsCursorAdapter extends CursorAdapter {
    public ShopsCursorAdapter(Context context, Cursor cursor, int flags) {
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
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_shop_list_entry, parent, false);
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        //TextView textviewShopid = (TextView) view.findViewById(R.id.shop_id_entry);
        TextView textViewShopName = (TextView) view.findViewById(R.id.shop_name_entry);
        TextView textViewShopOrder = (TextView) view.findViewById(R.id.shop_order_entry);
        TextView textViewShopStreet = (TextView) view.findViewById(R.id.shop_street_entry);
        TextView textViewShopCity = (TextView) view.findViewById(R.id.shop_city_entry);
        TextView textViewShopState = (TextView) view.findViewById(R.id.shop_state_entry);
        TextView textViewShopPhone = (TextView) view.findViewById(R.id.shop_phone_entry);
        TextView textViewShopNotes = (TextView) view.findViewById(R.id.shop_notes_entry);

        //textviewShopid.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
        textViewShopName.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
        textViewShopOrder.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
        textViewShopStreet.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
        textViewShopCity.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
        textViewShopState.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
        textViewShopPhone.setText(cursor.getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
        textViewShopNotes.setText(cursor.getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
    }
}
