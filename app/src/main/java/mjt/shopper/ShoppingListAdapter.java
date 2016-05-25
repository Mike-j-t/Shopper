package mjt.shopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.NumberFormat;

/**
 * Created by Mike092015 on 21/03/2016.
 */
public class ShoppingListAdapter extends CursorAdapter {

    public ShoppingListAdapter(Context context, Cursor cursor, int flags, int myvar) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();

        // Get the quantity (numbertoget) for this entry. 0 equates to all purchased so flag as such
        long quantity = this.getCursor().getLong(3);
        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        // If the quantity is 0 or less (should never be less) then flag as such
        // Change displayed fields to white, remove delete button and change Done button to Restore
        // Restore will allow the item to be re-introduced as an item to get (set to quantity of 1)
        // Otherwise ensure normal display.
        if(quantity < 1) {
            ((TextView) view.findViewById(R.id.shoppinglist_productname)).setTextColor(ContextCompat.getColor(context,R.color.colorNormalButtonText));
            ((TextView) view.findViewById(R.id.shoppinglist_quantity)).setTextColor(ContextCompat.getColor(context,R.color.colorNormalButtonText));
            ((TextView) view.findViewById(R.id.shoppinglist_price)).setTextColor(ContextCompat.getColor(context,R.color.colorNormalButtonText));
            ((TextView) view.findViewById(R.id.shoppinglist_priceforall)).setTextColor(ContextCompat.getColor(context, R.color.colorNormalButtonText));
            //((TextView) view.findViewById(R.id.shoppinglist_donebutton)).setText(R.string.standardrestorebutton);
            ((TextView) view.findViewById(R.id.shoppinglist_deletebutton)).setVisibility(View.INVISIBLE);
        } else {
            TextView dummy = new TextView(context);
            ColorStateList defaultcolor = dummy.getTextColors();
            ((TextView) view.findViewById(R.id.shoppinglist_productname)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_quantity)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_price)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_priceforall)).setTextColor(defaultcolor);
            //((TextView) view.findViewById(R.id.shoppinglist_donebutton)).setText(R.string.standarddonebutton);
            ((TextView) view.findViewById(R.id.shoppinglist_deletebutton)).setVisibility(View.VISIBLE);
        }
        return view;
    }
    @Override
    public void bindView(View view,Context context, Cursor cursor) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int pos = cursor.getPosition();

        LinearLayout shophdrll = (LinearLayout) view.findViewById(R.id.shoppinglist_shopheader);
        LinearLayout aislehdrll = (LinearLayout) view.findViewById(R.id.shoppinglist_aisleheader);

        TextView shopnametv = (TextView) view.findViewById(R.id.shoppinglist_shopname);
        TextView shopcitytv = (TextView) view.findViewById(R.id.shoppinglist_shopcity);
        TextView shopstreettv = (TextView) view.findViewById(R.id.shoppinglist_shopstreet);
        TextView aislenametv = (TextView) view.findViewById(R.id.shoppinglist_aislename);
        TextView productnametv = (TextView) view.findViewById(R.id.shoppinglist_productname);
        TextView quantitytv = (TextView) view.findViewById(R.id.shoppinglist_quantity);
        TextView pricetv = (TextView) view.findViewById(R.id.shoppinglist_price);
        TextView priceforalltv = (TextView) view.findViewById(R.id.shoppinglist_priceforall);
        TextView shopordertv = (TextView) view.findViewById(R.id.shoppinglist_shoporder);
        TextView shopidtv = (TextView) view.findViewById(R.id.shoppinglist_shopid);
        TextView aisleordertv = (TextView) view.findViewById(R.id.shoppinglist_aisleorder);
        TextView aisleidtv = (TextView) view.findViewById(R.id.shoppinglist_aisleid);
        TextView puordertv = (TextView) view.findViewById(R.id.shoppinglist_puorder);
        TextView puprodreftv = (TextView) view.findViewById(R.id.shoppinglist_puprodref);
        TextView donebtntv = (TextView) view.findViewById(R.id.shoppinglist_donebutton);
        TextView deletebtntv = (TextView) view.findViewById(R.id.shoppinglist_deletebutton);
        TextView replacebtntv = (TextView) view.findViewById(R.id.shoppinglist_replacebutton);

        // Need to check for a change in Shop or Aisle by looking at previous, if changed then
        // include the respective header(s).
        long prevshop = 0;
        long prevaisle = 0;
        // Only check if after the first, as first will always require headers, setting prevshop
        // and prevaisle to 0 (no such id's) will force this (as won't be changed)
        if(pos > 0 ) {
            cursor.moveToPrevious();
            prevshop = cursor.getLong(21); //  shopid column
            prevaisle = cursor.getLong(17); // aisleid column
            cursor.moveToNext(); // Restore cursor position to current
        }
        if(prevshop != cursor.getLong(21)) {
            shophdrll.setVisibility(View.VISIBLE);
            shophdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewheading));
        } else {
            shophdrll.setVisibility(View.GONE);
        }
        if(prevaisle != cursor.getLong(17)) {
            aislehdrll.setVisibility(View.VISIBLE);
            aislehdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewsubheading));
        } else {
            aislehdrll.setVisibility(View.GONE);
        }
        shopnametv.setText(cursor.getString(22));
        shopcitytv.setText(cursor.getString(25));
        shopstreettv.setText(cursor.getString(24));
        aislenametv.setText(cursor.getString(18));
        productnametv.setText(cursor.getString(30));
        quantitytv.setText(cursor.getString(3));
        pricetv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(6)));
        priceforalltv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(3) * cursor.getDouble(6)));
        shopordertv.setText(cursor.getString(23));
        shopidtv.setText(cursor.getString(21));
        aisleordertv.setText(cursor.getString(19));
        aisleidtv.setText(cursor.getString(17));
        puordertv.setText(cursor.getString(16));
        puprodreftv.setText(cursor.getString(9));
        // Set tags to enable onClick to determine the cursor position of the clicked entry
        donebtntv.setTag(pos);
        deletebtntv.setTag(pos);
        replacebtntv.setTag(pos);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //totalcost = 0;
        return LayoutInflater.from(context).inflate(R.layout.shopping_list_entry, parent, false);
    }
}
