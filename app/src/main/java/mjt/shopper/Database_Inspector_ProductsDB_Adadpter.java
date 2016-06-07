package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 17/02/2016.
 */
public class Database_Inspector_ProductsDB_Adadpter extends CursorAdapter {
    public Database_Inspector_ProductsDB_Adadpter(Context context, Cursor cursor, int flags) {
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

        TextView textviewproductid = (TextView) view.findViewById(R.id.adipe_productsdb_id);
        TextView textviewproductname = (TextView) view.findViewById(R.id.adipe_productsdb_name);
        TextView textviewproductorder = (TextView) view.findViewById(R.id.adipe_productsdb_order);
        TextView textviewproductaisle = (TextView) view.findViewById(R.id.adipe_productsdb_aisle);
        TextView textviewproductuses = (TextView) view.findViewById(R.id.adipe_productsdb_uses);
        TextView textviewproductnotes = (TextView) view.findViewById(R.id.adipe_productsdb_notes);

        textviewproductid.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX));
        textviewproductname.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX));
        textviewproductorder.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_ORDER_INDEX));
        textviewproductaisle.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_AISLE_INDEX));
        textviewproductuses.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_USES_INDEX));
        textviewproductnotes.setText(cursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_productsdb_entry,parent, false);
    }
}
