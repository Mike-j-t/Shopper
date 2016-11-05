package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Created by Mike092015 on 27/02/2016.
 */
public class ProductsPerAisleCursorAdapter extends CursorAdapter implements Serializable {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.


    // Variables to store productsperaisle query offsets as obtained via the defined column names by
    // call to setProductsPerAisleOffsets (productsperaisle_productid_offset set -1 to act as notdone flag )
    public static int productsperaisle_productid_offset = -1;
    public static int productsperaisle_productname_offset;
    public static int productsperaisle_productorder_offset;
    public static int productsperaisle_productaisleref_offset;
    public static int productsperaisle_productuses_offset;
    public static int productsperaisle_productnotes_offset;
    public static int productsperaisle_productusageaisleref_offset;
    public static int productsperaisle_productusageproductref_offset;
    public static int productsperaisle_productusagecost_offset;
    public static int productsperaisle_productusagebuycount_offset;
    public static int productsperaisle_productusagefirstbuydate_offset;
    public static int proudctsperaisle_productusagelatestbuydate_offset;
    public static int productsaperaisle_productusagemincost_offset;
    public static int productsperaisle_productusageorder_offset;

    public ProductsPerAisleCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setProductsPerAisleOffsets(cursor);
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
        TextView textviewproductid = (TextView) view.findViewById(R.id.product_id_entry);
        TextView textviewproductname = (TextView) view.findViewById(R.id.product_name_entry);
        TextView textviewproductorder = (TextView) view.findViewById(R.id.product_order_entry);
        TextView textviewproductasile = (TextView) view.findViewById(R.id.product_aisle_entry);
        TextView textviewproductuses = (TextView) view.findViewById(R.id.product_uses_entry);
        TextView textviewproductnotes = (TextView) view.findViewById(R.id.product_notes_entry);
        TextView textviewproductusageaisleref = (TextView) view.findViewById(R.id.productusage_aisleref_entry);
        TextView textviewproductusageproductref = (TextView) view.findViewById(R.id.productusage_productref_entry);
        TextView textviewproductusagecost = (TextView) view.findViewById(R.id.productusage_cost_entry);
        TextView textviewproductusagebuycount = (TextView) view.findViewById(R.id.productusage_buycount_entry);
        TextView textviewproductusagefirstbuydate = (TextView) view.findViewById(R.id.productusage_firstbuydate_entry);
        TextView textviewproductusagelastbuydate = (TextView) view.findViewById(R.id.productusage_lastbuydate_entry);
        TextView textviewproductusagemincost = (TextView) view.findViewById(R.id.productusage_mincost_entry);
        TextView textviewproductusageorderinaisle = (TextView) view.findViewById(R.id.productusage_orderinaisle_entry);

        textviewproductid.setText(cursor.getString(productsperaisle_productid_offset));
        textviewproductname.setText(cursor.getString(productsperaisle_productname_offset));
        textviewproductorder.setText(cursor.getString(productsperaisle_productorder_offset));
        textviewproductasile.setText(cursor.getString(productsperaisle_productaisleref_offset));
        textviewproductuses.setText(cursor.getString(productsperaisle_productuses_offset));
        textviewproductnotes.setText(cursor.getString(productsperaisle_productnotes_offset));
        //int joinoffset = ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX + 1;
        textviewproductusageaisleref.setText(cursor.getString(productsperaisle_productusageaisleref_offset));
        textviewproductusageproductref.setText(cursor.getString(productsperaisle_productusageproductref_offset));
        textviewproductusagecost.setText((NumberFormat.getCurrencyInstance().format(cursor.getFloat(productsperaisle_productusagecost_offset))));
        textviewproductusagebuycount.setText(cursor.getString(productsperaisle_productusagebuycount_offset));
        textviewproductusagefirstbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT,cursor.getLong(productsperaisle_productusagefirstbuydate_offset)));
        textviewproductusagelastbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT, cursor.getLong(proudctsperaisle_productusagelatestbuydate_offset)));
        textviewproductusagemincost.setText(cursor.getString(productsaperaisle_productusagemincost_offset));
        textviewproductusageorderinaisle.setText(cursor.getString(productsperaisle_productusageorder_offset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_products_per_aisle_entry, parent, false);
    }

    // Set ProductsPerAisle query offsets into returned cursor, if not already set
    public void setProductsPerAisleOffsets(Cursor cursor) {
        if(productsperaisle_productid_offset != -1) {
            return;
        }
        productsperaisle_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID);
        productsperaisle_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        productsperaisle_productorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
        productsperaisle_productaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
        productsperaisle_productuses_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
        productsperaisle_productnotes_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
        productsperaisle_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
        productsperaisle_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        productsperaisle_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        productsperaisle_productusagebuycount_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
        productsperaisle_productusagefirstbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
        proudctsperaisle_productusagelatestbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
        productsaperaisle_productusagemincost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
        productsperaisle_productusageorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
    }
}
