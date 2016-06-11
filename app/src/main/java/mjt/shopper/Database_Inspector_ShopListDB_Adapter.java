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

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int shoppinglist_shoplistid_offset = -1;
    public static int shoppinglist_shoplistproductref_offset;
    public static int shoppinglist_shoplistdateadded_offset;
    public static int shoppinglist_shoplistnumbertoget_offset;
    public static int shoppinglist_shoplistdone_offset;
    public static int shoppinglist_shoplistdategot_offset;
    public static int shoppinglist_shoplistcost_offset;
    public static int shoppinglist_shoplistproductusageref_offset;
    public static int shoppinglist_shoplistaisleref_offset;


    public Database_Inspector_ShopListDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setShoppingListOffsets(cursor);
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

        tvshoplistid.setText(cursor.getString(shoppinglist_shoplistid_offset));
        tvshoplistproductref.setText(cursor.getString(shoppinglist_shoplistproductref_offset));
        tvshoplistdateadded.setText(cursor.getString(shoppinglist_shoplistdateadded_offset));
        tvshoplistnumbertoget.setText(cursor.getString(shoppinglist_shoplistnumbertoget_offset));
        tvshoplistdone.setText(cursor.getString(shoppinglist_shoplistdone_offset));
        tvshoplistdategot.setText(cursor.getString(shoppinglist_shoplistdategot_offset));
        tvshoplistcost.setText(NumberFormat.getCurrencyInstance().format(cursor.getFloat(shoppinglist_shoplistcost_offset)));
        tvshoplistproductusageref.setText(cursor.getString(shoppinglist_shoplistproductusageref_offset));
        tvshoplistaisleref.setText(cursor.getString(shoppinglist_shoplistaisleref_offset));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_shoplistdb_entry,parent,false);
    }

    public void setShoppingListOffsets(Cursor cursor) {
        if(shoppinglist_shoplistid_offset != -1) {
            return;
        }
        shoppinglist_shoplistid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_ID);
        shoppinglist_shoplistproductref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTREF);
        shoppinglist_shoplistdateadded_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEADDED);
        shoppinglist_shoplistnumbertoget_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_NUMBERTOGET);
        shoppinglist_shoplistdone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DONE);
        shoppinglist_shoplistdategot_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEGOT);
        shoppinglist_shoplistcost_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_COST);
        shoppinglist_shoplistproductusageref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTUSAGEREF);
        shoppinglist_shoplistaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_AISLEREF);
    }
}
