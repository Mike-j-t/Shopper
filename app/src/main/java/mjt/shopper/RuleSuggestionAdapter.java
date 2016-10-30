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
 * Created by Mike092015 on 27/10/2016.
 */
public class RuleSuggestionAdapter extends CursorAdapter {


    /**
     * Extract from the query used to get the suggested rules
     *
     *
     productailseref * (productproductref * 100000) AS _id,
     productusage.productailseref,
     productusage.productproductref,
     productusage.productcost,
     productusage.productbuycount,
     productusage.productfirstbuydate,
     productusage.productlatestbuydate,
     products._id AS prodid,
     products.productname,
     aisles._id AS aisleid,
     aisles.aislename,
     aisles.aisleshopref,
     shops._id AS shopid,
     shops.shopname,
     shops.shopstreet,
     shops.shopcity,
     -- Include approximation of purchase frequency i.e. days between a purchase of 1 (lower # greater pruchase frequency)
     ((productusage.productlatestbuydate -
     productusage.productfirstbuydate) / 86400000) /
     productusage.productbuycount AS suggestedinterval
     */

    private static int rsl_calc_id_offset = -1;
    private static int rsl_pu_aislref_offest;
    private static int rsl_pu_productproductref_offset;
    private static int rsl_pu_productcost_offset;
    private static int rsl_pu_productbuycount_offset;
    private static int rsl_pu_productfirstbuydate_offset;
    private static int rsl_pu_productlatestbuydate_offset;
    private static int rsl_as_prodid_offset;
    private static int rsl_product_productname_offset;
    private static int rsl_as_aisleid_offset;
    private static int rsl_aisles_aislename_offset;
    private static int rsl_as_shopid_offset;
    private static int rsl_shops_shopname_offset;
    private static int rsl_shops_shopstreet_offset;
    private static int rsl_shops_shopcity_offset;
    private static int rsl_calc_frequency_offset;

    private static TextView addbutton;
    private static TextView dismissbutton;
    private static TextView disablebutton;


    public RuleSuggestionAdapter(Context context,Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setRuleSuggestionListOffets(cursor);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productname = (TextView) view.findViewById(R.id.rsle_product_name_entry);
        TextView shopname = (TextView) view.findViewById(R.id.rsle_shop_name_entry);
        TextView frequency = (TextView) view.findViewById(R.id.rsle_frequency_entry);
        TextView aislename = (TextView) view.findViewById(R.id.rsle_aisle_name_entry);

        productname.setText(cursor.getString(rsl_product_productname_offset));
        shopname.setText(cursor.getString(rsl_shops_shopname_offset) +
                " - " +
                cursor.getString(rsl_shops_shopcity_offset)
        );
        aislename.setText(cursor.getString(rsl_aisles_aislename_offset));
        frequency.setText("Add 1 " + productname.getText().toString() + " Every " +
                cursor.getString(rsl_calc_frequency_offset) +
                " days.");
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        View view = super.getView(position, contentview, parent);
        Context context = view.getContext();

        //Set button (TextViews) tag
        view.findViewById(R.id.rsle_addrulebutton_entry).setTag(position);
        view.findViewById(R.id.rsle_dismissrulebutton_entry).setTag(position);
        view.findViewById(R.id.rsle_disablerulebutton_entry).setTag(position);

        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.colorlistviewrowodd));
        }
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).
                inflate(R.layout.activity_rulesuggestion_entry,
                        parent,
                        false);
    }

    public void setRuleSuggestionListOffets(Cursor cursor) {

        // Already determined offsets?, if so return
        if(rsl_calc_id_offset != -1) {
            return;
        }

        // Interrogate the cursor for the offsets (index) according to
        // the column name.
        rsl_calc_id_offset = cursor.getColumnIndex("_id");
        rsl_pu_aislref_offest = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
        rsl_pu_productproductref_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        rsl_pu_productcost_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        rsl_pu_productbuycount_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
        rsl_pu_productfirstbuydate_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
        rsl_pu_productlatestbuydate_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
        rsl_as_prodid_offset = cursor.getColumnIndex("prodid");
        rsl_product_productname_offset = cursor.getColumnIndex(
                ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        rsl_as_aisleid_offset = cursor.getColumnIndex("aisleid");
        rsl_aisles_aislename_offset = cursor.getColumnIndex(
                ShopperDBHelper.AISLES_COLUMN_NAME);
        rsl_as_shopid_offset = cursor.getColumnIndex("shopid");
        rsl_shops_shopname_offset = cursor.getColumnIndex(
                ShopperDBHelper.SHOPS_COLUMN_NAME);
        rsl_shops_shopstreet_offset = cursor.getColumnIndex(
                ShopperDBHelper.SHOPS_COLUMN_STREET);
        rsl_shops_shopcity_offset = cursor.getColumnIndex(
                ShopperDBHelper.SHOPS_COLUMN_CITY);
        rsl_calc_frequency_offset = cursor.getColumnIndex("suggestedinterval");

    }
}
