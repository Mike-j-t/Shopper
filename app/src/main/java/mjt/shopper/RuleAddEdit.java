package mjt.shopper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mike092015 on 25/04/2016.
 */
public class RuleAddEdit extends AppCompatActivity {
    public final static int RESUMESTATE_NOTHING = 0;
    public int resume_state = RESUMESTATE_NOTHING;
    public boolean devmode;
    public boolean helpoffmode;
    public SharedPreferences sp;
    private final static String THIS_ACTIVITY = "RuleAddEdit";
    @SuppressLint("SimpleDateFormat")
    public SimpleDateFormat sdf = new SimpleDateFormat(Constants.STANDARD_DDMMYYY_FORMAT);
    public Date currentdate = new Date();
    public DatePickerDialog dpd;

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int purchasableproducts_productusageaisleref_offset = -1; //**
    public static int purchasableproducts_productusageproductref_offset;
    public static int purchasableproducts_productusagecost_offset;
    public static int purchasableproducts_productid_offset; //**
    public static int purchasableproducts_productname_offset;
    public static int purchasableproducts_aisleid_offset; //**
    public static int purchasableproducts_aislename_offset;
    public static int purchasableproducts_shopname_offset;
    public static int purchasableproducts_shopcity_offset;
    public static int purchasableproducts_shopstreet_offset;

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

    public static int values_valueid_offset = -1;
    public static int values_valuename_offset;
    public static int values_valuetype_offset;
    public static int values_valueint_offset;
    public static int values_valuereal_offset;
    public static int values_valuetext_offset;
    public static int values_valueincludeinsettings_offset;
    public static int values_valuesettingsinfro_offset;

    public LinearLayout ruleaddedithelplayout;
    public TextView ruleaddeditproductname;
    public int ruleaddeditproductid = -1;
    public TextView ruleaddeditstorename;
    public int ruleaddeditstoreid = -1;
    public TextView ruleaddeditaislename;
    public int ruleaddeditaisleid = -1;
    public EditText ruleaddeditrulename;
    public Switch ruleaddeditautoadd;
    public EditText ruleaddeditstartdate;
    public Spinner ruleaddeditperiodselector;
    public EditText ruleaddeditperiodmultiplier;
    public EditText ruleaddeditquantity;
    public ListView ruleaddeditcurrentrules;
    public long currentproductid;
    public String currentproductname;
    public long currentaisleid;
    public String currentaislename;
    public String currentstorename;
    public String currentstorecity;
    public String currentstorestreet;
    public RuleAddEditPeriodSpinnerCursorAdapter ruleaddeditperiodselectoradapter;
    public Cursor ruleaddeditperiodselectorcursor;
    public Cursor currentrulelistcursor;
    public RuleListAdapter currentrulelistadapter;
    public ListView currentrulelistlistview;
    public String caller;
    public String currentrulename = "";
    public Date currentstartdate;
    public long currentstartdateastime;
    public boolean currentautoadd;
    public String currentperiod;
    public int currentperiodasint;
    public int currentperiodmultiplier;
    public int currentquantity;
    public String currentrulelistsortorder = Constants.RULELISTORDER_BY_RULE;
    public Date oldate;


    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create standard method call log entry (after getting the activities caller)
        caller = getIntent().getStringExtra(getResources().getString(R.string.intentkey_activitycaller));
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "RULEADDEDIT Invoked by " + caller,THIS_ACTIVITY,"onCreate",true);
        super.onCreate(savedInstanceState);
        // Get Preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);
        //Set the Layout for this activity
        setContentView(R.layout.ruleaddedit);


        // Get Layout view groups/views
        ruleaddedithelplayout = (LinearLayout) findViewById(R.id.ruleaddedit_help_layout);
        ruleaddeditproductname = (TextView) findViewById(R.id.ruleaddedit_product);
        ruleaddeditstorename = (TextView) findViewById(R.id.ruleaddedit_store);
        ruleaddeditaislename = (TextView) findViewById(R.id.ruleaddedit_ailse);
        ruleaddeditrulename = (EditText) findViewById(R.id.ruleaddedit_rulename);
        ruleaddeditautoadd = (Switch) findViewById(R.id.ruleaddedit_autoadd);
        ruleaddeditstartdate = (EditText) findViewById(R.id.ruleaddedit_startdate);
        ruleaddeditperiodselector = (Spinner) findViewById(R.id.ruleaddedit_periodselector);
        ruleaddeditperiodmultiplier = (EditText) findViewById(R.id.ruleaddedit_periodmultiplier);
        ruleaddeditquantity = (EditText) findViewById(R.id.ruleaddedit_quantity);
       currentrulelistlistview = (ListView) findViewById(R.id.ruleaddedit_currentrules);

        //Get the data from the calling activity
        currentproductid = getIntent().getLongExtra(getResources().getString(R.string.intentkey_productid),-1);
        currentproductname = getIntent().getStringExtra(getResources().getString(R.string.intentkey_productname));
        currentaisleid = getIntent().getLongExtra(getResources().getString(R.string.intentkey_aislseid),-1);
        currentaislename = getIntent().getStringExtra(getResources().getString(R.string.intentkey_aislename));
        currentstorename = getIntent().getStringExtra(getResources().getString(R.string.intentkey_storename));
        currentstorecity = getIntent().getStringExtra(getResources().getString(R.string.intentkey_storecity));
        currentstorestreet = getIntent().getStringExtra(getResources().getString(R.string.intentkey_storestreet));

        //Set the Textviews that show the currrent product, store and aisle
        ruleaddeditproductname.setText(currentproductname);
        ruleaddeditaislename.setText(currentaislename);
        String extendedshopname = currentstorename + " - " + currentstorecity + " - " + currentstorestreet;
        ruleaddeditstorename.setText(extendedshopname);



        //Populate the period selector spinner
        ruleaddeditperiodselectorcursor = shopperdb.getCursorvalue(Constants.RULEPERIODS,false);
        setValuesOffsets(ruleaddeditperiodselectorcursor);
        ruleaddeditperiodselectoradapter = new RuleAddEditPeriodSpinnerCursorAdapter(this,
                ruleaddeditperiodselectorcursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        ruleaddeditperiodselector.setAdapter(ruleaddeditperiodselectoradapter);

        //Display Help according to Disable Help preference setting
        if(!helpoffmode) {
            ruleaddedithelplayout.setVisibility(View.VISIBLE);
        } else {
            ruleaddedithelplayout.setVisibility(View.GONE);
        }

        // Get a cursor containing the current(exisiting) rules
        currentrulelistcursor = shopperdb.getRuleList("",
                "",
                (long)0,
                false,
                false,
                currentrulelistsortorder
        );
        // Ensure that we have the cursor index/offsets for the columns
        setRuleListOffsfets(currentrulelistcursor);

        // Prepare the adapter/inflater for the current rules list
        currentrulelistadapter = new RuleListAdapter(this,
                currentrulelistcursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        // Set the current rules Listview to use the adapter
        currentrulelistlistview.setAdapter(currentrulelistadapter);

        // Initialise editable inputs according to how activity was invoked
        // Add mode ie a product was clicked in RuleAddEditList activity
        if(caller.equals("RuleAddEditList_ADD")) {
            ruleaddeditstartdate.setText(sdf.format(currentdate));
            ruleaddeditperiodmultiplier.setText("1");
            ruleaddeditquantity.setText("1");
        }
        /**
         * Update an existing Rule
         */
        if(caller.equals("RuleAddEditList_UPDATE")) {
            ruleaddeditrulename.setText(getIntent().getStringExtra(getResources().getString(R.string.intentkey_rulename)));
            ruleaddeditperiodmultiplier.setText(Integer.toString(
                    getIntent().getIntExtra(
                            getResources().getString(R.string.intentkey_rulemultiplier),
                            1
                    )));
            ruleaddeditquantity.setText(Integer.toString(
                    getIntent().getIntExtra(
                            getResources().getString(
                                    R.string.intentkey_rulequantity),
                            1
                    )));
            ruleaddeditstartdate.setText(sdf.format(getIntent().getLongExtra(getResources().getString(R.string.intentkey_ruleactiveon),0)));
            int period = getIntent().getIntExtra(getResources().getString(R.string.intentkey_ruleperiod),0);
            ruleaddeditperiodselector.setSelection(getIntent().getIntExtra(getResources().getString(R.string.intentkey_ruleperiod),0));
            int prompt = getIntent().getIntExtra(getResources().getString(R.string.intentkey_ruleprompt),0);
            if(prompt > 0) {
                ruleaddeditautoadd.setChecked(false);
            } else {
                ruleaddeditautoadd.setChecked(true);
            }
        }
        /**
         * Add a suggested Rule (i.e. multiplier is supplied)
         */
        if(caller.equals("RuleSuggestionActivity_ADD")) {
            ruleaddeditstartdate.setText(sdf.format(currentdate));
            ruleaddeditperiodmultiplier.setText(Integer.toString(
                    getIntent().getIntExtra(
                            getResources().getString(R.string.intentkey_rulemultiplier),
                            1
                    )));
            ruleaddeditquantity.setText("1");
            ruleaddeditperiodselector.setSelection(0);
            ruleaddeditautoadd.setChecked(false);
        }

    }
    @Override
    protected void onResume() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Method Call",THIS_ACTIVITY,"onResume",devmode);
        super.onResume();
    }

    @Override
    protected void onStart() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Method Call",THIS_ACTIVITY,"onStart",devmode);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Method Call",THIS_ACTIVITY,"onStop",devmode);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Method Call",THIS_ACTIVITY,"onDestroy",devmode);
        super.onDestroy();
        ruleaddeditperiodselectorcursor.close();
        shopperdb.close();
    }

    public void ruleaddedit_done(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Method Call",THIS_ACTIVITY,"ruleaddedit_done",devmode);
        finish();
    }

    // Rule Save
    public void ruleaddedit_save(View view) {
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.STANDARD_DDMMYYY_FORMAT);
        Emsg emsg;
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"ruleaddedit_save",devmode);

        // Validate Rule Name - Must be given ie length greater than 0
        currentrulename = ruleaddeditrulename.getText().toString();
        if(currentrulename.length() < 1 ) {
            Toast.makeText(RuleAddEdit.this,getResources().getString(R.string.norulename),Toast.LENGTH_LONG).show();
            ruleaddeditrulename.requestFocus();
            return;
        }

        // validate Rule Start Date - Must be valid date in format dd/mm/yyyy
        emsg = mjtUtils.validateDate(ruleaddeditstartdate.getText().toString());
        if(emsg.getErrorIndicator()) {
            Toast.makeText(RuleAddEdit.this,"Not Saved. Start Date Invalid." + emsg.getErrorMessage(),Toast.LENGTH_LONG).show();
            ruleaddeditstartdate.requestFocus();
            return;
        } else {
            try {
                currentstartdate = sdf.parse(ruleaddeditstartdate.getText().toString());
                currentstartdateastime = currentstartdate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // validate Rule Period (should be OK as selector holds values)
        currentperiod = ruleaddeditperiodselectorcursor.getString(values_valuetext_offset);
        if(currentperiod.length() < 1) {
            Toast.makeText(RuleAddEdit.this,getResources().getString(R.string.noruleperiod),Toast.LENGTH_LONG).show();
            ruleaddeditperiodselector.requestFocus();
            return;
        } else {
            switch(currentperiod) {
                case(Constants.PERIOD_DAYS): {
                    currentperiodasint = Constants.PERIOD_DAYSASINT;
                    break;
                }
                case(Constants.PERIOD_WEEKS): {
                    currentperiodasint = Constants.PERIOD_WEEKSASINT;
                    break;
                }
                case(Constants.PERIOD_FORTNIGHTS): {
                    currentperiodasint = Constants.PERIOD_FORTNIGHTSASINT;
                    break;
                }
                case(Constants.PERIOD_MONTHS): {
                    currentperiodasint = Constants.PERIOD_MONTHSASINT;
                    break;
                }
                case(Constants.PERIOD_QUARTERS): {
                    currentperiodasint = Constants.PERIOD_QUARTERSASINT;
                    break;
                }
                case(Constants.PERIOD_YEARS): {
                    currentperiodasint = Constants.PERIOD_YEARSASINT;
                }
            }
        }
        // Validate rule period multiplier - integer
        emsg = mjtUtils.validateInteger(ruleaddeditperiodmultiplier.getText().toString());
        if(emsg.getErrorIndicator()) {
            Toast.makeText(RuleAddEdit.this, getResources().getString(R.string.invalidperiodmultiplier) + " " + emsg.getErrorMessage(), Toast.LENGTH_SHORT).show();
            ruleaddeditperiodmultiplier.requestFocus();
            return;
        } else {
            currentperiodmultiplier = Integer.parseInt(ruleaddeditperiodmultiplier.getText().toString());
            if(currentperiodmultiplier < 1) {
                Toast.makeText(RuleAddEdit.this, getResources().getString(R.string.invalidperiodmultiplier) + " " + " Cannot be 0.",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // Validate rule quantity
        emsg = mjtUtils.validateInteger(ruleaddeditquantity.getText().toString());
        if(emsg.getErrorIndicator()) {
            Toast.makeText(RuleAddEdit.this,getResources().getString(R.string.invalidquantity) + " " + emsg.getErrorMessage(),Toast.LENGTH_LONG).show();
            ruleaddeditquantity.requestFocus();
            return;
        } else {
            currentquantity = Integer.parseInt(ruleaddeditquantity.getText().toString());
            if(currentquantity < 1) {
                Toast.makeText(RuleAddEdit.this, getResources().getString(R.string.invalidquantity) + " " + " Cannot be 0.",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        currentautoadd = !ruleaddeditautoadd.isChecked();
        if(caller.equals("RuleAddEditList_ADD") || caller.equals("RuleSuggestionActivity_ADD")) {
            shopperdb.insertRule(currentrulename,0,currentautoadd,currentperiodasint,
                    currentperiodmultiplier,currentstartdateastime,
                    currentproductid,currentaisleid,
                    0,0,0,
                    currentquantity);
            if(caller.equals("RuleSuggestionActivity_ADD")) {
                this.finish();
            }
        }
        if(caller.equals("RuleAddEditList_UPDATE")) {
            shopperdb.updateRule(getIntent().getLongExtra(getResources().getString(R.string.intentkey_ruleid),-1),
                    currentrulename,0,currentautoadd,currentperiodasint,
                    currentperiodmultiplier,currentstartdateastime,currentproductid,currentaisleid,
                    0,0,0,
                    currentquantity);
        }

        currentrulelistcursor = shopperdb.getRuleList("","",(long) 0,false,false,currentrulelistsortorder);
        currentrulelistadapter.swapCursor(currentrulelistcursor);
    }
    public void ruleaddeditOrderByRule(View view) {
        currentrulelistsortorder = Constants.RULELISTORDER_BY_RULE;
        currentrulelistcursor = shopperdb.getRuleList("","",(long) 0,false,false,currentrulelistsortorder);
        currentrulelistadapter.swapCursor(currentrulelistcursor);
    }
    public void ruleaddeditOrderByDate(View view) {
        currentrulelistsortorder = Constants.RULELISTORDER_BY_DATE;
        currentrulelistcursor = shopperdb.getRuleList("","",(long) 0,false,false,currentrulelistsortorder);
        currentrulelistadapter.swapCursor(currentrulelistcursor);
    }
    public void ruleaddeditOrderByPrompt(View view) {
        currentrulelistsortorder = Constants.RULELISTORDER_BY_PROMPT;
        currentrulelistcursor = shopperdb.getRuleList("","",(long) 0,false,false,currentrulelistsortorder);
        currentrulelistadapter.swapCursor(currentrulelistcursor);
    }

    public void ruleAddEditDatePick(View view) {

        try {
            oldate = sdf.parse(ruleaddeditstartdate.getText().toString());
        }
        catch (ParseException e ) {
            e.getStackTrace();

        }
        Calendar oldcal = Calendar.getInstance();
        oldcal.setTime(oldate);

        //Setup OnDateSetListener to apply selected date
        OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newcal = Calendar.getInstance();
                newcal.set(year, monthOfYear, dayOfMonth);
                ruleaddeditstartdate.setText(sdf.format(newcal.getTime()));
            }
        };

        DatePickerDialog dpd = new DatePickerDialog(this,odsl,oldcal.get(Calendar.YEAR),oldcal.get(Calendar.MONTH),oldcal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    public void setPurchasableProductsOffsets(Cursor cursor) {
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
        rulelist_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        rulelist_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        rulelist_aisleshopref_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        rulelist_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        rulelist_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        rulelist_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        rulelist_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
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