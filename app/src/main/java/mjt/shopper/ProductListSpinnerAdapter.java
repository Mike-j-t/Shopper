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

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int products_productid_offset = -1;
    public static int products_productname_offset;
    public static int products_productorder_offset;
    public static int products_productaisleref_offset;
    public static int products_productuses_offset;
    public static int products_productnotes_offset;

    public ProductListSpinnerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setProductsOffsets(cursor);
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

        textviewproductid.setText(cursor.getString(products_productid_offset));
        textviewproductid.setVisibility(View.GONE);
        textviewproductname.setText(cursor.getString(products_productname_offset));
        textviewproductorder.setText(cursor.getString(products_productorder_offset));
        textviewproductorder.setVisibility(View.GONE);
        textviewproductaisleref.setText(cursor.getString(products_productaisleref_offset));
        textviewproductaisleref.setVisibility(View.GONE);
        textviewproductuses.setText(cursor.getString(products_productuses_offset));
        textviewproductuses.setVisibility(View.GONE);
        textviewproductnotes.setText(cursor.getString(products_productnotes_offset));
    }

    // Set Products Table query offsets into returned cursor, if not already set
    public void setProductsOffsets(Cursor cursor) {
        if(products_productid_offset != -1) {
            return;
        }
        products_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID);
        products_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        products_productorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
        products_productaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
        products_productuses_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
        products_productnotes_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
    }
}
