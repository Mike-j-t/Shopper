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
 * Created by Mike092015 on 21/04/2016.
 */
public class RuleAddEditList extends AppCompatActivity {
    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_ADD = 1;
    public final static int RESUMESTATE_UPDATE = 2;
    public final static int RESUMESTATE_DELETE = 3;
    public int resume_state = RESUMESTATE_NOTHING;
    public  boolean devmode;
    public boolean helpoffmode;
    public SharedPreferences sp;
    private final static String THIS_ACTIVITY = "RuleAddEditList";

    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);

    public LinearLayout help_layout;
    public ListView potentialruleslist;
    public Cursor potentialrulescursor;
    public PurchaseableProductsAdapter potentialruleslistadapter;
    public String potentialrulesproductsearcharg = "";
    public String potentialrulesstoresearcharg = "";
    public EditText potentialrulesproductinput;
    public EditText potentialrulesstoreinput;
    public ListView currentruleslist;
    public Cursor currentrulescursor;
    public RuleListAdapter currentruleslistadapter;
    public long currentproductid = -1;
    public String currentproductname = "";
    public long currentaisleid = -1;
    public String currentaislename = "";
    public long currentstoreid = -1;
    public String currentstorename = "";
    public String potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_PRODUCT;
    public String rulelistsortorder = Constants.RULELISTORDER_BY_RULE;


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
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesproductsearcharg,
                potentialrulesstoresearcharg,potentialruleslistsortorder);
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
                potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesproductsearcharg,
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
                potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesproductsearcharg,
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
                currentstorename = potentialrulescursor.getString(7);
                currentaisleid = potentialrulescursor.getLong(5);
                currentaislename = potentialrulescursor.getString(6);
                intent.putExtra(getResources().getString(R.string.intentkey_productid), potentialrulescursor.getLong(3));
                intent.putExtra(getResources().getString(R.string.intentkey_productname), potentialrulescursor.getString(4));
                intent.putExtra(getResources().getString(R.string.intentkey_aislseid), potentialrulescursor.getLong(5));
                intent.putExtra(getResources().getString(R.string.intentkey_aislename), potentialrulescursor.getString(6));
                intent.putExtra(getResources().getString(R.string.intentkey_storename), potentialrulescursor.getString(7));
                intent.putExtra(getResources().getString(R.string.intentkey_storecity), potentialrulescursor.getString(8));
                intent.putExtra(getResources().getString(R.string.intentkey_storestreet), potentialrulescursor.getString(9));
                startActivity(intent);
            }
        });

        currentruleslist = (ListView) findViewById(R.id.ruleaddeditlist_currentrileslist);
        currentrulescursor = shopperdb.getRuleList("","",(long) 0,false,false,rulelistsortorder);
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
                intent.putExtra(getResources().getString(R.string.intentkey_productid),currentrulescursor.getLong(7));
                intent.putExtra(getResources().getString(R.string.intentkey_productname),currentrulescursor.getString(13));
                intent.putExtra(getResources().getString(R.string.intentkey_aislseid),currentrulescursor.getLong(8));
                intent.putExtra(getResources().getString(R.string.intentkey_aislename),currentrulescursor.getString(14));
                intent.putExtra(getResources().getString(R.string.intentkey_storename),currentrulescursor.getString(16));
                intent.putExtra(getResources().getString(R.string.intentkey_storecity),currentrulescursor.getString(17));
                intent.putExtra(getResources().getString(R.string.intentkey_storestreet),currentrulescursor.getString(18));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleid),currentrulescursor.getLong(0));
                intent.putExtra(getResources().getString(R.string.intentkey_rulename),currentrulescursor.getString(1));
                intent.putExtra(getResources().getString(R.string.intentkey_ruletype),currentrulescursor.getLong(2));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleprompt),currentrulescursor.getInt(3));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleperiod),currentrulescursor.getInt(4));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemultiplier),currentrulescursor.getInt(5));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleactiveon),currentrulescursor.getLong(6));
                intent.putExtra(getResources().getString(R.string.intentkey_ruleuses),currentrulescursor.getLong(9));
                intent.putExtra(getResources().getString(R.string.intentkey_rulequantity),currentrulescursor.getInt(10));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemincost),currentrulescursor.getDouble(11));
                intent.putExtra(getResources().getString(R.string.intentkey_rulemaxcost),currentrulescursor.getDouble(12));
                startActivity(intent);
            }
        });

        //Set Item Long Click Listener to enable a rule to be deleted
        currentruleslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                currentrulescursor.moveToPosition(position);
                AlertDialog.Builder okdialog = new AlertDialog.Builder(view.getContext());
                okdialog.setTitle(getResources().getString(R.string.rule_delete) + " " + currentrulescursor.getString(1));
                okdialog.setMessage(getResources().getString(R.string.rule_delete_message));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton(getResources().getString(R.string.standarddeletetext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopperdb.deleteRule(currentrulescursor.getLong(0));
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
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_PRODUCT;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyStore(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STORE;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyStreet(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STREET;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyCity(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_CITY;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyCost(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_COST;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
        potentialruleslistadapter.swapCursor(potentialrulescursor);
    }
    public void potentialrulesorderbyAisle(View view) {
        potentialruleslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_AISLE;
        potentialrulescursor = shopperdb.getPurchaseableProducts(potentialrulesstoresearcharg,potentialrulesstoresearcharg,potentialruleslistsortorder);
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
}