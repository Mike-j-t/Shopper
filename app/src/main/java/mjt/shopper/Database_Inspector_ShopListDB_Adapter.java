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
        TextView tvshoplistid = (TextView) view.findViewById(R.id.adise_shoplistdb_id);
        TextView tvshoplistproductref = (TextView) view.findViewById(R.id.adise_shoplistdb_productref);
        TextView tvshoplistdateadded = (TextView) view.findViewById(R.id.adise_shoplistdb_dateadded);
        TextView tvshoplistnumbertoget = (TextView) view.findViewById(R.id.adise_shoplistdb_numbertoget);
        TextView tvshoplistdone = (TextView) view.findViewById(R.id.adise_shoplistdb_done);
        TextView tvshoplistdategot = (TextView) view.findViewById(R.id.adise_shoplistdb_dategot);
        TextView tvshoplistcost = (TextView) view.findViewById(R.id.adise_shoplistdb_cost);
        TextView tvshoplistproductusageref = (TextView) view.findViewById(R.id.adise_shoplistdb_productusageref);
        TextView tvshoplistaisleref = (TextView) view.findViewById(R.id.adise_shoplistdb_aisleref);

        tvshoplistid.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_ID_INDEX));
        tvshoplistproductref.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTREF_INDEX));
        tvshoplistdateadded.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_DATEADDED_INDEX));
        tvshoplistnumbertoget.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_NUMBERTOGET_INDEX));
        tvshoplistdone.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_DONE_INDEX));
        tvshoplistdategot.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_DATEGOT_INDEX));
        tvshoplistcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getFloat(ShopperDBHelper.SHOPLIST_COLUMN_COST_INDEX)));
        tvshoplistproductusageref.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTUSAGEREF_INDEX));
        tvshoplistaisleref.setText(cursor.getString(ShopperDBHelper.SHOPLIST_COLUMN_AISLEREF_INDEX));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shoplistdb_entry,parent,false);
    }
}
