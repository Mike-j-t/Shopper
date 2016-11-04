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
 *
 */
public class ReviewDisabledRuleSuggestionsAdapter extends CursorAdapter {

    private Cursor cursor;

    private static int drsl_calc_id_offset = -1;
    private static int drsl_product_productname_offset;
    private static int drsl_pu_productref_offset;
    private static int drsl_pu_aisleref_offset;
    private static int drsl_aisles_name_offset;
    private static int drsl_shops_name_offset;
    private static int drsl_shops_city_offset;
    private static int drsl_shops_street_offset;


    public ReviewDisabledRuleSuggestionsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        this.cursor = cursor;
        setDisabledRulesListOffsets();

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_reviewdisabledrulesuggestions_entry, parent, false);
    }

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

    public void setDisabledRulesListOffsets() {
        if(drsl_calc_id_offset != -1) {
        } else {
            drsl_calc_id_offset = cursor.getColumnIndex(ShopperDBHelper.PRIMARY_KEY_NAME);
            drsl_product_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            drsl_pu_productref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
            drsl_pu_aisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
            drsl_aisles_name_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            drsl_shops_name_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            drsl_shops_city_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            drsl_shops_street_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        }
    }
}
