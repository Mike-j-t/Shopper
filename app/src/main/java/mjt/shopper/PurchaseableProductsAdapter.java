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
 *
 */
class PurchaseableProductsAdapter extends CursorAdapter {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    //Purchasable Products Query
    private static int purchasableproducts_productusageaisleref_offset = -1; //**
    private static int purchasableproducts_productusageproductref_offset;
    private static int purchasableproducts_productusagecost_offset;
    private static int purchasableproducts_productid_offset; //**
    private static int purchasableproducts_productname_offset;
    private static int purchasableproducts_aisleid_offset; //**
    private static int purchasableproducts_aislename_offset;
    private static int purchasableproducts_shopname_offset;
    private static int purchasableproducts_shopcity_offset;
    private static int purchasableproducts_shopstreet_offset;

    PurchaseableProductsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setPurchasableProductsOffsets(cursor);
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

        tvproductname.setText(cursor.getString(purchasableproducts_productname_offset));
        tvshopname.setText(cursor.getString(purchasableproducts_shopname_offset));
        tvshopcity.setText(cursor.getString(purchasableproducts_shopcity_offset));
        tvshopstreet.setText(cursor.getString(purchasableproducts_shopstreet_offset));
        tvaislename.setText(cursor.getString(purchasableproducts_aislename_offset));
        tvcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(purchasableproducts_productusagecost_offset)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.purchaseable_product_list_entry, parent, false);
    }

    private void setPurchasableProductsOffsets(Cursor cursor) {
        if(purchasableproducts_productusageaisleref_offset != -1) {
            return;
        }
        purchasableproducts_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRIMARY_KEY_NAME);
        purchasableproducts_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        purchasableproducts_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        purchasableproducts_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID_FULL);
        purchasableproducts_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        purchasableproducts_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID_FULL);
        purchasableproducts_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        purchasableproducts_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        purchasableproducts_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        purchasableproducts_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
    }
}
