package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Mike092015 on 30/03/2016.
 */
public class Database_Inspector_ValuesDB_Adapter extends CursorAdapter {
    public static int valuesidoffset;
    public static int valuesnameoffset;
    public static int valuestypeoffset;
    public static int valuesintoffset;
    public static int valuesrealoffset;
    public static int valuestextoffset;
    public static int valuesincludeinsettingsoffset;
    public static int valuessettingsinfooffset;

    public Database_Inspector_ValuesDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
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
        if (cursor.getPosition() == 0 ) {
            valuesidoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_ID);
            valuesnameoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUENAME);
            valuestypeoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUETYPE);
            valuesintoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEINT);
            valuesrealoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEREAL);
            valuestextoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUETEXT);
            valuesincludeinsettingsoffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUEINCLUDEINSETTINGS);
            valuessettingsinfooffset = cursor.getColumnIndex(ShopperDBHelper.VALUES_COLUMN_VALUESETTINGSINFO);
        }
        TextView textviewvalueid = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueid);
        TextView textviewvaluename = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuename);
        TextView textviewvaluetype = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuetype);
        TextView textviewvalueint = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueint);
        TextView textviewvaluereal = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuereal);
        TextView textviewvaluestr = (TextView) view.findViewById(R.id.adi_appvaluesdb_valuestr);
        TextView textviewvaluesettingsincl = (TextView) view.findViewById(R.id.adi_appvaluesdb_valueincludeinsettings);
        TextView textviewvaluesettingsinfo = (TextView) view.findViewById(R.id.adi_appvaluesdb_settingsinfo);

        textviewvalueid.setText(cursor.getString(valuesidoffset));
        textviewvaluename.setText(cursor.getString(valuesnameoffset));
        textviewvaluetype.setText(cursor.getString(valuestypeoffset));
        textviewvalueint.setText(cursor.getString(valuesintoffset));
        textviewvaluereal.setText(cursor.getString(valuesrealoffset));
        textviewvaluestr.setText(cursor.getString(valuestextoffset));
        textviewvaluesettingsincl.setText(cursor.getString(valuesincludeinsettingsoffset));
        textviewvaluesettingsinfo.setText(cursor.getString(valuessettingsinfooffset));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_appvaluesdb_entry, parent, false);
    }
}