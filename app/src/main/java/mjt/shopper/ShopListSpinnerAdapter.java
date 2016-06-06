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
    public static int storeidfoffset;
    public static int storenameoffset;
    public static int storeorderoffset;
    public static int storestreetofffset;
    public static int storecityoffset;
    public static int storestateoffset;
    public static int storephoneoffset;
    public static int storenotesoffset;

    public ShopListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_aisle_shop_list_entry, parent, false);
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
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
        TextView textViewShopName = (TextView) view.findViewById(R.id.aasletv01);
        TextView textViewShopStreet = (TextView) view.findViewById(R.id.aasletv03);
        TextView textViewShopCity = (TextView) view.findViewById(R.id.aasletv02);

        textViewShopName.setText(cursor.getString(storenameoffset));
        textViewShopStreet.setText(cursor.getString(storestreetofffset));
        textViewShopCity.setText(cursor.getString(storecityoffset));
    }
}
