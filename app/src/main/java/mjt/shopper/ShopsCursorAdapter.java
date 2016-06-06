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
    public static int storeidfoffset;
    public static int storenameoffset;
    public static int storeorderoffset;
    public static int storestreetofffset;
    public static int storecityoffset;
    public static int storestateoffset;
    public static int storephoneoffset;
    public static int storenotesoffset;

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
        // get column offsets from cursor (once to reduce overheads)
        if(cursor.getPosition() == 0 ) {
            storeidfoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID);
            storenameoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            storeorderoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
            storestreetofffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
            storecityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            storestateoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
            storephoneoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
            storenotesoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
        }
        //TextView textviewShopid = (TextView) view.findViewById(R.id.shop_id_entry);
        TextView textViewShopName = (TextView) view.findViewById(R.id.shop_name_entry);
        TextView textViewShopOrder = (TextView) view.findViewById(R.id.shop_order_entry);
        TextView textViewShopStreet = (TextView) view.findViewById(R.id.shop_street_entry);
        TextView textViewShopCity = (TextView) view.findViewById(R.id.shop_city_entry);
        TextView textViewShopState = (TextView) view.findViewById(R.id.shop_state_entry);
        TextView textViewShopPhone = (TextView) view.findViewById(R.id.shop_phone_entry);
        TextView textViewShopNotes = (TextView) view.findViewById(R.id.shop_notes_entry);

        //textviewShopid.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
        textViewShopName.setText(cursor.getString(storenameoffset));
        textViewShopOrder.setText(cursor.getString(storeorderoffset));
        textViewShopStreet.setText(cursor.getString(storestreetofffset));
        textViewShopCity.setText(cursor.getString(storecityoffset));
        textViewShopState.setText(cursor.getString(storestateoffset));
        textViewShopPhone.setText(cursor.getString(storephoneoffset));
        textViewShopNotes.setText(cursor.getString(storenotesoffset));
    }
}
