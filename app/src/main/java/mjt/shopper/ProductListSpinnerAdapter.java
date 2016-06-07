package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 14/02/2016.
 */
public class ProductListSpinnerAdapter extends CursorAdapter{
    public ProductListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_product_list_entry, parent, false);
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        TextView textviewproductid = (TextView) view.findViewById(R.id.product_id_entry);
        TextView textviewproductname = (TextView) view.findViewById(R.id.product_name_entry);
        TextView textviewproductorder = (TextView) view.findViewById(R.id.product_order_entry);
        TextView textviewproductaisleref = (TextView) view.findViewById(R.id.product_aisle_entry);
        TextView textviewproductnotes = (TextView) view.findViewById(R.id.product_notes_entry);
        TextView textviewproductuses = (TextView) view.findViewById(R.id.product_uses_entry);

        textviewproductid.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX));
        textviewproductid.setVisibility(View.GONE);
        textviewproductname.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX));
        textviewproductorder.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ORDER_INDEX));
        textviewproductorder.setVisibility(View.GONE);
        textviewproductaisleref.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_AISLE_INDEX));
        textviewproductaisleref.setVisibility(View.GONE);
        textviewproductuses.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_USES_INDEX));
        textviewproductuses.setVisibility(View.GONE);
        textviewproductnotes.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX));
    }
}
