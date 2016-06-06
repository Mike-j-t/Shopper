package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Mike092015 on 27/04/2016.
 */
public class RuleListAdapter extends CursorAdapter {

    public SimpleDateFormat sdf = new SimpleDateFormat(Constants.EXTENDED__DATE_DORMAT);
    public static int ruleidoffset;
    public static int rulenameoffset;
    public static int ruletyoeoffset;
    public static int rulepromptflagoffset;
    public static int ruleperiodoffset;
    public static int rulemultiplieroffset;
    public static int ruleactiveonoffset;
    public static int ruleproductrefoffset;
    public static int ruleaislerefoffset;
    public static int ruleusesoffset;
    public static int rulenumbertogetoffset;
    public static int rulemincostoffset;
    public static int rulemaxcostoffset;
    public static int productnameoffset;
    public static int aislenameoffset;
    public static int aisleshoprefoffset;
    public static int storenameoffset;
    public static int storecityoffset;
    public static int storestreetoffset;
    public static int productusagecostoffset;

    public RuleListAdapter(Context context, Cursor cursor, int flags) {
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
        int pos = cursor.getPosition();
        if(pos == 0) {
            ruleidoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ID);
            rulenameoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NAME);
            ruletyoeoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_TYPE);
            rulepromptflagoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG);
            ruleperiodoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PERIOD);
            rulemultiplieroffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MULTIPLIER);
            ruleactiveonoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ACTIVEON);
            ruleproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PRODUCTREF);
            ruleaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_AISLEREF);
            ruleusesoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_USES);
            rulenumbertogetoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NUMBERTOGET);
            rulemincostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MINCOST);
            rulemaxcostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MAXCOST);
            productnameoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            aislenameoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            aisleshoprefoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
            storenameoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            storecityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            storestreetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        }
        TextView rulename = (TextView) view.findViewById(R.id.rulelistentry_rulename);
        TextView ruledate = (TextView) view.findViewById(R.id.rulelistentry_ruledate);
        CheckedTextView ruleprompt = (CheckedTextView) view.findViewById(R.id.rulelistentry_ruleprompttoadd);
        TextView ruleperiod = (TextView) view.findViewById(R.id.rulelistentry_ruleperiod);

        rulename.setText(cursor.getString(rulenameoffset));
        ruledate.setText(sdf.format(cursor.getLong(ruleactiveonoffset)));
        String freq = "Get <b><font color=\"BLACK\">" + cursor.getInt(rulenumbertogetoffset) + "</font></b> <b><font color=\"BLUE\">" + cursor.getString(productnameoffset) + "</font></b> every ";
        String periodasstr = "";
        String loc = " from Aisle <b><font color=\"BLUE\">" + cursor.getString(aislenameoffset) + "</font></b> at <b><font color=\"BLUE\">" + cursor.getString(storenameoffset) +
                "</font></b> <font color=\"#4169E1\"><i>(" + cursor.getString(storestreetoffset) + " - " + cursor.getString(storecityoffset) + ")</i></font>";
        int promptstate = cursor.getInt(rulepromptflagoffset);
        if (promptstate < 1) {
            ruleprompt.setChecked(true);
        } else {
            ruleprompt.setChecked(false);
        }
        int period = cursor.getInt(ruleperiodoffset);
        if (cursor.getInt(rulemultiplieroffset) > 1) {
            freq = freq + "<b><font color=\"BLACK\">" + cursor.getInt(rulemultiplieroffset) + "</font></b> ";
            switch (cursor.getInt(ruleperiodoffset)) {
                case Constants.PERIOD_DAYSASINT:
                    periodasstr = Constants.PERIOD_DAYS;
                    break;
                case Constants.PERIOD_WEEKSASINT:
                    periodasstr = Constants.PERIOD_WEEKS;
                    break;
                case Constants.PERIOD_FORTNIGHTSASINT:
                    periodasstr = Constants.PERIOD_FORTNIGHTS;
                    break;
                case Constants.PERIOD_MONTHSASINT:
                    periodasstr = Constants.PERIOD_MONTHS;
                    break;
                case Constants.PERIOD_QUARTERSASINT:
                    periodasstr = Constants.PERIOD_QUARTERS;
                    break;
                case Constants.PERIOD_YEARSASINT:
                    periodasstr = Constants.PERIOD_YEARS;
                    break;
                default:
                    periodasstr = "UNKNOWN!!!";
            }
        } else {
            switch (cursor.getInt(ruleperiodoffset)) {
                case Constants.PERIOD_DAYSASINT:
                    periodasstr = Constants.PERIOD_DAYS_SINGULAR;
                    break;
                case Constants.PERIOD_WEEKSASINT:
                    periodasstr = Constants.PERIOD_WEEKS_SINGULAR;
                    break;
                case Constants.PERIOD_FORTNIGHTSASINT:
                    periodasstr = Constants.PERIOD_FORTNIGHTS_SINGULAR;
                    break;
                case Constants.PERIOD_MONTHSASINT:
                    periodasstr = Constants.PERIOD_MONTHS_SINGULAR;
                    break;
                case Constants.PERIOD_QUARTERSASINT:
                    periodasstr = Constants.PERIOD_QUARTERS_SINGULAR;
                    break;
                case Constants.PERIOD_YEARSASINT:
                    periodasstr = Constants.PERIOD_YEARS_SINGULAR;
                    break;
                default:
                    periodasstr = "UNKNOWN!!!";
            }

        }
        ruleperiod.setText(Html.fromHtml(freq + "<b><font color=\"BLUE\">" + periodasstr + "</font></b>" + loc));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.rulelistentry, parent,false);
    }
}