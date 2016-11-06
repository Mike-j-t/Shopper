package mjt.shopper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 *
 */
public class RuleAddEditList extends AppCompatActivity {

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    private static int purchasableproducts_productusageaisleref_offset = -1; //**
    private static int purchasableproducts_productusageproductref_offset;
    private static int purchasableproducts_productusagecost_offset;
    private static int purchasableproducts_productid_offset; //**
    private static int purchasableproducts_productname_offset;
    private static int purchasableproducts_aisleid_offset; //**
    private static int purchasableproducts_aislename_offset;
    private static int purchasableproducts_shopname_offset;
    private static int purchasableproducts_shopcity_offset;
    private static int purchasableproducts_shopstreet_offset;

    private static int ruleslist_ruleid_offset = -1;
    private static int ruleslist_rulename_offset;
    private static int rulelist_ruletype_offset;
    private static int rulelist_rulepromptflag_offset;
    private static int rulelist_ruleperiod_offset;
    private static int rulelist_rulemultiplier_offset;
    private static int rulelist_ruleactiveon_offset;
    private static int rulelist_ruleproductref_offset;
    private static int rulelist_ruleaisleref_offset;
    private static int rulelist_ruleuses_offset;
    private static int rulelist_rulenumbertoget_offset;
    private static int rulelist_rulemincost_offset;
    private static int rulelist_rulemaxcost_offset;
    private static int rulelist_productname_offset;
    private static int rulelist_aislename_offset;
    private static int rulelist_aisleshopref_offset;
    private static int rulelist_shopname_offset;
    private static int rulelist_shopcity_offset;
    private static int rulelist_shopstreet_offset;
    private static int rulelist_productusagecost_offset;

    private final static int RESUMESTATE_NOTHING = 0;
    private final static int RESUMESTATE_ADD = 1;
    private final static int RESUMESTATE_UPDATE = 2;
    public final static int RESUMESTATE_DELETE = 3;
    private int resume_state = RESUMESTATE_NOTHING;
    private boolean devmode;
    private boolean helpoffmode;
    private SharedPreferences sp;
    private final static String THIS_ACTIVITY = "RuleAddEditList";

    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);

    private LinearLayout help_layout;
    private ListView potentialruleslist;
    private Cursor potentialrulescursor;
    private PurchaseableProductsAdapter potentialruleslistadapter;
    private String potentialrulesproductsearcharg = "";
    private String potentialrulesstoresearcharg = "";
    private EditText potentialrulesproductinput;
    private EditText potentialrulesstoreinput;
    private ListView currentruleslist;
    private Cursor currentrulescursor;
    private RuleListAdapter currentruleslistadapter;
    public long currentproductid = -1;
    public String currentproductname = "";
    private long currentaisleid = -1;
    private String currentaislename = "";
    public long currentstoreid = -1;
    private String currentstorename = "";
    private String potentialruleslistsortorder = Constants.PURCHASABLEPRODUCTSLISTORDER_BY_PRODUCT;
    private String rulelistsortorder = Constants.RULELISTORDER_BY_RULE;


    protected void  onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "RuleAdddEditList (RULES button) Invoked", THIS_ACTIVITY, "RuleAddEditList", true);
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode), false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode), false);
        setContentView(R.layout.ruleaddeditlist);

        // Turn Help Display on or off according to user preference
        help_layout = (LinearLayout) findViewById(R.id.ruleaddeditlist_help_layout);
        if (!helpoffmode) {
            help_layout.setVisibility(View.VISIBLE);
        } else {
            help_layout.setVisibility(View.GONE);
        }

        potentialruleslist = (ListView) findViewById(R.id.ruleaddeditlist_potentialruleslist);
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesproductsearcharg,
                potentialrulesstoresearcharg,
                potentialruleslistsortorder);
        setPurchasableProductsOffsets(potentialrulescursor);
        potentialruleslistadapter = new PurchaseableProductsAdapter(this,
                potentialrulescursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        potentialruleslist.setAdapter(potentialruleslistadapter);
        potentialrulesstoreinput = (EditText) findViewById(R.id.ruleaddeditlist_storeselection);
        potentialrulesproductinput = (EditText) findViewById(R.id.ruleaddeditlist_productselection);

        // Handle Product Input. Whenever input changes refresh potential rules list
        potentialrulesproductinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                potentialrulesproductsearcharg = potentialrulesproductinput.getText().toString();
                potentialrulesstoresearcharg = potentialrulesstoreinput.getText().toString();
                potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesproductsearcharg,
                        potentialrulesstoresearcharg,potentialruleslistsortorder);
                potentialruleslistadapter.swapCursor(potentialrulescursor);
            }
        });

        //Handle Store Input. Whenever input changes refresh the potential rules list
        potentialrulesstoreinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                potentialrulesproductsearcharg = potentialrulesproductinput.getText().toString();
                potentialrulesstoresearcharg = potentialrulesstoreinput.getText().toString();
                potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesproductsearcharg,
                        potentialrulesstoresearcharg,potentialruleslistsortorder);
                potentialruleslistadapter.swapCursor(potentialrulescursor);
            }
        });

        //Set Listener to enable a rule to be added by clicking on an item
        potentialruleslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                resume_state = RESUMESTATE_ADD;
                Toast.makeText(context, "You clicked on a product to create a rule.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, RuleAddEdit.class);
                intent.putExtra(getResources().getString(R.string.intentkey_activitycaller), THIS_ACTIVITY + "_ADD");
                potentialrulescursor.moveToPosition(position);
                currentstorename = potentialrulescursor.getString(purchasableproducts_shopname_offset);
                currentaisleid = potentialrulescursor.getLong(purchasableproducts_aisleid_offset);
                currentaislename = potentialrulescursor.getString(purchasableproducts_aislename_offset);
                intent.putExtra(getResources().getString(R.string.intentkey_productid), potentialrulescursor.getLong(purchasableproducts_productid_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_productname), potentialrulescursor.getString(purchasableproducts_productname_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_aislseid), potentialrulescursor.getLong(purchasableproducts_aisleid_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_aislename), potentialrulescursor.getString(purchasableproducts_aislename_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storename), potentialrulescursor.getString(purchasableproducts_shopname_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storecity), potentialrulescursor.getString(purchasableproducts_shopcity_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storestreet), potentialrulescursor.getString(purchasableproducts_shopstreet_offset));
                startActivity(intent);
            }
        });

        currentruleslist = (ListView) findViewById(R.id.ruleaddeditlist_currentrileslist);
        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
        setRuleListOffsfets(currentrulescursor);
        currentruleslistadapter = new RuleListAdapter(this,currentrulescursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        currentruleslist.setAdapter(currentruleslistadapter);

        //Set Listener to enable a rule to be edited by clicking on a rule
        currentruleslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                resume_state = RESUMESTATE_UPDATE;
                Intent intent = new Intent(context,RuleAddEdit.class);
                intent.putExtra(getResources().getString(R.string.intentkey_activitycaller),THIS_ACTIVITY + "_UPDATE");
                currentrulescursor.moveToPosition(position);
                intent.putExtra(getResources().getString(R.string.intentkey_productid),currentrulescursor.getLong(rulelist_ruleproductref_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_productname),currentrulescursor.getString(rulelist_productname_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_aislseid),currentrulescursor.getLong(rulelist_ruleaisleref_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_aislename),currentrulescursor.getString(rulelist_aislename_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storename),currentrulescursor.getString(rulelist_shopname_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storecity),currentrulescursor.getString(rulelist_shopcity_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_storestreet),currentrulescursor.getString(rulelist_shopstreet_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleid),currentrulescursor.getLong(ruleslist_ruleid_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_rulename),currentrulescursor.getString(ruleslist_rulename_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruletype),currentrulescursor.getLong(rulelist_ruletype_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleprompt),currentrulescursor.getInt(rulelist_rulepromptflag_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleperiod),currentrulescursor.getInt(rulelist_ruleperiod_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemultiplier),currentrulescursor.getInt(rulelist_rulemultiplier_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleactiveon),currentrulescursor.getLong(rulelist_ruleactiveon_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleuses),currentrulescursor.getLong(rulelist_ruleuses_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_rulequantity),currentrulescursor.getInt(rulelist_rulenumbertoget_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemincost),currentrulescursor.getDouble(rulelist_rulemincost_offset));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemaxcost),currentrulescursor.getDouble(rulelist_rulemaxcost_offset));
                startActivity(intent);
            }
        });

        //Set Item Long Click Listener to enable a rule to be deleted
        currentruleslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                currentrulescursor.moveToPosition(position);
                AlertDialog. Builder okdialog = new AlertDialog.Builder(view.getContext());
                okdialog.setTitle(getResources().getString(R.string.rule_delete) + " " + currentrulescursor.getString(ruleslist_rulename_offset));
                okdialog.setMessage(getResources().getString(R.string.rule_delete_message));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton(getResources().getString(R.string.standarddeletetext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopperdb.deleteRule(currentrulescursor.getLong(ruleslist_ruleid_offset));
                        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
                        currentruleslistadapter.swapCursor(currentrulescursor);
                        dialog.cancel();
                    }
                });
                okdialog.setNegativeButton(getResources().getString(R.string.standardcanceltext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                okdialog.show();
                return true;
            }
        });
    }

    protected void onStart() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onStart",devmode);
        super.onStart();
    }

    protected void onResume() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onResume",devmode);
        super.onResume();
        switch(resume_state) {
            case RESUMESTATE_ADD: case RESUMESTATE_UPDATE: {
                currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
                setRuleListOffsfets(currentrulescursor);
                currentruleslistadapter.swapCursor(currentrulescursor);
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
            default: {
                resume_state = RESUMESTATE_NOTHING;
            }
        }
    }

    protected void onStop() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onStop",devmode);
        super.onStop();
    }
    protected void  onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onDestroy",devmode);
        super.onDestroy();
        potentialrulescursor.close();
        currentrulescursor.close();
        shopperdb.close();
    }
    public void raeldone(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"rael_done",devmode);
        finish();
    }
    public void potentialrulesorderbyProduct(View view) {
        potentialruleslistsortorder = Constants.PURCHASABLEPRODUCTSLISTORDER_BY_PRODUCT;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyStore(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STORE;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyStreet(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STREET;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyCity(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_CITY;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyCost(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_COST;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyAisle(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_AISLE;
        potentialrulescursor = shopperdb.getPurchasableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void rulelistorderByRule(View view) {
        rulelistsortorder = Constants.RULELISTORDER_BY_RULE;
        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
        currentruleslistadapter.swapCursor(currentrulescursor);
    }
    public void rulelistorderByDate(View view) {
        rulelistsortorder = Constants.RULELISTORDER_BY_DATE;
        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
        currentruleslistadapter.swapCursor(currentrulescursor);
    }
    public void rulelistorderByPrompt(View view) {
        rulelistsortorder = Constants.RULELISTORDER_BY_PROMPT;
        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
        currentruleslistadapter.swapCursor(currentrulescursor);
    }

    private void setPurchasableProductsOffsets(Cursor cursor) {
        if(purchasableproducts_productusageaisleref_offset != -1) {
            return;
        }
        purchasableproducts_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRIMARY_KEY_NAME);
        purchasableproducts_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        purchasableproducts_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        purchasableproducts_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID_FULL);
        purchasableproducts_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        purchasableproducts_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID_FULL);
        purchasableproducts_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        purchasableproducts_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        purchasableproducts_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        purchasableproducts_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
    }

    private void setRuleListOffsfets(Cursor cursor) {
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
        rulelist_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        rulelist_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        rulelist_aisleshopref_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        rulelist_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        rulelist_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        rulelist_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        rulelist_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
    }
}