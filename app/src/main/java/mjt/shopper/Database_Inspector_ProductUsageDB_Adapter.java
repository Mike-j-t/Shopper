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
 *
 */
public class Database_Inspector_ProductUsageDB_Adapter extends CursorAdapter{

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int productusage_productusageaislref_offset = -1;
    public static int productusage_productusageproductref_offset;
    public static int productusage_productusagecost_offset;
    public static int productusage_productusagebuycount_offset;
    public static int productusage_productusagefirstbuydate_offset;
    public static int productusage_productusagelatestbuydate_offset;
    public static int productusage_productusagemincost_offset;
    public static int productusage_productusageorder_offset;

    public Database_Inspector_ProductUsageDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context,cursor, 0);
        setProductUsageOffsets(cursor);
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
        TextView textviewproductusageaislref = (TextView) view.findViewById(R.id.adipue_productusagedb_aisleref);
        TextView textviewproductusageproductref = (TextView) view.findViewById(R.id.adipue_productusagedb_productref);
        TextView textviewproductusagecost = (TextView) view.findViewById(R.id.adipue_productusagedb_cost);
        TextView textviewproductusagebuycount = (TextView) view.findViewById(R.id.adipue_productusagedb_buycount);
        TextView textviewproductusagefirstbuydate = (TextView) view.findViewById(R.id.adipue_productusagedb_productfirstbuydate);
        TextView textviewproductusagelastbuydate = (TextView) view.findViewById(R.id.adipue_productusagedb_productlastbuydate);
        TextView textviewproductusagemincost = (TextView) view.findViewById(R.id.adipue_productusagedb_mincost);

        textviewproductusageaislref.setText(cursor.getString(productusage_productusageaislref_offset));
        textviewproductusageproductref.setText(cursor.getString(productusage_productusageproductref_offset));
        textviewproductusagecost.setText(NumberFormat.getCurrencyInstance().format(cursor.getFloat(productusage_productusagecost_offset)));
        textviewproductusagebuycount.setText(cursor.getString(productusage_productusagebuycount_offset));
        textviewproductusagefirstbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT,cursor.getLong(productusage_productusagefirstbuydate_offset)));
        textviewproductusagelastbuydate.setText(DateFormat.format(Constants.STANDARD_DDMMYYY_FORMAT, cursor.getLong(productusage_productusagelatestbuydate_offset)));
        textviewproductusagemincost.setText(NumberFormat.getCurrencyInstance().format(cursor.getLong(productusage_productusagemincost_offset)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_productusagedb_entry, parent, false);
    }

    public void setProductUsageOffsets(Cursor cursor) {
        if(productusage_productusageaislref_offset != -1) {
            return;
        }
        productusage_productusageaislref_offset = cursor.getColumnIndex(ShopperDBHelper.PRIMARY_KEY_NAME);
        productusage_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        productusage_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        productusage_productusagebuycount_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
        productusage_productusagefirstbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
        productusage_productusagelatestbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
        productusage_productusagemincost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
        productusage_productusageorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
    }
}
