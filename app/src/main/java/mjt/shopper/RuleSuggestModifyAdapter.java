package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Mike092015 on 7/11/2016.
 */

public class RuleSuggestModifyAdapter extends CursorAdapter {

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
    private static int rsl_rules_id_offset;
    private static int rsl_rules_multiplier_offset;
    private static int rsl_rules_period_offset;
    private static int rsl_rules_numbertoget_offset;

    private TextView modifybutton;
    private ProgressBar progresslow;
    private ProgressBar progresshigh;

    private DecimalFormat df = new DecimalFormat("#.0#");

    RuleSuggestModifyAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setRuleSuggestModifyOffets(cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        double actlfreq = cursor.getDouble(rsl_calc_frequency_offset);

        TextView productname = (TextView) view.findViewById(R.id.rsmle_product_name_entry);
        TextView shopname = (TextView) view.findViewById(R.id.rsmle_shop_name_entry);
        TextView actualfreq = (TextView) view.findViewById(R.id.rsmle_actual_frequency);
        TextView currentfreq = (TextView) view.findViewById(R.id.rsmle_current_frequency);
        TextView aislename = (TextView) view.findViewById(R.id.rsmle_aisle_name_entry);
        TextView accuracy = (TextView) view.findViewById(R.id.rsmle_accuracy);
        progresslow = (ProgressBar) view.findViewById(R.id.progressBarlow);
        progresshigh = (ProgressBar) view.findViewById(R.id.progressBarhigh);

        double itemfreq = 0;
        int period = cursor.getInt(rsl_rules_period_offset);
        double periodasmult = 0;
        switch (period) {
            case Constants.PERIOD_DAYSASINT:
                periodasmult = 1;
                break;
            case Constants.PERIOD_WEEKSASINT:
                periodasmult = 7;
                break;
            case Constants.PERIOD_FORTNIGHTSASINT:
                periodasmult = 14;
                break;
            case Constants.PERIOD_MONTHSASINT:
                periodasmult = 365.25 / 12;
                break;
            case Constants.PERIOD_QUARTERSASINT:
                periodasmult = (365.25 / 12) * 4;
                break;
            case Constants.PERIOD_YEARSASINT:
                periodasmult = 365.25;
                break;
        }
        double rulemultiplier = cursor.getDouble(rsl_rules_multiplier_offset);
        double numbertoget = cursor.getDouble(rsl_rules_numbertoget_offset);
        double rulefreq = (periodasmult * rulemultiplier) / numbertoget;
        double ruleaccuracy = ((actlfreq / rulefreq) * 100);

        currentfreq.setText((String)df.format(rulefreq));
        actualfreq.setText(df.format(cursor.getDouble(rsl_calc_frequency_offset)));
        progresslow.setProgress(100);
        progresshigh.setProgress(0);
        if(ruleaccuracy < 100) {
            progresslow.setProgress((int) ruleaccuracy);
            progresshigh.setProgress(0);
        }
        if (ruleaccuracy > 100)
        {
            progresshigh.setProgress((int)((ruleaccuracy - 100)));
            progresslow.setProgress(100);
        }

        //accuracy.setText(Double.toString((cursor.getDouble(rsl_calc_frequency_offset) / rulefreq) * 100 ));
        accuracy.setText(Integer.toString((int)ruleaccuracy)+"%");
        productname.setText(cursor.getString(rsl_product_productname_offset));
        shopname.setText(cursor.getString(rsl_shops_shopname_offset) +
                " - " +
                cursor.getString(rsl_shops_shopcity_offset)
        );
        aislename.setText(cursor.getString(rsl_aisles_aislename_offset));
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();

        view.findViewById(R.id.rsmle_modifyrulebutton_entry).setTag(position);

        if(position % 2 ==0) {
            view.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.colorlistviewroweven
            ));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(
                    context,
                    R.color.colorlistviewrowodd
            ));
        }

        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.activity_rulesuggestmodify_entry,
                        parent,
                        false);
    }

    private void setRuleSuggestModifyOffets(Cursor cursor) {

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
        rsl_rules_id_offset = cursor.getColumnIndex(
                ShopperDBHelper.RULES_TABLE_NAME +
                        ShopperDBHelper.RULES_COLUMN_ID
        );
        rsl_rules_multiplier_offset = cursor.getColumnIndex(
                ShopperDBHelper.RULES_COLUMN_MULTIPLIER
        );
        rsl_rules_numbertoget_offset = cursor.getColumnIndex(
                ShopperDBHelper.RULES_COLUMN_NUMBERTOGET
        );
        rsl_rules_period_offset = cursor.getColumnIndex(
                ShopperDBHelper.RULES_COLUMN_PERIOD
        );

    }
}
