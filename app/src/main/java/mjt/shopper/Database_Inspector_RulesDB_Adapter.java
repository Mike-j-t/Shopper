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
    public static int ruleidoffset;
    public static int rulenameoffset;
    public static int ruletypeoffset;
    public static int rulepromptflagoffset;
    public static int ruleperiodoffset;
    public static int rulemultiplieroffset;
    public static int ruleactiveonoffset;
    public static int ruleproductrefoffset;
    public static int ruleaislerefoffset;
    public static int ruleusesoffset;
    public static int rulenumbertogetoffset;
    public static int rulemincostoffset;
    public static int rulemaxcostoffset;

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
        if(cursor.getPosition() == 0) {
            ruleidoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ID);
            rulenameoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NAME);
            ruletypeoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_TYPE);
            rulepromptflagoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG);
            ruleperiodoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PERIOD);
            rulemultiplieroffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MULTIPLIER);
            ruleactiveonoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ACTIVEON);
            ruleproductrefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PRODUCTREF);
            ruleaislerefoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_AISLEREF);
            ruleusesoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_USES);
            rulenumbertogetoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NUMBERTOGET);
            rulemincostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MINCOST);
            rulemaxcostoffset = cursor.getColumnIndex(ShopperDBHelper.RULES_COLUMN_MAXCOST);
        }
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

        textviewrulesid.setText(cursor.getString(ruleidoffset));
        textviewrulesrulename.setText(cursor.getString(rulenameoffset));
        textviewrulesruletype.setText(cursor.getString(ruletypeoffset));
        textviewrulesrulepromtflag.setText(cursor.getString(rulepromptflagoffset));
        textviewrulesruleperiod.setText(cursor.getString(ruleperiodoffset));
        textviewrulesrulemultiplier.setText(cursor.getString(rulemultiplieroffset));
        textviewrulesruleactiveon.setText(cursor.getString(ruleactiveonoffset));
        textviewrulesruleproductref.setText(cursor.getString(ruleproductrefoffset));
        textviewrulesruleaisleref.setText(cursor.getString(ruleaislerefoffset));
        textviewrulesruleuses.setText(cursor.getString(ruleusesoffset));
        textviewrulesmincost.setText(cursor.getString(rulemincostoffset));
        textviewrulesmaxcost.setText(cursor.getString(rulemaxcostoffset));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_rulesdb_entry, parent, false);
    }
}
