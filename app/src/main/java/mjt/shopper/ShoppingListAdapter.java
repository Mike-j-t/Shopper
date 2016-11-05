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
 *
 */
public class ShoppingListAdapter extends CursorAdapter {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    private static int shoppinglist_shoplistid_offset = -1;
    private static int shoppinglist_shoplistproductref_offset;
    private static int shoppinglist_shoplistdateadded_offset;
    private static int shoppinglist_shoplistnumbertoget_offset;
    private static int shoppinglist_shoplistdone_offset;
    private static int shoppinglist_shoplistdategot_offset;
    private static int shoppinglist_shoplistcost_offset;
    private static int shoppinglist_shoplistproductusageref_offset;
    private static int shoppinglist_shoplistaisleref_offset;
    private static int shoppinglist_productusageproductref_offset;
    private static int shoppinglist_productusageaisleref_offset;
    private static int shoppinglist_productusagecost_offset;
    private static int shoppinglist_productusagebuycount_offset;
    private static int shoppinglist_productusagefirstbuydate_offset;
    private static int shoppinglist_productusagelatestbuydate_offset;
    private static int shoppinglist_productusagemincost_offset;
    private static int shoppinglist_productusageorder_offset;
    private static int shoppinglist_aisleid_offset;
    private static int shoppinglist_aislename_offset;
    private static int shoppinglist_aisleorder_offset;
    private static int shoppinglist_aisleshopref_offest;
    private static int shoppinglist_shopid_offset;
    private static int shoppinglist_shopname_offset;
    private static int shopponglist_shoporder_offset;
    private static int shopponglist_shopstreet_offset;
    private static int shoppinglist_shopcity_offset;
    private static int shoppinglist_shopstate_offset;
    private static int shoppinglist_shopphone_offset;
    private static int shoppinglist_shopnotes_offset;
    private static int shoppinglist_productid_offset;
    private static int shoppinglist_productname_offset;
    private static int shoppinglist_productorder_offset;
    private static int shoppinglist_productaisleref_offset;
    private static int shoppinglist_productuses_offset;
    private static int shoppinglist_productnotes_offset;


    public ShoppingListAdapter(Context context, Cursor cursor, int flags, int myvar) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setShoppingListOffsets(cursor);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();

        // Get the quantity (numbertoget) for this entry. 0 equates to all purchased so flag as such
        long quantity = this.getCursor().getLong(shoppinglist_shoplistnumbertoget_offset);
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
            ((TextView) view.findViewById(R.id.shoppinglist_deletebutton)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.shoppinglist_donebutton)).setVisibility(View.INVISIBLE);
        } else {
            TextView dummy = new TextView(context);
            ColorStateList defaultcolor = dummy.getTextColors();
            ((TextView) view.findViewById(R.id.shoppinglist_productname)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_quantity)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_price)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_priceforall)).setTextColor(defaultcolor);
            ((TextView) view.findViewById(R.id.shoppinglist_deletebutton)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.shoppinglist_donebutton)).setVisibility(View.VISIBLE);
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
        TextView replacebtntv = (TextView) view.findViewById(R.id.shoppinglist_adjustbutton);

        // Need to check for a change in Shop or Aisle by looking at previous, if changed then
        // include the respective header(s).
        long prevshop = 0;
        long prevaisle = 0;
        // Only check if after the first, as first will always require headers, setting prevshop
        // and prevaisle to 0 (no such id's) will force this (as won't be changed)
        if(pos > 0 ) {
            cursor.moveToPrevious();
            prevshop = cursor.getLong(shoppinglist_shopid_offset); //  shopid column
            prevaisle = cursor.getLong(shoppinglist_aisleid_offset); // aisleid column
            cursor.moveToNext(); // Restore cursor position to current
        }
        if(prevshop != cursor.getLong(shoppinglist_shopid_offset)) {
            shophdrll.setVisibility(View.VISIBLE);
            shophdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewheading));
        } else {
            shophdrll.setVisibility(View.GONE);
        }
        if(prevaisle != cursor.getLong(shoppinglist_aisleid_offset)) {
            aislehdrll.setVisibility(View.VISIBLE);
            aislehdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewsubheading));
        } else {
            aislehdrll.setVisibility(View.GONE);
        }
        shopnametv.setText(cursor.getString(shoppinglist_shopname_offset));
        shopcitytv.setText(cursor.getString(shoppinglist_shopcity_offset));
        shopstreettv.setText(cursor.getString(shopponglist_shopstreet_offset));
        aislenametv.setText(cursor.getString(shoppinglist_aislename_offset));
        productnametv.setText(cursor.getString(shoppinglist_productname_offset));
        quantitytv.setText(cursor.getString(shoppinglist_shoplistnumbertoget_offset));
        pricetv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(shoppinglist_productusagecost_offset)));
        priceforalltv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(shoppinglist_shoplistnumbertoget_offset) * cursor.getDouble(shoppinglist_productusagecost_offset)));
        shopordertv.setText(cursor.getString(shopponglist_shoporder_offset));
        shopidtv.setText(cursor.getString(shoppinglist_shopid_offset));
        aisleordertv.setText(cursor.getString(shoppinglist_aisleorder_offset));
        aisleidtv.setText(cursor.getString(shoppinglist_aisleid_offset));
        puordertv.setText(cursor.getString(shoppinglist_productusageorder_offset));
        puprodreftv.setText(cursor.getString(shoppinglist_productusageproductref_offset));
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

    private void setShoppingListOffsets(Cursor cursor) {
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
        shoppinglist_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF_FULL);
        shoppinglist_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
        shoppinglist_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        shoppinglist_productusagebuycount_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
        shoppinglist_productusagefirstbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
        shoppinglist_productusagelatestbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
        shoppinglist_productusagemincost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
        shoppinglist_productusageorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
        shoppinglist_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID_FULL);
        shoppinglist_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        shoppinglist_aisleorder_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
        shoppinglist_aisleshopref_offest = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        shoppinglist_shopid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID_FULL);
        shoppinglist_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        shopponglist_shoporder_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
        shopponglist_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        shoppinglist_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        shoppinglist_shopstate_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
        shoppinglist_shopphone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
        shoppinglist_shopnotes_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
        shoppinglist_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID_FULL);
        shoppinglist_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        shoppinglist_productorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
        shoppinglist_productaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
        shoppinglist_productuses_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
        shoppinglist_productnotes_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
    }
}
