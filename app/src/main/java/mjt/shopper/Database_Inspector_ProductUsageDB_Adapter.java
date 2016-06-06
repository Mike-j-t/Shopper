package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.text.format.DateFormat;

import java.text.NumberFormat;

/**
 * Created by Mike092015 on 17/02/2016.
 */
public class Database_Inspector_ProductUsageDB_Adapter extends CursorAdapter{
    public static int productusageaislrefoffset;
    public static int productusageproductrefoffset;
    public static int productusagecostoffset;
    public static int productusagebuycountoffset;
    public static int productusagefirstbudateoffset;
    public static int productusagelatestbutdateoffset;
    public static int productusagemincostoffset;

    public Database_Inspector_ProductUsageDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context,cursor, 0);
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
            productusageaislrefoffset = cursor.getColumnIndex("_id"); // Note set to id via as in SQL
            productusageproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
            productusagebuycountoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
            productusagefirstbudateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
            productusagelatestbutdateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
            productusagemincostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
        }
        TextView textviewproductusageaislref = (TextView) view.findViewById(R.id.adipue_productusagedb_aisleref);
        TextView textviewproductusageproductref = (TextView) view.findViewById(R.id.adipue_productusagedb_productref);
        TextView textviewproductusagecost = (TextView) view.findViewById(R.id.adipue_productusagedb_cost);
        TextView textviewproductusagebuycount = (TextView) view.findViewById(R.id.adipue_productusagedb_buycount);
        TextView textviewproductusagefirstbuydate = (TextView) view.findViewById(R.id.adipue_productusagedb_productfirstbuydate);
        TextView textviewproductusagelastbuydate = (TextView) view.findViewById(R.id.adipue_productusagedb_productlastbuydate);
        TextView textviewproductusagemincost = (TextView) view.findViewById(R.id.adipue_productusagedb_mincost);

        textviewproductusageaislref.setText(cursor.getString(productusageaislrefoffset));
        textviewproductusageproductref.setText(cursor.getString(productusageproductrefoffset));
        textviewproductusagecost.setText(NumberFormat.getCurrencyInstance().format(cursor.getFloat(productusagecostoffset)));
        textviewproductusagebuycount.setText(cursor.getString(productusagebuycountoffset));
        textviewproductusagefirstbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT,cursor.getLong(productusagefirstbudateoffset)));
        textviewproductusagelastbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT, cursor.getLong(productusagelatestbutdateoffset)));
        textviewproductusagemincost.setText(NumberFormat.getCurrencyInstance().format(cursor.getLong(productusagemincostoffset)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_productusagedb_entry, parent, false);
    }
}
