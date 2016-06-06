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
 * Created by Mike092015 on 8/03/2016.
 */
public class Database_Inspector_ShopListDB_Adapter extends CursorAdapter {
    public static int shoplistidoffset;
    public static int shoplistproductrefoffset;
    public static int shoplistdateaddedoffset;
    public static int shoplistnumbertogetoffset;
    public static int shoplistdoneoffset;
    public static int shoplistdategotoffset;
    public static int shoplistcostoffset;
    public static int shoplistproductusagerefoffset;
    public static int shoplistaislerefoffset;

    public Database_Inspector_ShopListDB_Adapter(Context context, Cursor cursor, int flags) {
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
            shoplistidoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_ID);
            shoplistproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTREF);
            shoplistdateaddedoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEADDED);
            shoplistnumbertogetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_NUMBERTOGET);
            shoplistdoneoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DONE);
            shoplistdategotoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEGOT);
            shoplistcostoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_COST);
            shoplistproductusagerefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTUSAGEREF);
            shoplistproductusagerefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_AISLEREF);
        }
        TextView tvshoplistid = (TextView) view.findViewById(R.id.adise_shoplistdb_id);
        TextView tvshoplistproductref = (TextView) view.findViewById(R.id.adise_shoplistdb_productref);
        TextView tvshoplistdateadded = (TextView) view.findViewById(R.id.adise_shoplistdb_dateadded);
        TextView tvshoplistnumbertoget = (TextView) view.findViewById(R.id.adise_shoplistdb_numbertoget);
        TextView tvshoplistdone = (TextView) view.findViewById(R.id.adise_shoplistdb_done);
        TextView tvshoplistdategot = (TextView) view.findViewById(R.id.adise_shoplistdb_dategot);
        TextView tvshoplistcost = (TextView) view.findViewById(R.id.adise_shoplistdb_cost);
        TextView tvshoplistproductusageref = (TextView) view.findViewById(R.id.adise_shoplistdb_productusageref);
        TextView tvshoplistaisleref = (TextView) view.findViewById(R.id.adise_shoplistdb_aisleref);

        tvshoplistid.setText(cursor.getString(shoplistidoffset));
        tvshoplistproductref.setText(cursor.getString(shoplistproductrefoffset));
        tvshoplistdateadded.setText(cursor.getString(shoplistdateaddedoffset));
        tvshoplistnumbertoget.setText(cursor.getString(shoplistnumbertogetoffset));
        tvshoplistdone.setText(cursor.getString(shoplistdoneoffset));
        tvshoplistdategot.setText(cursor.getString(shoplistdategotoffset));
        tvshoplistcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getFloat(shoplistcostoffset)));
        tvshoplistproductusageref.setText(cursor.getString(shoplistproductusagerefoffset));
        tvshoplistaisleref.setText(cursor.getString(shoplistaislerefoffset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shoplistdb_entry,parent,false);
    }
}
