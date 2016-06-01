package mjt.shopper;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
                shoppinglistcsr = shopperdb.getShoppingList();
                remainingamount = calculateRemainingAmount(shoppinglistcsr);
                remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
                currentsla.swapCursor(shoppinglistcsr);
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
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));

        currentsla = new ShoppingListAdapter(this,shoppinglistcsr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,0);
        shoppinglistlv.setAdapter(currentsla);
        shoppinglistlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "ListView clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onDestroy",devmode);
        super.onDestroy();
        shoppinglistcsr.close();
        promptentriescursor.close();
        shopperdb.close();
    }

    // Button handling (called via XML onCLick)
    public void sladone(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sladone",devmode);
        this.finish();
    }
    public void sledone(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sledone",devmode);
        // Get tag, which is set in ShoppingListAdapter, as it is the position in the list and
        // therefore the cursor of the respective clicked item.
        Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        purchasedamount = purchasedamount + shoppinglistcsr.getDouble(6);
        totalspent.setText(NumberFormat.getCurrencyInstance().format(purchasedamount));
        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(0), (shoppinglistcsr.getLong(3) - 1));
        shopperdb.setProductUsageLatestPurchase(shoppinglistcsr.getLong(17), shoppinglistcsr.getLong(9), 1);
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }
    public void slereplace(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"slereplace",devmode);
        Integer tag = (Integer) view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        if (purchasedamount - shoppinglistcsr.getDouble(6) >= 0) {
            purchasedamount = purchasedamount - shoppinglistcsr.getDouble(6);
        }
        totalspent.setText(NumberFormat.getCurrencyInstance().format(purchasedamount));
        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(0), (shoppinglistcsr.getLong(3) + 1));
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }
    public void sledelete(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"sledelete",devmode);
        Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);
        shopperdb.setShopListEntryAsComplete(shoppinglistcsr.getLong(0));
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }
    //Trim Button
    public void slareorg(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"slareorg",devmode);
        shopperdb.reorgShopList();
        shoppinglistcsr = shopperdb.getShoppingList();
        remainingamount = calculateRemainingAmount(shoppinglistcsr);
        remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
        currentsla.swapCursor(shoppinglistcsr);
    }
    public void slaaddrules(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"slaaddrules",devmode);
    }
    // Handle Adding a prompted rule entry
    public void slaaddpromptautoadd(View view) {
        // Move to appropriate cursor row (as per button's tag)
        int position = (Integer) view.getTag();
        promptentriescursor.moveToPosition(position);
        // Insert the new Rule
        shopperdb.insertShopListEntry(promptentriescursor.getLong(7),
                promptentriescursor.getLong(8),
                promptentriescursor.getLong(10),
                promptentriescursor.getDouble(19),true);
        // Set the Next Active on Date, updating the Rules DB accordingly
        setNextAutoAddDate(promptentriescursor.getLong(0), promptentriescursor.getLong(6),
                promptentriescursor.getInt(4),promptentriescursor.getInt(5),
                promptentriescursor.getInt(9));
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
    // Handle Skipping a promted rule entry
    public void slaskippromptautoadd(View view) {
        // Move to the appropriate cursor row  (as per button's tag)
        int position = (Integer) view.getTag();
        promptentriescursor.moveToPosition(position);
        // Set the Next Active on Date, updating the Rules DB accordingly
        setNextAutoAddDate(promptentriescursor.getLong(0), promptentriescursor.getLong(6),
                promptentriescursor.getInt(4),promptentriescursor.getInt(5),
                promptentriescursor.getInt(9));
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
    public Double calculateRemainingAmount(Cursor csr) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"calculateRemainingAmount",devmode);
        double ra = 0;
        csr.moveToPosition(-1);
        while(csr.moveToNext()) {
            ra = ra + csr.getDouble(11) * csr.getDouble(3);
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

        // Loop through any NOTE!! data is extracted until all are up-todate
        // i.e. after a rule has been added it's new activeon date is calculated which
        // may be before tomorrow's date
        while(autoaddentriescursor.getCount() > 0) {
            // Move past initial position (Move to First would also have same effect???)
            autoaddentriescursor.moveToNext();
            //Build report string for this entry
            autoaddoverview = autoaddoverview + "Rule - " + autoaddentriescursor.getString(1) +
                    "ID=" + Long.toString(autoaddentriescursor.getLong(0)) +
                    " \n\tPRODUCT=" + autoaddentriescursor.getString(13) +
                    " \n\tSHOP=" + autoaddentriescursor.getString(16) + " - " +
                    autoaddentriescursor.getString(17) + " - " +
                    autoaddentriescursor.getString(18) +
                    " \n\t\tAISLE=" + autoaddentriescursor.getString(14) +
                    " being ";
            // Increment count of rules added
            autoaddcount++;
            // Set action (sort redundant after removing processing of promptables)
            autoaddoverview = autoaddoverview + "ADDED\n";
            // Add the entry to the shopping list
            shopperdb.insertShopListEntry(autoaddentriescursor.getLong(7),
                    autoaddentriescursor.getLong(8),
                    autoaddentriescursor.getInt(10),
                    autoaddentriescursor.getDouble(19),true);
            // Change the rule's use count and the rules activeon date
            setNextAutoAddDate(autoaddentriescursor.getLong(0),autoaddentriescursor.getLong(6),
                    autoaddentriescursor.getInt(4),autoaddentriescursor.getInt(5),
                    autoaddentriescursor.getInt(9));
            //Re-Extract rules
            autoaddentriescursor = shopperdb.getRuleList("","",chkdate,false,true,"");
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
                cal.add(cal.DAY_OF_MONTH,(multiplier));
                break;
            case Constants.PERIOD_WEEKSASINT:
                cal.add(cal.DAY_OF_MONTH, (multiplier * 7));
                break;
            case Constants.PERIOD_FORTNIGHTSASINT:
                cal.add(cal.DAY_OF_MONTH, (multiplier * 14));
                break;
            case Constants.PERIOD_MONTHSASINT:
                cal.add(cal.MONTH, multiplier);
                break;
            case Constants.PERIOD_QUARTERSASINT:
                cal.add(cal.MONTH, (multiplier * 3));
                break;
            case Constants.PERIOD_YEARSASINT:
                cal.add(cal.YEAR,multiplier);
                break;
        }
        long newdate = cal.getTimeInMillis();
        uses++;
        shopperdb.updateRuleDateAndUse(ruleid, newdate, uses);
    }

    public void actionDialogShow(View view){
        //Intent intent = new Intent(this, CustomDialog.class);
        //startActivity(intent);

        Integer tag = (Integer)view.getTag();
        shoppinglistcsr.moveToPosition(tag);

        final Dialog actionsdialog = new Dialog(ShoppingListActivity.this);
        actionsdialog.setContentView(R.layout.shoppinglistactionsdialog);

        final TextView originalproduct = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_product);
        final TextView originalquantity = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_quantity);
        final TextView originalcost = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_cost);
        TextView originaltotal = (TextView) actionsdialog.findViewById(R.id.shoppinglistactions_originaldata_total);
        final EditText newquantity = (EditText) actionsdialog.findViewById(R.id.shoppinglistaction_newquantity);
        final EditText newcost = (EditText) actionsdialog.findViewById(R.id.shoppinglistaction_newcost);

        originalproduct.setText(shoppinglistcsr.getString(30));
        originalquantity.setText(shoppinglistcsr.getString(3));
        newquantity.setText(shoppinglistcsr.getString(3));
        originalcost.setText(shoppinglistcsr.getString(11));
        newcost.setText(shoppinglistcsr.getString(11));
        originaltotal.setText(NumberFormat.getCurrencyInstance().format(shoppinglistcsr.getDouble(3) * shoppinglistcsr.getDouble(11)));
        actionsdialog.setTitle("Shopping List Actions");

        Button donebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_finish_button);
        Button savebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_save_button);
        Button restorebutton = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_restore_button);
        Button increasequantity = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_quantityplus_button);
        Button decreasequantity = (Button) actionsdialog.findViewById(R.id.shoppinglistactions_quantityminus_button);

        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resume_state == RESUMESTATE_ADJUSTED) {
                    Toast.makeText(actionsdialog.getContext(),"Action Dialog Shown now Dismissed",Toast.LENGTH_LONG).show();
                    shoppinglistcsr = shopperdb.getShoppingList();
                    remainingamount = calculateRemainingAmount(shoppinglistcsr);
                    remainingcost.setText(NumberFormat.getCurrencyInstance().format(remainingamount));
                    currentsla.swapCursor(shoppinglistcsr);
                    resume_state = RESUMESTATE_NOTHING;
                }
                actionsdialog.dismiss();
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nqty = newquantity.getText().toString();
                String oqty = originalquantity.getText().toString();
                String ncost = newcost.getText().toString();
                String ocost = originalcost.getText().toString();
                Float ucost = Float.valueOf(ncost);
                // Check to see if Updates are Required
                if(nqty.equals("0")) {
                    newquantity.setText("1");
                    Toast.makeText(actionsdialog.getContext(),"New Quantity was 0. This has been changed to 1 as 0 is not allowed.",Toast.LENGTH_LONG).show();
                    return;
                }
                if(nqty.equals(oqty) && ncost.equals(ocost)) {
                    // No Updates/Changes made so don't update, just issue meesage
                    Toast.makeText(actionsdialog.getContext(),originalproduct.getText() + " Not Changed, Update Not Required.",Toast.LENGTH_LONG).show();
                } else {
                    resume_state = RESUMESTATE_ADJUSTED;
                    // If quantity has changed then update the shoppinglist entry quantity
                    if(!nqty.equals(oqty)) {
                        shopperdb.changeShopListEntryQuantity(shoppinglistcsr.getLong(0),Integer.parseInt(nqty));
                    }
                    if(!ncost.equals(ocost)) {
                        shopperdb.updateProductInAisle(shoppinglistcsr.getLong(17),shoppinglistcsr.getLong(9),ucost,
                                shoppinglistcsr.getInt(12),shoppinglistcsr.getLong(13),shoppinglistcsr.getLong(14),shoppinglistcsr.getInt(16),shoppinglistcsr.getFloat(15));
                    }
                    Toast.makeText(actionsdialog.getContext(),originalproduct.getText() + " Updated."
                                    + " New Qauntity=" + newquantity.getText() + " was(" + originalquantity.getText() + ")"
                                    + " New Cost=$" + newcost.getText() + " was($" + originalcost.getText() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // Restore original Values (Quantity and Cost)
        restorebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                newquantity.setText(originalquantity.getText());
                newcost.setText(originalcost.getText());
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
}
