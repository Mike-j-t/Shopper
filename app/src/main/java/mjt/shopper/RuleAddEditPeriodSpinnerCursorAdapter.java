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
@SuppressWarnings("FieldCanBeLocal")
class RuleAddEditPeriodSpinnerCursorAdapter extends CursorAdapter {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    private static int values_valueid_offset = -1;
    private static int values_valuename_offset;
    private static int values_valuetype_offset;
    private static int values_valueint_offset;
    private static int values_valuereal_offset;
    private static int values_valuetext_offset;
    private static int values_valueincludeinsettings_offset;
    private static int values_valuesettingsinfro_offset;

    RuleAddEditPeriodSpinnerCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setValuesOffsets(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.ruleaddedit_period_spinner_entry,
                parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textviewvaluestr = (TextView) view.findViewById(R.id.ruleaddedit_ruleperiodentry);
        textviewvaluestr.setText(cursor.getString(values_valuetext_offset));
    }

    @Override
    public View getDropDownView(int position, View convertview, ViewGroup parent) {
        View v = convertview;
        if(v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.ruleaddedit_period_spinner_entry,
                    parent,
                    false
            );
        }
        Context context = v.getContext();
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        TextView textviewvaluestr = (TextView) v.findViewById(R.id.ruleaddedit_ruleperiodentry);
        textviewvaluestr.setText(cursor.getString(values_valuetext_offset));

        if(position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewroweven));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewrowodd));
        }
        return v;
    }

    private void setValuesOffsets(Cursor cursor) {
        if(values_valueid_offset != -1) {
            return;
        }
        values_valueid_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_ID);
        values_valuename_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUENAME);
        values_valuetype_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUETYPE);
        values_valueint_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEINT);
        values_valuereal_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEREAL);
        values_valuetext_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUETEXT);
        values_valueincludeinsettings_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEINCLUDEINSETTINGS);
        values_valuesettingsinfro_offset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUESETTINGSINFO);
    }
}
