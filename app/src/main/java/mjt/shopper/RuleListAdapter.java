package mjt.shopper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
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

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int ruleslist_ruleid_offset = -1;
    public static int ruleslist_rulename_offset;
    public static int rulelist_ruletype_offset;
    public static int rulelist_rulepromptflag_offset;
    public static int rulelist_ruleperiod_offset;
    public static int rulelist_rulemultiplier_offset;
    public static int rulelist_ruleactiveon_offset;
    public static int rulelist_ruleproductref_offset;
    public static int rulelist_ruleaisleref_offset;
    public static int rulelist_ruleuses_offset;
    public static int rulelist_rulenumbertoget_offset;
    public static int rulelist_rulemincost_offset;
    public static int rulelist_rulemaxcost_offset;
    public static int rulelist_productname_offset;
    public static int rulelist_aislename_offset;
    public static int rulelist_aisleshopref_offset;
    public static int rulelist_shopname_offset;
    public static int rulelist_shopcity_offset;
    public static int rulelist_shopstreet_offset;
    public static int rulelist_productusagecost_offset;

    @SuppressLint("SimpleDateFormat")
    public SimpleDateFormat sdf = new SimpleDateFormat(Constants.EXTENDED__DATE_DORMAT);

    public RuleListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        setRuleListOffsfets(cursor);
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
        TextView rulename = (TextView) view.findViewById(R.id.rulelistentry_rulename);
        TextView ruledate = (TextView) view.findViewById(R.id.rulelistentry_ruledate);
        CheckedTextView ruleprompt = (CheckedTextView) view.findViewById(R.id.rulelistentry_ruleprompttoadd);
        TextView ruleperiod = (TextView) view.findViewById(R.id.rulelistentry_ruleperiod);

        rulename.setText(cursor.getString(ruleslist_rulename_offset));
        ruledate.setText(sdf.format(cursor.getLong(rulelist_ruleactiveon_offset)));
        String freq = "Get <b><font color=\"BLACK\">" +
                cursor.getInt(rulelist_rulenumbertoget_offset) +
                "</font></b> <b><font color=\"BLUE\">" +
                cursor.getString(rulelist_productname_offset) +
                "</font></b> every ";
        String periodasstr = "";
        String loc = " from Aisle <b><font color=\"BLUE\">" +
                cursor.getString(rulelist_aislename_offset) +
                "</font></b> at <b><font color=\"BLUE\">" +
                cursor.getString(rulelist_shopname_offset) +
                "</font></b> <font color=\"#4169E1\"><i>(" +
                cursor.getString(rulelist_shopcity_offset) + " - " +
                cursor.getString(rulelist_shopstreet_offset) +
                ")</i></font>";
        int promptstate = cursor.getInt(rulelist_rulepromptflag_offset);
        if (promptstate < 1) {
            ruleprompt.setChecked(true);
        } else {
            ruleprompt.setChecked(false);
        }
        int period = cursor.getInt(rulelist_ruleperiod_offset);
        if (cursor.getInt(rulelist_rulemultiplier_offset) > 1) {
            freq = freq + "<b><font color=\"BLACK\">" + cursor.getInt(rulelist_rulemultiplier_offset) + "</font></b> ";
            switch (cursor.getInt(rulelist_ruleperiod_offset)) {
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
            switch (cursor.getInt(rulelist_ruleperiod_offset)) {
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
        ruleperiod.setText(Html.fromHtml(freq +
                "<b><font color=\"BLUE\">" +
                periodasstr + "</font></b>" + loc));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.rulelistentry, parent,false);
    }

    public void setRuleListOffsfets(Cursor cursor) {
        if(ruleslist_ruleid_offset != -1) {
            return;
        }
        ruleslist_ruleid_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ID);
        ruleslist_rulename_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NAME);
        rulelist_ruletype_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_TYPE);
        rulelist_rulepromptflag_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG);
        rulelist_ruleperiod_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PERIOD);
        rulelist_rulemultiplier_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MULTIPLIER);
        rulelist_ruleactiveon_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ACTIVEON);
        rulelist_ruleproductref_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PRODUCTREF);
        rulelist_ruleaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_AISLEREF);
        rulelist_ruleuses_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_USES);
        rulelist_rulenumbertoget_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NUMBERTOGET);
        rulelist_rulemincost_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MINCOST);
        rulelist_rulemaxcost_offset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MAXCOST);
        rulelist_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        rulelist_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        rulelist_aisleshopref_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        rulelist_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        rulelist_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        rulelist_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        rulelist_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
    }
}