package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Mike092015 on 16/03/2016.
 */
public class PurchaseableProductsAdapter extends CursorAdapter {
    public PurchaseableProductsAdapter(Context context, Cursor cursor, int flags) {
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
        TextView tvproductname = (TextView) view.findViewById(R.id.product_name_entry);
        TextView tvshopname = (TextView) view.findViewById(R.id.shop_name_entry);
        TextView tvshopcity = (TextView) view.findViewById(R.id.shop_city_entry);
        TextView tvshopstreet = (TextView) view.findViewById(R.id.shop_street_entry);
        TextView tvaislename = (TextView) view.findViewById(R.id.aisle_name_entry);
        TextView tvcost = (TextView) view.findViewById(R.id.cost_entry);

        tvproductname.setText(cursor.getString(4));
        tvshopname.setText(cursor.getString(7));
        tvshopcity.setText(cursor.getString(8));
        tvshopstreet.setText(cursor.getString(9));
        tvaislename.setText(cursor.getString(6));
        tvcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(2)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.purchaseable_product_list_entry, parent, false);
    }
}
