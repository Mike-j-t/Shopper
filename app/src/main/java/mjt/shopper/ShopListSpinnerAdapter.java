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

    public ShopListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
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

        textViewShopName.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
        textViewShopStreet.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
        textViewShopCity.setText(cursor.getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
    }
}
