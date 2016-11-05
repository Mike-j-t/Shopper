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
 * Custom Cursor adapter for review of disabled rule suggestions
 * More specifically, a rule itself isn't actually disabled, rather a
 * productusage (combination of a product and aisle) has a flag set
 * disabling it's inclusion in rule suggestion.
 *
 * This adapter caters for a cursor consisting of the following columns:-
 *      a calculated pseudo _id column (overcomes issues as _id is expected)
 *      product name
 *      product reference (long) i.e. pointer from productusage to the product
 *      aisle reference (long) i.e. pointer from product usage to the aisle
 *      aisle name
 *      shop name
 *      shop city
 *      shop street (not used)
 *
 *  See getDisabledRules() method in ShopperDBHelper for generation of the cursor
 */
class ReviewDisabledRuleSuggestionsAdapter extends CursorAdapter {

    private Cursor cursor;

    private static int drsl_calc_id_offset = -1;
    private static int drsl_product_productname_offset;
    private static int drsl_pu_productref_offset;
    private static int drsl_pu_aisleref_offset;
    private static int drsl_aisles_name_offset;
    private static int drsl_shops_name_offset;
    private static int drsl_shops_city_offset;
    private static int drsl_shops_street_offset;

    /**
     *
     * @param context
     * @param cursor
     * @param flags
     *
     * Note! that method setDisabledRulesOffsets is called to set offsets just
     * once
     */
    ReviewDisabledRuleSuggestionsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        this.cursor = cursor;
        setDisabledRulesListOffsets();
    }

    /**
     *
     * @param context   context passed from invoking activity
     * @param cursor    curors passed
     * @param parent    The listview to which the view is being added
     * @return View     The view that has been added to the ListView
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_reviewdisabledrulesuggestions_entry, parent, false);
    }

    /**
     *
     * @param view          The view within the list view being processed
     * @param context       The context, passsed down from the invoking activity
     * @param cursor        The cursor passed
     *
     * Set the product name, the aisle name and the shopname textfields with
     * the respective data from the cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int pos = cursor.getPosition();
        TextView productname = (TextView) view.findViewById(R.id.rdrse_product_name_entry);
        TextView aislename = (TextView) view.findViewById(R.id.rdrse_aisle_name_entry);
        TextView shopname = (TextView) view.findViewById(R.id.rdrse_shop_name_entry);

        productname.setText(cursor.getString(drsl_product_productname_offset));
        aislename.setText(cursor.getString(drsl_aisles_name_offset));
        String city = cursor.getString(drsl_shops_city_offset);
        shopname.setText(cursor.getString(drsl_shops_name_offset) + " - " + city);
    }

    /**
     *
     * @param position      position withing the listview (and therefore cursor)
     * @param convertview   the specific view within the listview
     * @param parent        the parent of the view (the listview)
     * @return view         the modified view
     *
     * Change the background colour of the view according to the posoition
     * to alternate background colours
     *
     * set the view's button view (actually a TextView acting as a button) tag
     * to position, thus allowing the button to be identified via it's tag
     */
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();
        if(position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        view.findViewById(R.id.rdrse_enablerulebutton_entry).setTag(position);

        return view;
    }

    /**
     * Determine the cursor offsets just once to reduce overheads of repeatedly
     * determening the cursor offset from a given name.
     *
     */
    private void setDisabledRulesListOffsets() {
        if(drsl_calc_id_offset == -1) {
            drsl_calc_id_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.PRIMARY_KEY_NAME
                    );
            drsl_product_productname_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.PRODUCTS_COLUMN_NAME
                    );
            drsl_pu_productref_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF
                    );
            drsl_pu_aisleref_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF
                    );
            drsl_aisles_name_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.AISLES_COLUMN_NAME
                    );
            drsl_shops_name_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.SHOPS_COLUMN_NAME
                    );
            drsl_shops_city_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.SHOPS_COLUMN_CITY
                    );
            drsl_shops_street_offset =
                    cursor.getColumnIndex(
                            ShopperDBHelper.SHOPS_COLUMN_STREET
                    );
        }
    }
}
