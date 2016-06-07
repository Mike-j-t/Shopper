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


        String periodstr;
        if(cursor.getInt(5) > 1) {
            switch(cursor.getInt(4)) {
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
            switch(cursor.getInt(4)) {
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

        String ruleastextstr = "Get <font color=\"BLACK\">" + cursor.getString(10) + " </font>" +
                "<font color=\"BLUE\">" + cursor.getString(13) + "</font>" +
                " every <font color=\"BLACK\">" + cursor.getString(5) + " </font>" +
                "<font color=\"BLUE\">" + periodstr + "</font>" +
                " from Ailse " + "<font color=\"BLUE\">" + cursor.getString(14) + "</font>" +
                " at " + "<font color=\"BLUE\">" + cursor.getString(16) + "</font>" +
                " <font color=\"#4169E1\"><i>(" + cursor.getString(17) +
                " - " + cursor.getString(18) + ")</i></font>";
        rulename.setText(cursor.getString(1));
        ruledate.setText(sdf.format(cursor.getLong(6)));
        ruleastext.setText(Html.fromHtml(ruleastextstr));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.autoaddprompt_entry,parent,false);
    }
}
