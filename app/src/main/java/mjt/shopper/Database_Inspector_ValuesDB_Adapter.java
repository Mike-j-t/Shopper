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
 * Created by Mike092015 on 30/03/2016.
 */
public class Database_Inspector_ValuesDB_Adapter extends CursorAdapter {

    public static int values_valueid_offset = -1;
    public static int values_valuename_offset;
    public static int values_valuetype_offset;
    public static int values_valueint_offset;
    public static int values_valuereal_offset;
    public static int values_valuetext_offset;
    public static int values_valueincludeinsettings_offset;
    public static int values_valuesettingsinfro_offset;

    public Database_Inspector_ValuesDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setValuesOffsets(cursor);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view = super.getView(position, convertview, parent);
        Context context = view.getContext();
        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
        }
        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textviewvalueid = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueid);
        TextView textviewvaluename = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuename);
        TextView textviewvaluetype = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuetype);
        TextView textviewvalueint = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueint);
        TextView textviewvaluereal = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuereal);
        TextView textviewvaluestr = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuestr);
        TextView textviewvaluesettingsincl = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueincludeinsettings);
        TextView textviewvaluesettingsinfo = (TextView) view.findViewById(R.id.adi_appvaluesdb_settingsinfo);

        textviewvalueid.setText(cursor.getString(values_valueid_offset));
        textviewvaluename.setText(cursor.getString(values_valuename_offset));
        textviewvaluetype.setText(cursor.getString(values_valuetype_offset));
        textviewvalueint.setText(cursor.getString(values_valueint_offset));
        textviewvaluereal.setText(cursor.getString(values_valuereal_offset));
        textviewvaluestr.setText(cursor.getString(values_valuetext_offset));
        textviewvaluesettingsincl.setText(cursor.getString(values_valueincludeinsettings_offset));
        textviewvaluesettingsinfo.setText(cursor.getString(values_valuesettingsinfro_offset));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_appvaluesdb_entry, parent, false);
    }

    public void setValuesOffsets(Cursor cursor) {
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