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
    public static int shoplistidoffset;
    public static int shoplistproductrefoffset;
    public static int shoplistdateaddedoffset;
    public static int shoplistnumbertogetoffset;
    public static int shoplistdoneoffset;
    public static int shoplistdategotoffset;
    public static int shoplistcostoffset;
    public static int shoplistproductusagerefoffset;
    public static int shoplistaislerefoffset;
    public static int productusageproductrefoffset; // NOTE SQL uses AS productusageid
    public static int productusageaislerefoffset;
    public static int productusagecostoffset;
    public static int productusagebuycountoffset;
    public static int productusagefirstbuydateoffset;
    public static int productusagelatestbuydateoffset;
    public static int productusagemincostoffset;
    public static int productusageorderoffset;
    public static int aisleidoffset; // NOTE SQL uses AS aisleid
    public static int aislenameoffset;
    public static int aisleorderoffset;
    public static int aisleshopoffset;
    public static int shopsidoffset; // NOTE SQL uses  AS shopid
    public static int shopnameoffset;
    public static int shoporderoffset;
    public static int shopstreetoffset;
    public static int shopcityoffset;
    public static int shopstateoffset;
    public static int shopphoneoffset;
    public static int shopnotesoffset;
    public static int productidoffset; // NOTE SQL uses AS productid
    public static int productnameoffset;
    public static int productorderoffset;
    public static int productaisleoffset;
    public static int productusesoffset;
    public static int productnotesoffset;

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

        if(pos == 0) {
            shoplistidoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_ID);
            shoplistproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTREF);
            shoplistdateaddedoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEADDED);
            shoplistnumbertogetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_NUMBERTOGET);
            shoplistdoneoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DONE);
            shoplistdategotoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEGOT);
            shoplistcostoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_COST);
            shoplistproductusagerefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTUSAGEREF);
            shoplistaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_AISLEREF);
            productusageproductrefoffset = cursor.getColumnIndex("productusageid");
            productusageaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
            productusagebuycountoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
            productusagefirstbuydateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
            productusagelatestbuydateoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
            productusagemincostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
            productusageorderoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
            aisleidoffset = cursor.getColumnIndex("aisleid");
            aislenameoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            aisleorderoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
            aisleshopoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
            shopsidoffset = cursor.getColumnIndex("shopid");
            shopnameoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            shoporderoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
            shopstreetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
            shopcityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            shopstateoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
            shopphoneoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
            shopnotesoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
            productidoffset = cursor.getColumnIndex("productid");
            productnameoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            productorderoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
            productaisleoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
            productusesoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
            productnotesoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
        }

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
            prevshop = cursor.getLong(shopsidoffset); //  shopid column
            prevaisle = cursor.getLong(aisleidoffset); // aisleid column
            cursor.moveToNext(); // Restore cursor position to current
        }
        if(prevshop != cursor.getLong(shopsidoffset)) {
            shophdrll.setVisibility(View.VISIBLE);
            shophdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewheading));
        } else {
            shophdrll.setVisibility(View.GONE);
        }
        if(prevaisle != cursor.getLong(aisleidoffset)) {
            aislehdrll.setVisibility(View.VISIBLE);
            aislehdrll.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewsubheading));
        } else {
            aislehdrll.setVisibility(View.GONE);
        }
        shopnametv.setText(cursor.getString(shopnameoffset));
        shopcitytv.setText(cursor.getString(shopcityoffset));
        shopstreettv.setText(cursor.getString(shopstreetoffset));
        aislenametv.setText(cursor.getString(aislenameoffset));
        productnametv.setText(cursor.getString(productnameoffset));
        quantitytv.setText(cursor.getString(shoplistnumbertogetoffset));
        pricetv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(productusagecostoffset)));
        priceforalltv.setText(NumberFormat.getCurrencyInstance().format(cursor.getDouble(shoplistnumbertogetoffset) * cursor.getDouble(productusagecostoffset)));
        shopordertv.setText(cursor.getString(shoporderoffset));
        shopidtv.setText(cursor.getString(shopsidoffset));
        aisleordertv.setText(cursor.getString(aisleorderoffset));
        aisleidtv.setText(cursor.getString(aisleidoffset));
        puordertv.setText(cursor.getString(productusageorderoffset));
        puprodreftv.setText(cursor.getString(productusageproductrefoffset));
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
