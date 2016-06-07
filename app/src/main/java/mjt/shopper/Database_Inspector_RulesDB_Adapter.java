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
 * Created by Mike092015 on 25/02/2016.
 */
public class Database_Inspector_RulesDB_Adapter extends CursorAdapter {
    public Database_Inspector_RulesDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
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
        TextView textviewrulesid = (TextView) view.findViewById(R.id.adire_rulesdb_id);
        TextView textviewrulesrulename = (TextView) view.findViewById(R.id.adire_rulesdb_rulename);
        TextView textviewrulesruletype = (TextView) view.findViewById(R.id.adire_rulesdb_ruletype);
        TextView textviewrulesrulepromtflag = (TextView) view.findViewById(R.id.adire_rulesdb_rulepromtflag);
        TextView textviewrulesruleperiod = (TextView) view.findViewById(R.id.adire_rulesdb_ruleperiod);
        TextView textviewrulesrulemultiplier = (TextView) view.findViewById(R.id.adire_rulesdb_rulemultiplier);
        TextView textviewrulesruleactiveon = (TextView) view.findViewById(R.id.adire_rulesdb_ruleactiveon);
        TextView textviewrulesruleproductref = (TextView) view.findViewById(R.id.adire_rulesdb_ruleproductref);
        TextView textviewrulesruleaisleref = (TextView) view.findViewById(R.id.adire_rulesdb_ruleaisleref);
        TextView textviewrulesruleuses = (TextView) view.findViewById(R.id.adire_rulesdb_ruleuses);
        TextView textviewrulesmincost = (TextView) view.findViewById(R.id.adire_rulesdb_mincost);
        TextView textviewrulesmaxcost = (TextView) view.findViewById(R.id.adire_rulesdb_maxcost);

        textviewrulesid.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_ID_INDEX));
        textviewrulesrulename.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_NAME_INDEX));
        textviewrulesruletype.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_TYPE_INDEX));
        textviewrulesrulepromtflag.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG_INDEX));
        textviewrulesruleperiod.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_PERIOD_INDEX));
        textviewrulesrulemultiplier.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_MULTIPLIER_INDEX));
        textviewrulesruleactiveon.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_ACTIVEON_INDEX));
        textviewrulesruleproductref.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_PRODUCTREF_INDEX));
        textviewrulesruleaisleref.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_AISLEREF_INDEX));
        textviewrulesruleuses.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_USES_INDEX));
        textviewrulesruleactiveon.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_ACTIVEON_INDEX));
        textviewrulesmincost.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_MINCOST_INDEX));
        textviewrulesmaxcost.setText(cursor.getString(ShopperDBHelper.RULES_COLUMN_MAXCOST_INDEX));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_rulesdb_entry, parent, false);
    }
}
