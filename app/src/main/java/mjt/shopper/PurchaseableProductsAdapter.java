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
    public static int productusageaislerefoffest;
    public static int productusageproductrefoffset;
    public static int productusagecostoffset;
    public static int productidoffset;
    public static int productnameoffset;
    public static int aisleidoffset;
    public static int aislenameoffset;
    public static int shopnameofffset;
    public static int shopcityoffset;
    public static int shopstreetoffset;
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
        if(cursor.getPosition() == 0) {
            productusageaislerefoffest = cursor.getColumnIndex("_ID"); // Note! SQL uses AS _ID
            productusageproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
            productidoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_TABLE_NAME+ShopperDBHelper.PRODUCTS_COLUMN_ID);
            productnameoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            aisleidoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_TABLE_NAME+ShopperDBHelper.AISLES_COLUMN_ID);
            aislenameoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            shopnameofffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            shopcityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            shopstreetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        }
        TextView tvproductname = (TextView) view.findViewById(R.id.product_name_entry);
        TextView tvshopname = (TextView) view.findViewById(R.id.shop_name_entry);
        TextView tvshopcity = (TextView) view.findViewById(R.id.shop_city_entry);
        TextView tvshopstreet = (TextView) view.findViewById(R.id.shop_street_entry);
        TextView tvaislename = (TextView) view.findViewById(R.id.aisle_name_entry);
        TextView tvcost = (TextView) view.findViewById(R.id.cost_entry);

        tvproductname.setText(cursor.getString(productnameoffset));
        tvshopname.setText(cursor.getString(shopnameofffset));
        tvshopcity.setText(cursor.getString(shopcityoffset));
        tvshopstreet.setText(cursor.getString(shopstreetoffset));
        tvaislename.setText(cursor.getString(aislenameoffset));
        tvcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(productusagecostoffset)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.purchaseable_product_list_entry, parent, false);
    }
}
