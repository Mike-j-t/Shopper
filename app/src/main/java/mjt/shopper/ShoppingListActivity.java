package mjt.shopper;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by Mike092015 on 21/03/2016.
 */
public class ShoppingListActivity extends AppCompatActivity{
    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_ADJUSTED = 1;
    public int resume_state = RESUMESTATE_NOTHING;
    public boolean devmode;
    public SharedPreferences sp;
    public boolean helpoffmode;

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

    public static int shoppinglist_shoplistid_offset = -1;
    public static int shoppinglist_shoplistproductref_offset;
    public static int shoppinglist_shoplistdateadded_offset;
    public static int shoppinglist_shoplistnumbertoget_offset;
    public static int shoppinglist_shoplistdone_offset;
    public static int shoppinglist_shoplistdategot_offset;
    public static int shoppinglist_shoplistcost_offset;
    public static int shoppinglist_shoplistproductusageref_offset;
    public static int shoppinglist_shoplistaisleref_offset;
    public static int shoppinglist_productusageproductref_offset;
    public static int shoppinglist_productusageaisleref_offset;
    public static int shoppinglist_productusagecost_offset;
    public static int shoppinglist_productusagebuycount_offset;
    public static int shoppinglist_productusagefirstbuydate_offset;
    public static int shoppinglist_productusagelatestbuydate_offset;
    public static int shoppinglist_productusagemincost_offset;
    public static int shoppinglist_productusageorder_offset;
    public static int shoppinglist_aisleid_offset;
    public static int shoppinglist_aislename_offset;
    public static int shoppinglist_aisleorder_offset;
    public static int shoppinglist_aisleshopref_offest;
    public static int shoppinglist_shopid_offset;
    public static int shoppinglist_shopname_offset;
    public static int shopponglist_shoporder_offset;
    public static int shopponglist_shopstreet_offset;
    public static int shoppinglist_shopcity_offset;
    public static int shoppinglist_shopstate_offset;
    public static int shoppinglist_shopphone_offset;
    public static int shoppinglist_shopnotes_offset;
    public static int shoppinglist_productid_offset;
    public static int shoppinglist_productname_offset;
    public static int shoppinglist_productorder_offset;
    public static int shoppinglist_productaisleref_offset;
    public static int shoppinglist_productuses_offset;
    public static int shoppinglist_productnotes_offset;


    public ShoppingListAdapter currentsla;
    public double purchasedamount = 0;
    public double remainingamount = 0;
    public String autoaddoverview = "";
    public Cursor autoaddentriescursor;
    public Cursor promptentriescursor;
    public ListView promptentrieslist;
    public ShoppingListPromptedRulesAdapter promptentriesadapter;
    public TextView shoppinglisthelp;
    public long chkdate;
    public long promptentriescount = 0;
    public String shoppinglist_normalhelp;
    public String shoppinglist_promptedhelp;

    private final static String THIS_ACTIVITY = "ShoppingListActivity";
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);

    private ListView shoppinglistlv;
    private LinearLayout shoppingliststatslv;
    private Cursor shoppinglistcsr;
    private TextView totalspent;
    private LinearLayout shoppinglisthelplayout;
    private ViewGroup.LayoutParams params;
    private TextView remainingcost;

    protected void onResume() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onResume",devmode);
        super.onResume();
        if(!helpoffmode) {
            params.height = (int) getResources().getDimension(R.dimen.standard_listview_height_small);
            shoppinglistlv.setLayoutParams(params);
            shoppinglisthelplayout.setVisibility(View.VISIBLE);
        } else {
            params.height = (int) getResources().getDimension(R.dimen.standard_listview_height);
            shoppinglistlv.setLayoutParams(params);
            shoppinglisthelplayout.setVisibility(View.GONE);
        }
        switch(resume_state) {
            case RESUMESTATE_ADJUSTED: {
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
            default: {
                resume_state = RESUMESTATE_NOTHING;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Shopping List (SHOP button) Invoked",THIS_ACTIVITY,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist_list);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode), false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);
        remainingcost = (TextView) findViewById(R.id.shoppinglist_remainingcost);

        //Get date used to check which rules should be applied
        Calendar chkcal = Calendar.getInstance();  // time as of now
        chkcal.clear(Calendar.MINUTE); // 00 minutes
        chkcal.clear(Calendar.HOUR); // 00 hour
        chkcal.clear(Calendar.SECOND); // 0 seconds
        chkcal.add(Calendar.DAY_OF_MONTH,1); // add 1 day
        chkdate = chkcal.getTimeInMillis(); // get time as long

        shoppinglisthelplayout = (LinearLayout) findViewById(R.id.shoppinglist_help_layout);
        shoppinglistlv = (ListView) findViewById(R.id.shoppinglist_list);
        shoppingliststatslv = (LinearLayout) findViewById(R.id.shoppinglist_stats);
        shoppinglisthelp = (TextView) findViewById(R.id.shoplist_help);
        shoppinglist_normalhelp = getResources().getText(R.string.shoppinglist_instructions).toString();
        shoppinglist_promptedhelp = getResources().getText(R.string.shoppinglist_prompted_instructions).toString();


        params = shoppinglistlv.getLayoutParams();

        if(!helpoffmode) {
            params.height = (int) 500;
            shoppinglistlv.setLayoutParams(params);
            shoppinglisthelplayout.setVisibility(View.VISIBLE);
        } else {
            params.height = (int) 750;
            shoppinglistlv.setLayoutParams(params);
            shoppinglisthelplayout.setVisibility(View.GONE);
        }

        // Apply an Auto Add Rules
        getAutoAddEntries();

        // If there are any prompted rules then add them, switch display to show prompts and hide
        // the shopping list.
        promptentrieslist = (ListView) findViewById(R.id.autoaddpromptlist);
        promptentriescursor = shopperdb.getRuleList("","",chkdate,true,false,"");
        setRuleListOffsfets(promptentriescursor);
        promptentriescount = promptentriescursor.getCount();
        promptentriesadapter = new ShoppingListPromptedRulesAdapter(this,promptentriescursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        promptentrieslist.setAdapter(promptentriesadapter);
        if(promptentriescursor.getCount() < 1 ) {
            promptentrieslist.setVisibility(View.GONE);
            shoppinglistlv.setVisibility(View.VISIBLE);
            shoppingliststatslv.setVisibility(View.VISIBLE);
            shoppinglisthelp.setText(shoppinglist_normalhelp);
        } else {
            promptentrieslist.setVisibility(View.VISIBLE);
            shoppinglistlv.setVisibility(View.GONE);
            shoppingliststatslv.setVisibility(View.GONE);
            shoppinglisthelp.setText(shoppinglist_promptedhelp);
        }

        totalspent = (TextView) findViewById(R.id.shoppinglist_purchasedamount);
        totalspent.setText(NumberFormat.getCurrencyInstance().format(purchasedamount));

        shoppinglistcsr = shopperdb.getShoppingList();
        setShoppingListOffsets(shoppinglistcsr);
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));

        currentsla = new ShoppingListAdapter(this,shoppinglistcsr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,0);
        shoppinglistlv.setAdapter(currentsla);
    }
    protected void onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onDestroy",devmode);
        super.onDestroy();
        shoppinglistcsr.close();
        promptentriescursor.close();
        shopperdb.close();
    }

    // Button handling (called via XML onCLick)
    // Activiities DONE button
    public void sladone(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sladone",devmode);
        this.finish();
    }

    // Entry BOUGHT button
    public void sledone(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sledone",devmode);
        // Get tag, which is set in ShoppingListAdapter, as it is the position in the list and
        // therefore the cursor of the respective clicked item.
        Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        purchasedamount = purchasedamount + shoppinglistcsr.getDouble(shoppinglist_shoplistcost_offset);
        totalspent.setText(NumberFormat.getCurrencyInstance().format(purchasedamount));
        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(shoppinglist_shoplistid_offset),
                (shoppinglistcsr.getLong(shoppinglist_shoplistnumbertoget_offset) - 1));
        shopperdb.setProductUsageLatestPurchase(shoppinglistcsr.getLong(shoppinglist_aisleid_offset),
                shoppinglistcsr.getLong(shoppinglist_productusageproductref_offset), 1);
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }

    // was Entry REPLACE Button - now redundant due to use of ADJUST button
    public void slereplace(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"slereplace",devmode);
        Integer tag = (Integer) view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        if (purchasedamount - shoppinglistcsr.getDouble(shoppinglist_shoplistcost_offset) >= 0) {
            purchasedamount = purchasedamount - shoppinglistcsr.getDouble(shoppinglist_shoplistcost_offset);
        }
        totalspent.setText(NumberFormat.getCurrencyInstance().format(purchasedamount));
        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(shoppinglist_shoplistid_offset),
                (shoppinglistcsr.getLong(shoppinglist_shoplistnumbertoget_offset) + 1));
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }

    // Entry DELETE button
    public void sledelete(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sledelete",devmode);
        Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        shopperdb.setShopListEntryAsComplete(shoppinglistcsr.getLong(shoppinglist_shoplistid_offset));
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }
    //Tidy Button
    public void slareorg(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"slareorg",devmode);
        shopperdb.reorgShopList();
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }

    // Handle Adding a prompted rule entry
    public void slaaddpromptautoadd(View view) {
        // Move to appropriate cursor row (as per button's tag)
        int position = (Integer) view.getTag();
        promptentriescursor.moveToPosition(position);
        // Insert the new Rule
        shopperdb.insertShopListEntry(promptentriescursor.getLong(rulelist_ruleproductref_offset),
                promptentriescursor.getLong(rulelist_ruleaisleref_offset),
                promptentriescursor.getLong(rulelist_rulenumbertoget_offset),
                promptentriescursor.getDouble(rulelist_productusagecost_offset),true);
        // Set the Next Active on Date, updating the Rules DB accordingly
        setNextAutoAddDate(promptentriescursor.getLong(ruleslist_ruleid_offset),
                promptentriescursor.getLong(rulelist_ruleactiveon_offset),
                promptentriescursor.getInt(rulelist_ruleperiod_offset),
                promptentriescursor.getInt(rulelist_rulemultiplier_offset),
                promptentriescursor.getInt(rulelist_ruleuses_offset));
        // Refresh the cursor from the DB
        promptentriescursor = shopperdb.getRuleList("","",chkdate,true,false,"");
        promptentriesadapter.swapCursor(promptentriescursor);
        // Check if any propmted rules exist and adjust views accordingly
        // i.e. none then display shopping list else display prompt list
        // Note refresh the shopping list cursor as new entries.
        if(promptentriescursor.getCount() < 1 ) {
            promptentrieslist.setVisibility(View.GONE);
            shoppinglistcsr = shopperdb.getShoppingList();
            currentsla.swapCursor(shoppinglistcsr);
            shoppinglistlv.setVisibility(View.VISIBLE);
            shoppingliststatslv.setVisibility(View.VISIBLE);
            shoppinglisthelp.setText(shoppinglist_normalhelp);
        } else {
            promptentrieslist.setVisibility(View.VISIBLE);
            shoppinglistlv.setVisibility(View.GONE);
            shoppingliststatslv.setVisibility(View.GONE);
            shoppinglisthelp.setText(shoppinglist_promptedhelp);
        }
    }
    // Handle Skipping a prompted rule entry
    public void slaskippromptautoadd(View view) {
        // Move to the appropriate cursor row  (as per button's tag)
        int position = (Integer) view.getTag();
        promptentriescursor.moveToPosition(position);
        // Set the Next Active on Date, updating the Rules DB accordingly
        setNextAutoAddDate(promptentriescursor.getLong(ruleslist_ruleid_offset),
                promptentriescursor.getLong(rulelist_ruleactiveon_offset),
                promptentriescursor.getInt(rulelist_ruleperiod_offset),
                promptentriescursor.getInt(rulelist_rulemultiplier_offset),
                promptentriescursor.getInt(rulelist_ruleuses_offset));
        // Refresh the cursor from the DB
        promptentriescursor = shopperdb.getRuleList("","",chkdate,true,false,"");
        promptentriesadapter.swapCursor(promptentriescursor);
        // Check if any propmted rules exist and adjust views accordingly
        // i.e. none then display shopping list else display prompt list
        if(promptentriescursor.getCount() < 1 ) {
            promptentrieslist.setVisibility(View.GONE);
            shoppinglistlv.setVisibility(View.VISIBLE);
            shoppingliststatslv.setVisibility(View.VISIBLE);
            shoppinglisthelp.setText(shoppinglist_normalhelp);
        } else {
            promptentrieslist.setVisibility(View.VISIBLE);
            shoppinglistlv.setVisibility(View.GONE);
            shoppingliststatslv.setVisibility(View.GONE);
            shoppinglisthelp.setText(shoppinglist_promptedhelp);
        }
    }

    // Routines (Methods)
    public Double calculateRemainingAmount(Cursor csr) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"calculateRemainingAmount",devmode);
        double ra = 0;
        csr.moveToPosition(-1);
        while(csr.moveToNext()) {
            ra = ra + csr.getDouble(shoppinglist_productusagecost_offset) * csr.getDouble(shoppinglist_shoplistnumbertoget_offset);
        }
        csr.moveToPosition(-1);
        return ra;
    }

    // Automatically Add Rules based (un-prompted)Entries
    public void getAutoAddEntries() {
        long autoaddcount = 0;
        autoaddoverview = "";
        // Extract extended rules (ie shop, aisle and product in aisle data included) for all
        // promptable rules before first thing tomorrow (ie date as setup)
        autoaddentriescursor = shopperdb.getRuleList("","",chkdate,false,true,"");
        setRuleListOffsfets(autoaddentriescursor);

        // Loop through any NOTE!! data is extracted until all are up-todate
        // i.e. after a rule has been added it's new activeon date is calculated which
        // may be before tomorrow's date
        while(autoaddentriescursor.getCount() > 0) {
            // Move past initial position (Move to First would also have same effect???)
            autoaddentriescursor.moveToNext();
            // If the multiplier is 0 then skip as this rule is invalid (stops endless loop
            // when using cetNextAutoAddDate as it adds nothing to date so newly created entry
            // is then processed and so on
            if(autoaddentriescursor.getInt(rulelist_rulemultiplier_offset) > 0) {
                //Build report string for this entry
                autoaddoverview = autoaddoverview +
                        "Rule - " + autoaddentriescursor.getString(ruleslist_rulename_offset) +
                        "ID=" + Long.toString(autoaddentriescursor.getLong(ruleslist_ruleid_offset)) +
                        " \n\tPRODUCT=" + autoaddentriescursor.getString(rulelist_productname_offset) +
                        " \n\tSHOP=" + autoaddentriescursor.getString(rulelist_shopname_offset) + " - " +
                        autoaddentriescursor.getString(rulelist_shopcity_offset) + " - " +
                        autoaddentriescursor.getString(rulelist_shopstreet_offset) +
                        " \n\t\tAISLE=" + autoaddentriescursor.getString(rulelist_aislename_offset) +
                        " being ";
                // Increment count of rules added
                autoaddcount++;
                // Set action (sort redundant after removing processing of promptables)
                autoaddoverview = autoaddoverview + "ADDED\n";
                // Add the entry to the shopping list
                shopperdb.insertShopListEntry(autoaddentriescursor.getLong(rulelist_ruleproductref_offset),
                        autoaddentriescursor.getLong(rulelist_ruleaisleref_offset),
                        autoaddentriescursor.getInt(rulelist_rulenumbertoget_offset),
                        autoaddentriescursor.getDouble(rulelist_productusagecost_offset), true);
                // Change the rule's use count and the rules activeon date
                setNextAutoAddDate(autoaddentriescursor.getLong(ruleslist_ruleid_offset),
                        autoaddentriescursor.getLong(rulelist_ruleactiveon_offset),
                        autoaddentriescursor.getInt(rulelist_ruleperiod_offset),
                        autoaddentriescursor.getInt(rulelist_rulemultiplier_offset),
                        autoaddentriescursor.getInt(rulelist_ruleuses_offset));
                //Re-Extract rules
                autoaddentriescursor = shopperdb.getRuleList("", "", chkdate, false, true, "");
            }
        }
        // Displayan overview of what has been done
        autoaddoverview = autoaddoverview + "RULE BASED ENTRIES ADDED=" + Long.toString(autoaddcount);
        Toast.makeText(this,autoaddoverview,Toast.LENGTH_LONG).show();
        // Finished with db cursor
        autoaddentriescursor.close();
    }

    // Update Rule to the next activeon date
    public void setNextAutoAddDate(long ruleid, long currentdate, int period, int multiplier, int uses) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentdate);
        switch(period) {
            case Constants.PERIOD_DAYSASINT:
                cal.add(Calendar.DAY_OF_MONTH,(multiplier));
                break;
            case Constants.PERIOD_WEEKSASINT:
                cal.add(Calendar.DAY_OF_MONTH, (multiplier * 7));
                break;
            case Constants.PERIOD_FORTNIGHTSASINT:
                cal.add(Calendar.DAY_OF_MONTH, (multiplier * 14));
                break;
            case Constants.PERIOD_MONTHSASINT:
                cal.add(Calendar.MONTH, multiplier);
                break;
            case Constants.PERIOD_QUARTERSASINT:
                cal.add(Calendar.MONTH, (multiplier * 3));
                break;
            case Constants.PERIOD_YEARSASINT:
                cal.add(Calendar.YEAR,multiplier);
                break;
        }
        long newdate = cal.getTimeInMillis();
        uses++;
        shopperdb.updateRuleDateAndUse(ruleid, newdate, uses);
    }

    // ADJUST Button handler i.e display dialog with various actions
    public void actionDialogShow(View view){

        final Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);

        final Dialog actionsdialog = new Dialog(ShoppingListActivity.this);
        actionsdialog.setContentView(R.layout.shoppinglistactionsdialog);

        LinearLayout adjusthelplayout = (LinearLayout) actionsdialog.findViewById(R.id.shoppinglistactions_help_layout);

        if(!helpoffmode) {
            adjusthelplayout.setVisibility(View.VISIBLE);
        } else {
            adjusthelplayout.setVisibility(View.GONE);
        }

        final TextView originalproduct = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_product);
        final TextView originalquantity = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_quantity);
        final TextView originalcost = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_cost);
        TextView originaltotal = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_total);
        final EditText newquantity = (EditText) actionsdialog.findViewById(R.id.shoppinglistaction_newquantity);
        final EditText newcost = (EditText) actionsdialog.findViewById(R.id.shoppinglistaction_newcost);
        final EditText newname = (EditText) actionsdialog.findViewById(R.id.shoppinglistaction_newname);

        originalproduct.setText(shoppinglistcsr.getString(shoppinglist_productname_offset));
        newname.setText(shoppinglistcsr.getString(shoppinglist_productname_offset));
        originalquantity.setText(shoppinglistcsr.getString(shoppinglist_shoplistnumbertoget_offset));
        newquantity.setText(shoppinglistcsr.getString(shoppinglist_shoplistnumbertoget_offset));
        originalcost.setText(shoppinglistcsr.getString(shoppinglist_productusagecost_offset));
        newcost.setText(shoppinglistcsr.getString(shoppinglist_productusagecost_offset));
        originaltotal.setText(NumberFormat.getCurrencyInstance().format(shoppinglistcsr.getDouble(shoppinglist_shoplistnumbertoget_offset) * shoppinglistcsr.getDouble(shoppinglist_productusagecost_offset)));
        actionsdialog.setTitle("Shopping List Actions");

        Button donebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_finish_button);
        Button savebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_save_button);
        Button restorebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_restore_button);
        Button increasequantity = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_quantityplus_button);
        Button decreasequantity = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_quantityminus_button);

        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsdialog.dismiss();
                if(resume_state == RESUMESTATE_ADJUSTED) {
                    shoppinglistcsr = shopperdb.getShoppingList();
                    remainingamount = calculateRemainingAmount(shoppinglistcsr);
                    remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
                    currentsla.swapCursor(shoppinglistcsr);
                    resume_state = RESUMESTATE_NOTHING;
                }
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppinglistcsr.moveToPosition(tag);
                String nqty = newquantity.getText().toString();
                String oqty = originalquantity.getText().toString();
                String ncost = newcost.getText().toString();
                String ocost = originalcost.getText().toString();
                Float ucost;
                try {
                    ucost = Float.valueOf(ncost);
                } catch (NumberFormatException e) {
                    e.getStackTrace();
                    newcost.setText(originalcost.getText().toString());
                    Toast.makeText(actionsdialog.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }

                String nname = newname.getText().toString();
                String oname = originalproduct.getText().toString();
                String updateresults = "";
                // Check to see if Updates are Required
                if(nqty.equals("0")) {
                    newquantity.setText("1");
                    Toast.makeText(actionsdialog.getContext(),"New Quantity was 0. This has been changed to 1 as 0 is not allowed.",Toast.LENGTH_LONG).show();
                    return;
                }
                if(nname.isEmpty()) {
                    newname.setText(originalproduct.getText().toString());
                    Toast.makeText(actionsdialog.getContext(),"New Product Name cannot be blank. Reset to Original Name",Toast.LENGTH_LONG).show();
                    return;
                }
                if(nqty.equals(oqty) && ncost.equals(ocost) && nname.equals(oname)) {
                    // No Updates/Changes made so don't update, just issue meesage
                    Toast.makeText(actionsdialog.getContext(),originalproduct.getText() + " Not Changed, Update Not Required.",Toast.LENGTH_LONG).show();
                    resume_state = RESUMESTATE_NOTHING;
                } else {
                    resume_state = RESUMESTATE_ADJUSTED;
                    // If quantity has changed then update the shoppinglist entry quantity
                    if(!nqty.equals(oqty)) {
                        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(shoppinglist_shoplistid_offset),Integer.parseInt(nqty));
                        if(updateresults.length() > 0) {
                            updateresults = updateresults + "\n";
                        }
                        updateresults = updateresults + "Qauntity adjusted from " + oqty + " to " + nqty + ".";

                    }
                    // If cost has changed then update the productusage entry to reflect the new cost
                    if(!ncost.equals(ocost)) {
                        shopperdb.updateProductInAisle(shoppinglistcsr.getLong(shoppinglist_aisleid_offset),
                                shoppinglistcsr.getLong(shoppinglist_productusageproductref_offset),
                                ucost,
                                shoppinglistcsr.getInt(shoppinglist_productusagebuycount_offset),
                                shoppinglistcsr.getLong(shoppinglist_productusagefirstbuydate_offset),
                                shoppinglistcsr.getLong(shoppinglist_productusagelatestbuydate_offset),
                                shoppinglistcsr.getInt(shoppinglist_productusageorder_offset),
                                shoppinglistcsr.getFloat(shoppinglist_productusagemincost_offset));
                        if(updateresults.length() > 0) {
                            updateresults = updateresults + "\n";
                        }
                        updateresults = updateresults + "Cost adjusted from " + ocost + " to " + ncost + ".";

                    }
                    if(!nname.equals(oname)) {
                        shopperdb.updateProduct(shoppinglistcsr.getLong(shoppinglist_shoplistproductref_offset),
                                nname,
                                shoppinglistcsr.getString(shoppinglist_productnotes_offset));
                        if(updateresults.length() > 0 ) {
                            updateresults = updateresults + "\n";
                        }
                        updateresults = updateresults + "Product Name change from :-" + oname + " to :-" + nname + ".";

                    }
                    Toast.makeText(actionsdialog.getContext(), updateresults, Toast.LENGTH_LONG).show();
                }
            }
        });


        // Restore original Values (Quantity and Cost)
        restorebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                newquantity.setText(originalquantity.getText());
                newcost.setText(originalcost.getText());
                newname.setText(originalproduct.getText());
            }
        });

        // Handle +1 (increment quantity) Button i.e. add 1 to quantity within bounds
        increasequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer nqty = Integer.parseInt(newquantity.getText().toString()) + 1;
                // Check bounds i.e. max allowed is 9999
                if(nqty > 9999 ) {
                    nqty = 9999;
                }
                newquantity.setText(nqty.toString());
            }
        });

        // Handle -1 (decrement quantity) Button i.e. subtract 1 from quantity within bounds
        decreasequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer nqty = Integer.parseInt(newquantity.getText().toString()) - 1;
                //Check bounds i.e. must not drop below 1
                if(nqty < 1 ) {
                    nqty = 1;
                }
                newquantity.setText(nqty.toString());
            }
        });
        actionsdialog.show();
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

    public void setShoppingListOffsets(Cursor cursor) {
        if(shoppinglist_shoplistid_offset != -1) {
            return;
        }
        shoppinglist_shoplistid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_ID);
        shoppinglist_shoplistproductref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTREF);
        shoppinglist_shoplistdateadded_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEADDED);
        shoppinglist_shoplistnumbertoget_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_NUMBERTOGET);
        shoppinglist_shoplistdone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DONE);
        shoppinglist_shoplistdategot_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_DATEGOT);
        shoppinglist_shoplistcost_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_COST);
        shoppinglist_shoplistproductusageref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_PRODUCTUSAGEREF);
        shoppinglist_shoplistaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPLIST_COLUMN_AISLEREF);
        shoppinglist_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF_FULL);
        shoppinglist_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF);
        shoppinglist_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        shoppinglist_productusagebuycount_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT);
        shoppinglist_productusagefirstbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE);
        shoppinglist_productusagelatestbuydate_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LATESTBUYDATE);
        shoppinglist_productusagemincost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST);
        shoppinglist_productusageorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER);
        shoppinglist_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID_FULL);
        shoppinglist_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        shoppinglist_aisleorder_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
        shoppinglist_aisleshopref_offest = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
        shoppinglist_shopid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID_FULL);
        shoppinglist_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        shopponglist_shoporder_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
        shopponglist_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        shoppinglist_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        shoppinglist_shopstate_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
        shoppinglist_shopphone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
        shoppinglist_shopnotes_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
        shoppinglist_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID_FULL);
        shoppinglist_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        shoppinglist_productorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
        shoppinglist_productaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
        shoppinglist_productuses_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
        shoppinglist_productnotes_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
    }
}
