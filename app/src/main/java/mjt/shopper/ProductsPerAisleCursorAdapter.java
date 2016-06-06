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
    public static int productidoffset;
    public static int productnameoffset;
    public static int productorderoffset;
    public static int productaisleoffset;
    public static int productusesoffset;
    public static int productnotesoffset;
    public static int productusageaislerefoffset;
    public static int productusageproductrefoffset;
    public static int productusagecostoffset;
    public static int productusagebuycountoffset;
    public static int productusagefirstbuydateoffset;
    public static int productusagelastbuydateoffset;
    public static int productusagemincostoffset;
    public static int productusageorderinaisleoffset;
    public ProductsPerAisleCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
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
        if(cursor.getPosition() == 0) {
            productidoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID);
            productnameoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            productorderoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
            productaisleoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
            productusesoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
            productnotesoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
            productusageaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
            productusageproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
            productusagebuycountoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
            productusagefirstbuydateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
            productusagelastbuydateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
            productusagemincostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
            productusageorderinaisleoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
        }
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

        textviewproductid.setText(cursor.getString(productidoffset));
        textviewproductname.setText(cursor.getString(productnameoffset));
        textviewproductorder.setText(cursor.getString(productorderoffset));
        textviewproductasile.setText(cursor.getString(productaisleoffset));
        textviewproductuses.setText(cursor.getString(productusesoffset));
        textviewproductnotes.setText(cursor.getString(productnotesoffset));
        textviewproductusageaisleref.setText(cursor.getString(productusageaislerefoffset));
        textviewproductusageproductref.setText(cursor.getString(productusageproductrefoffset));
        textviewproductusagecost.setText((NumberFormat.getCurrencyInstance().format(cursor.getFloat(productusagecostoffset))));
        textviewproductusagebuycount.setText(cursor.getString(productusagebuycountoffset));
        textviewproductusagefirstbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT,cursor.getLong(productusagefirstbuydateoffset)));
        textviewproductusagelastbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT, cursor.getLong(productusagelastbuydateoffset)));
        textviewproductusagemincost.setText(cursor.getString(productusagemincostoffset));
        textviewproductusageorderinaisle.setText(cursor.getString(productusageorderinaisleoffset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_products_per_aisle_entry, parent, false);
    }
}
