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

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int ruleslist_ruleid_offset = -1;
    public static int ruleslist_rulename_offset;
    public static int rulelist_ruletype_offset;
    public static int rulelist_rulepromptflag_offset;
    public static int rulelist_ruleperiod_offset;
    public static int rulelist_rulemultiplier_offset;
    public static int rulelist_ruleactiveon_offset;
    public static int rulelist_ruleproductref_offset;
    public static int rulelist_ruleaisleref_offset;
    public static int rulelist_ruleuses_offset;
    public static int rulelist_rulenumbertoget_offset;
    public static int rulelist_rulemincost_offset;
    public static int rulelist_rulemaxcost_offset;
    public static int rulelist_productname_offset;
    public static int rulelist_aislename_offset;
    public static int rulelist_aisleshopref_offset;
    public static int rulelist_shopname_offset;
    public static int rulelist_shopcity_offset;
    public static int rulelist_shopstreet_offset;
    public static int rulelist_productusagecost_offset;

    public Database_Inspector_RulesDB_Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        setRuleListOffsfets(cursor);
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

        textviewrulesid.setText(cursor.getString(ruleslist_ruleid_offset));
        textviewrulesrulename.setText(cursor.getString(ruleslist_rulename_offset));
        textviewrulesruletype.setText(cursor.getString(rulelist_ruletype_offset));
        textviewrulesrulepromtflag.setText(cursor.getString(rulelist_rulepromptflag_offset));
        textviewrulesruleperiod.setText(cursor.getString(rulelist_ruleperiod_offset));
        textviewrulesrulemultiplier.setText(cursor.getString(rulelist_rulemultiplier_offset));
        textviewrulesruleactiveon.setText(cursor.getString(rulelist_ruleactiveon_offset));
        textviewrulesruleproductref.setText(cursor.getString(rulelist_ruleproductref_offset));
        textviewrulesruleaisleref.setText(cursor.getString(rulelist_ruleaisleref_offset));
        textviewrulesruleuses.setText(cursor.getString(rulelist_ruleuses_offset));
        textviewrulesmincost.setText(cursor.getString(rulelist_rulemincost_offset));
        textviewrulesmaxcost.setText(cursor.getString(rulelist_rulemaxcost_offset));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_database_inspect_rulesdb_entry, parent, false);
    }

    public void setRuleListOffsfets(Cursor cursor) {
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
    }
}
