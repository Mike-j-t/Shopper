package mjt.shopper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 *
 */
@SuppressWarnings("FieldCanBeLocal")
class ShoppingListPromptedRulesAdapter extends CursorAdapter {
    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    private static int ruleslist_ruleid_offset = -1;
    private static int ruleslist_rulename_offset;
    private static int rulelist_ruletype_offset;
    private static int rulelist_rulepromptflag_offset;
    private static int rulelist_ruleperiod_offset;
    private static int rulelist_rulemultiplier_offset;
    private static int rulelist_ruleactiveon_offset;
    private static int rulelist_ruleproductref_offset;
    private static int rulelist_ruleaisleref_offset;
    private static int rulelist_ruleuses_offset;
    private static int rulelist_rulenumbertoget_offset;
    private static int rulelist_rulemincost_offset;
    private static int rulelist_rulemaxcost_offset;
    private static int rulelist_productname_offset;
    private static int rulelist_aislename_offset;
    private static int rulelist_aisleshopref_offset;
    private static int rulelist_shopname_offset;
    private static int rulelist_shopcity_offset;
    private static int rulelist_shopstreet_offset;
    private static int rulelist_productusagecost_offset;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat(Constants.EXTENDED__DATE_DORMAT);

    ShoppingListPromptedRulesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setRuleListOffsfets(cursor);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();
        Button addbutton = (Button) view.findViewById(R.id.autoaddprompt_addbutton);
        Button skipbutton = (Button) view.findViewById(R.id.autoaddprompt_skipbutton);
        addbutton.setTag(position);
        skipbutton.setTag(position);

        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        String periodstr;
        if(cursor.getInt(rulelist_rulemultiplier_offset) > 1) {
            switch(cursor.getInt(rulelist_ruleperiod_offset)) {
                case Constants.PERIOD_DAYSASINT:
                    periodstr = Constants.PERIOD_DAYS;
                    break;
                case Constants.PERIOD_WEEKSASINT:
                    periodstr = Constants.PERIOD_WEEKS;
                    break;
                case Constants.PERIOD_FORTNIGHTSASINT:
                    periodstr = Constants.PERIOD_FORTNIGHTS;
                    break;
                case Constants.PERIOD_MONTHSASINT:
                    periodstr = Constants.PERIOD_MONTHS;
                    break;
                case Constants.PERIOD_QUARTERSASINT:
                    periodstr = Constants.PERIOD_QUARTERS;
                    break;
                case Constants.PERIOD_YEARSASINT:
                    periodstr = Constants.PERIOD_YEARS;
                    break;
                default:
                    periodstr = "UNKNOWN!!!";
            }
        } else {
            switch(cursor.getInt(rulelist_ruleperiod_offset)) {
                case Constants.PERIOD_DAYSASINT:
                    periodstr = Constants.PERIOD_DAYS_SINGULAR;
                    break;
                case Constants.PERIOD_WEEKSASINT:
                    periodstr = Constants.PERIOD_WEEKS_SINGULAR;
                    break;
                case Constants.PERIOD_FORTNIGHTSASINT:
                    periodstr = Constants.PERIOD_FORTNIGHTS_SINGULAR;
                    break;
                case Constants.PERIOD_MONTHSASINT:
                    periodstr = Constants.PERIOD_MONTHS_SINGULAR;
                    break;
                case Constants.PERIOD_QUARTERSASINT:
                    periodstr = Constants.PERIOD_QUARTERS_SINGULAR;
                    break;
                case Constants.PERIOD_YEARSASINT:
                    periodstr = Constants.PERIOD_YEARS_SINGULAR;
                    break;
                default:
                    periodstr = "UNKNOWN!!!";
            }

        }

        TextView rulename = (TextView) view.findViewById(R.id.autoaddprompt_rulename);
        TextView ruledate = (TextView) view.findViewById(R.id.autoaddprompt_ruledate);
        TextView ruleastext = (TextView) view.findViewById(R.id.autoprompt_ruleastext);

        String ruleastextstr = "Get <font color=\"BLACK\">" +
                cursor.getString(rulelist_rulenumbertoget_offset) + " </font>" +
                "<font color=\"BLUE\">" +
                cursor.getString(rulelist_productname_offset) + "</font>" +
                " every <font color=\"BLACK\">" + cursor.getString(rulelist_rulemultiplier_offset) + " </font>" +
                "<font color=\"BLUE\">" + periodstr + "</font>" +
                " from Ailse " + "<font color=\"BLUE\">" + cursor.getString(rulelist_aislename_offset) + "</font>" +
                " at " + "<font color=\"BLUE\">" + cursor.getString(rulelist_shopname_offset) + "</font>" +
                " <font color=\"#4169E1\"><i>(" + cursor.getString(rulelist_shopcity_offset) +
                " - " + cursor.getString(rulelist_shopstreet_offset) + ")</i></font>";
        rulename.setText(cursor.getString(ruleslist_rulename_offset));
        ruledate.setText(sdf.format(cursor.getLong(rulelist_ruleactiveon_offset)));
        ruleastext.setText(Html.fromHtml(ruleastextstr));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.autoaddprompt_entry,parent,false);
    }

    private void setRuleListOffsfets(Cursor cursor) {
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
