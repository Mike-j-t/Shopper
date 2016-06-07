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

        textviewproductid.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX));
        textviewproductname.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX));
        textviewproductorder.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ORDER_INDEX));
        textviewproductasile.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_AISLE_INDEX));
        textviewproductuses.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_USES_INDEX));
        textviewproductnotes.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX));
        int joinoffset = ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX + 1;
        textviewproductusageaisleref.setText(cursor.getString((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF_INDEX)));
        textviewproductusageproductref.setText(cursor.getString((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF_INDEX)));
        textviewproductusagecost.setText((NumberFormat.getCurrencyInstance().format(cursor.getFloat((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST_INDEX)))));
        textviewproductusagebuycount.setText(cursor.getString((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT_INDEX)));
        textviewproductusagefirstbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT,cursor.getLong((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE_INDEX))));
        textviewproductusagelastbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT, cursor.getLong((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_LASTBUYDATE_INDEX))));
        textviewproductusagemincost.setText(cursor.getString((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST_INDEX)));
        textviewproductusageorderinaisle.setText(cursor.getString((joinoffset + ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER_INDEX)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_products_per_aisle_entry, parent, false);
    }
}
