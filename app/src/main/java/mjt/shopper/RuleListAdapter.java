package mjt.shopper;

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

    public SimpleDateFormat sdf = new SimpleDateFormat(Constants.EXTENDED__DATE_DORMAT);

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
        TextView rulename = (TextView) view.findViewById(R.id.rulelistentry_rulename);
        TextView ruledate = (TextView) view.findViewById(R.id.rulelistentry_ruledate);
        CheckedTextView ruleprompt = (CheckedTextView) view.findViewById(R.id.rulelistentry_ruleprompttoadd);
        TextView ruleperiod = (TextView) view.findViewById(R.id.rulelistentry_ruleperiod);

        rulename.setText(cursor.getString(1));
        ruledate.setText(sdf.format(cursor.getLong(6)));
        String freq = "Get <b><font color=\"BLACK\">" + cursor.getInt(10) + "</font></b> <b><font color=\"BLUE\">" + cursor.getString(13) + "</font></b> every ";
        String periodasstr = "";
        String loc = " from Aisle <b><font color=\"BLUE\">" + cursor.getString(14) + "</font></b> at <b><font color=\"BLUE\">" + cursor.getString(16) +
                "</font></b> <font color=\"#4169E1\"><i>(" + cursor.getString(17) + " - " + cursor.getString(18) + ")</i></font>";
        int promptstate = cursor.getInt(3);
        if (promptstate < 1) {
            ruleprompt.setChecked(true);
        } else {
            ruleprompt.setChecked(false);
        }
        int period = cursor.getInt(4);
        if (cursor.getInt(5) > 1) {
            freq = freq + "<b><font color=\"BLACK\">" + cursor.getInt(5) + "</font></b> ";
            switch (cursor.getInt(4)) {
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
            switch (cursor.getInt(4)) {
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