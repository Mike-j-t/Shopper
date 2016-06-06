package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 30/03/2016.
 */

public class RuleAddEditPeriodSpinnerCursorAdapter extends CursorAdapter {
    public static int valuetextdataoffset;
    public RuleAddEditPeriodSpinnerCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(cursor.getPosition() == 0) {
            valuetextdataoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUETEXT);
        }
        //TextView textviewvalueid = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueid);
        //TextView textviewvaluename = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuename);
        //TextView textviewvaluetype = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuetype);
        //TextView textviewvalueint = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueint);
        //TextView textviewvaluereal = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuereal);
        TextView textviewvaluestr = (TextView) view.findViewById(R.id.ruleaddedit_ruleperiodentry);
        //TextView textviewvaluesettingsincl = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueincludeinsettings);
        //TextView textviewvaluesettingsinfo = (TextView) view.findViewById(R.id.adi_appvaluesdb_settingsinfo);

        //textviewvalueid.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_ID_INDEX));
        //textviewvaluename.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUENAME_INDEX));
        //textviewvaluetype.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUETYPE_INDDEX));
        //textviewvalueint.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUEINT_INDEX));
        //textviewvaluereal.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUEREAL_INDEX));
        textviewvaluestr.setText(cursor.getString(valuetextdataoffset));
        //textviewvaluesettingsincl.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUEINCLUDEINSETTINGS_INDEX));
        //textviewvaluesettingsinfo.setText(cursor.getString(ShopperDBHelper.VALUES_COLUMN_VALUESETTINGSINFO_INDEX));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ruleaddedit_period_spinner_entry, parent, false);
    }
}
