package mjt.shopper;

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
 * Created by Mike092015 on 29/04/2016.
 */
public class ShoppingListPromptedRulesAdapter extends CursorAdapter {
    public static int ruleidoffset;
    public static int rulenameoffset;
    public static int ruletypeoffset;
    public static int rulepromptflagoffset;
    public static int ruleperiodoffset;
    public static int rulemultipleroffset;
    public static int ruleactiveonoffset;
    public static int ruleproductrefoffset;
    public static int ruleaislerefoffset;
    public static int ruleusesoffset;
    public static int rulenumbertogetoffset;
    public static int rulemincostoffset;
    public static int rulemaxcostoffset;
    public static int productsnameoffset;
    public static int aislenameoffset;
    public static int aisleshopoffset;
    public static int shopnameoffset;
    public static int shopcityoffset;
    public static int shopstreetoffset;
    public static int productusagecostoffset;

    public SimpleDateFormat sdf = new SimpleDateFormat(Constants.EXTENDED__DATE_DORMAT);

    ShoppingListPromptedRulesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();
        Button addbutton = (Button) view.findViewById(R.id.autoaddprompt_addbutton);
        Button skipbutton = (Button) view.findViewById(R.id.autoaddprompt_skipbutton);
        addbutton.setTag(Integer.valueOf(position));
        skipbutton.setTag(Integer.valueOf(position));

        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getPosition() == 0) {
            ruleidoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ID);
            rulenameoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NAME);
            ruletypeoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_TYPE);
            rulepromptflagoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG);
            ruleperiodoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PERIOD);
            rulemultipleroffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MULTIPLIER);
            ruleactiveonoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ACTIVEON);
            ruleproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PRODUCTREF);
            ruleaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_AISLEREF);
            ruleusesoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_USES);
            rulenumbertogetoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NUMBERTOGET);
            rulemincostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MINCOST);
            rulemaxcostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MAXCOST);
            productsnameoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
            aislenameoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
            aisleshopoffset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
            shopnameoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
            shopcityoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
            shopstreetoffset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
            productusagecostoffset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        }


        String periodstr;
        if(cursor.getInt(rulemultipleroffset) > 1) {
            switch(cursor.getInt(ruleperiodoffset)) {
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
            switch(cursor.getInt(ruleperiodoffset)) {
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

        String ruleastextstr = "Get <font color=\"BLACK\">" + cursor.getString(rulenumbertogetoffset) + " </font>" +
                "<font color=\"BLUE\">" + cursor.getString(productsnameoffset) + "</font>" +
                " every <font color=\"BLACK\">" + cursor.getString(rulemultipleroffset) + " </font>" +
                "<font color=\"BLUE\">" + periodstr + "</font>" +
                " from Ailse " + "<font color=\"BLUE\">" + cursor.getString(aislenameoffset) + "</font>" +
                " at " + "<font color=\"BLUE\">" + cursor.getString(shopnameoffset) + "</font>" +
                " <font color=\"#4169E1\"><i>(" + cursor.getString(shopcityoffset) +
                " - " + cursor.getString(shopstreetoffset) + ")</i></font>";
        rulename.setText(cursor.getString(rulenameoffset));
        ruledate.setText(sdf.format(cursor.getLong(ruleactiveonoffset)));
        ruleastext.setText(Html.fromHtml(ruleastextstr));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.autoaddprompt_entry,parent,false);
    }
}
