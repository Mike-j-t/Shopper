package mjt.shopper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

// MainActivity - Application Starts Here
// Perform inital checks
// Database exists, if not creates database
// Retrieves Settings (Disable Help Screens boolean and Developermode boolean)
// Gets database stats (row counts of the tables), displays them id inn developer mode
// Displays context relevant buttons including extra buttons if in developer mode
// Compore actual DB against design and add tables/columns if necessary
// If no stores then invoke add store directly
// Setup Values table (if already done then duplicates won't be added)
// Button handling as per xml then handle selection of options

//TODO update manual for new features added
//TODO additional settings for Rule Selection 3; allow suggestions, freq and Next suggest
//TODO update manual for addition actions (...) Data handling and Rule suggestions
//TODO update manual to fully cover Datahandling
//TODO update manual to full cover Rule Suggestion
//TODO Help display for Rule Suggestion

public class MainActivity extends AppCompatActivity {

    // Activity/Shared preferences variables
    private final static String THIS_ACTIVITY = "MainActivity"; // Allows ActivityNames to be sent
    private SharedPreferences sharedPreferences;
    private boolean developermode = false;
    private boolean helpoffmode = false;
    private boolean rulesuggestion = true;
    private int rulesuggestionfrequency = 30;
    private boolean resumestate = false;

    // Data/DB variables (generally in unset state)
    public ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
    private int shopcount;
    private int aislecount;
    private int productcount;
    private int productsinaisles;
    private int rulecount;
    private int shoppinglistcount;

    // View variables (in unset state)
    private TextView stats;
    private TextView storesbutton;
    private TextView storeshelpbutton;
    private TextView aislesbutton;
    private TextView aisleshelpbutton;
    private TextView productsbutton;
    private TextView productshelpbutton;
    private TextView productusagebutton;
    private TextView productusagehelpbutton;
    private TextView shoppinglistbutton;
    private TextView shoppinglisthelpbutton;
    private TextView ruleaddbutton;
    private TextView ruleaddhelpbutton;
    private TextView dbdatabutton;
    private TextView dbdatahelpbutton;
    private TextView dbschemabutton;
    private TextView dbschemahelpbutton;

    //==============================================================================================
    //==============================================================================================
    protected void onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Shopper Application Starting", THIS_ACTIVITY, "onCreate", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);

        //Need to specifically request WRITE_EXTERNAL_STORAGE (READ Implied)
        // if API 23+
        if(Build.VERSION.SDK_INT >= 23) {
            ExternalStoragePermissions.verifyStoragePermissions(this);
        }
        PreferenceManager.setDefaultValues(this, R.xml.usersettings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        developermode = sharedPreferences.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode), false);
        //helpoffmode = sharedPreferences.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode), false);
        //rulesuggestion = sharedPreferences.getBoolean(getResources().getString(R.string.sharedpreferencekey_allowrulesuggest),true);
        //rulesuggestionfrequency = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.sharedpreferencekey_rulesuggestfrequency),"30"));
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Primary Activity Initialisation Complete", THIS_ACTIVITY, "onCreate",developermode);

        // Check to see if database exists
        if(!doesDatabaseExist(this,ShopperDBHelper.DATABASE_NAME)) {
            Toast.makeText(this,"Welcome to Shopper. Creating Shopper Database and underlying tables.",
                    Toast.LENGTH_LONG).show();
            mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                    "Shopper Database will be created as it didn't exist",
                    THIS_ACTIVITY, "onCreate",developermode);
        }

        // Get Button id's
        storesbutton = (TextView) this.findViewById(R.id.am_stores_button);
        storeshelpbutton = (TextView) this.findViewById(R.id.am_storeshelp_button);
        aislesbutton = (TextView) this.findViewById(R.id.am_aisles_button);
        aisleshelpbutton = (TextView) this.findViewById(R.id.am_aisleshelp_button);
        productsbutton = (TextView) this.findViewById(R.id.am_produce_button);
        productshelpbutton = (TextView) this.findViewById(R.id.am_producehelp_button);
        productusagebutton = (TextView) this.findViewById(R.id.am_toget_button);
        productusagehelpbutton = (TextView) this.findViewById(R.id.am_togethelp_button);
        shoppinglistbutton = (TextView) this.findViewById(R.id.am_shop_button);
        shoppinglisthelpbutton = (TextView) this.findViewById(R.id.am_shophelp_button);
        ruleaddbutton = (TextView) this.findViewById(R.id.am_ruleadd_button);
        ruleaddhelpbutton = (TextView) this.findViewById(R.id.am_ruleaddhelp_button);
        dbdatabutton = (TextView) this.findViewById(R.id.am_data_button);
        dbdatahelpbutton = (TextView) this.findViewById(R.id.am_datahelp_button);
        dbschemabutton = (TextView) this.findViewById(R.id.am_schema_button);
        dbschemahelpbutton = (TextView) this.findViewById(R.id.am_schemahelp_button);
        stats = (TextView) this.findViewById(R.id.amtv30);

        // Perform Initialisation
        getSettings();
        handleStats();
        selectButtons();
        initialisationChecks();
        ShopperDBHelper valuesdb = new ShopperDBHelper(this,null,null,1);


        //Setup AutoAdd Periods for dropdown selection
        //To add another repeat
        //Retrieve via getStringArrayListValue method
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_DAYS,true,false,"");
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_WEEKS,true,false,"");
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_FORTNIGHTS,true,false,"");
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_MONTHS,true,false,"");
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_QUARTERS,true,false,"");
        valuesdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_YEARS,true,false,"");
        valuesdb.insertValuesEntry(Constants.LASTRULESUGGESTION,(long)0,false,false,"");
        // Settings
        // Debug option note sharedpreferences are used
        valuesdb.insertValuesEntry(Constants.DEBUGFLAG,(long) 0,false,true,"Turn On Debugging Options");

        //Just for testing purposes
        /*
        valuesdb.insertValuesEntry("TESTDouble",1.56,false,false,"");
        valuesdb.insertValuesEntry("TESTSTRING","Hello World.",false,false,"");
        valuesdb.insertValuesEntry("TESTINTEGER",99,false,false,"");
        valuesdb.close();
        double testdbl = valuesdb.getDoubleValue("TESTDouble");
        valuesdb.alterDoubleValue("TESTDouble",(double) 167);
        long testint = valuesdb.getLongValue("TESTINTEGER");
        valuesdb.alterLongValue("TESTINTEGER",testint + 100);
        String teststring = valuesdb.getStringValue("TESTSTRING");
        valuesdb.alterStringValue("TESTSTRING","Goodbye World");
        */


        valuesdb.enableDismissedRules();
        RuleSuggestion.checkRuleSuggestions(this, false);

    }

    //==============================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onCreateOptionsMenu",developermode);
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usersettingsmenu, menu);
        return true;
    }

    //==============================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onOptionsItemSelected",developermode);

        switch (menuItem.getItemId()) {
            case R.id.usersettings_menu: {
                Intent intent = new Intent(this,UserSettings.class);
                startActivity(intent);
                break;
            }
            case R.id.dataoptions_menu: {
                Intent intent = new Intent(this,MainDataHandlingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.forcesuggestions:
                RuleSuggestion.checkRuleSuggestions(this, true);
                break;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //==============================================================================================
    @Override
    protected  void onResume() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onResume",developermode);
        super.onResume();
        getSettings();
        handleStats();
        selectButtons();
        resumestate = true;
    }

    //==============================================================================================
    @Override
    protected void onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onDestroy",developermode);
        //SQLiteStudioService.instance().stop();
        super.onDestroy();
        shopperdb.close();
    }

    //==============================================================================================
    // retrieve settings from sharedpreferences
    // developer mode boolean if on turns on extra logging and addtitional main display options
    // helpoffmode if on turns off help displays
    private void getSettings() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"getSettings",developermode);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        developermode = sharedPreferences.getBoolean(getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sharedPreferences.getBoolean(getString(R.string.sharedpreferencekey_showhelpmode),false);
        rulesuggestion = sharedPreferences.getBoolean(getString(R.string.sharedpreferencekey_allowrulesuggest),true);
        rulesuggestionfrequency =
                Integer.parseInt(
                        sharedPreferences.getString(getResources().getString(
                                R.string.sharedpreferencekey_rulesuggestfrequency),
                                "30"
                        )
                );
    }

    //==============================================================================================
    // Select Buttons that are to be dissplayed
    // Displays buttons relevant to the current state/context i.e. DB state and developermode
    // e.g. if no stores or products then only Stores and Products buttons as other information is
    // dependany up stores or products (and so on as per comments)
    // developer mode is independant of stores/products etc if true then display DATA and SCHEMA
    // buttons else display neither.
    private void selectButtons() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"selectButtons",developermode);

        // Always show Stores Button
        storesbutton.setVisibility(View.VISIBLE);
        if(!helpoffmode) {
            storeshelpbutton.setVisibility(View.GONE);
        } else {
            storeshelpbutton.setVisibility(View.GONE);
        }

        // Only show Aisles Button if Shops exist
        if(shopcount < 1) {
            aislesbutton.setVisibility(View.GONE);
            aisleshelpbutton.setVisibility(View.GONE);
        } else {
            aislesbutton.setVisibility(View.VISIBLE);
            if(!helpoffmode) {
                aisleshelpbutton.setVisibility(View.GONE);
            } else {
                aisleshelpbutton.setVisibility(View.GONE);
            }

        }
        // Always show Products Button
        productsbutton.setVisibility(View.VISIBLE);
        if(!helpoffmode) {
            productshelpbutton.setVisibility(View.GONE);
        } else {
            productshelpbutton.setVisibility(View.GONE);
        }

        // Only Show ProductUsage (TO GET) and Rules buttons if products are in aisles
        // This requires products and aisles to exist
        if(productcount < 1  | productcount < 1 | aislecount < 1 | productsinaisles < 1) {
            productusagebutton.setVisibility(View.GONE);
            productusagehelpbutton.setVisibility(View.GONE);
            ruleaddbutton.setVisibility(View.GONE);
            ruleaddhelpbutton.setVisibility(View.GONE);
        } else {
            productusagebutton.setVisibility(View.VISIBLE);
            ruleaddbutton.setVisibility(View.VISIBLE);
            if(!helpoffmode) {
                productusagehelpbutton.setVisibility(View.GONE);
                ruleaddhelpbutton.setVisibility(View.GONE);
            } else {
                productusagehelpbutton.setVisibility(View.GONE);
                ruleaddhelpbutton.setVisibility(View.GONE);
            }
        }

        // Only show ShoppingList button if there are shops and there are products and there are
        // products in aisles.
        if(shoppinglistcount < 1 & rulecount < 1) {
            shoppinglistbutton.setVisibility(View.GONE);
            shoppinglisthelpbutton.setVisibility(View.GONE);

        } else {
            shoppinglistbutton.setVisibility(View.VISIBLE);
            if(!helpoffmode) {
                shoppinglisthelpbutton.setVisibility(View.GONE);
            } else {
                shoppinglisthelpbutton.setVisibility(View.GONE);
            }
        }

        // Only Show Data (Database data) and Schema (Database Schema) buttons if developer
        // option/preference is checked.
        if(!developermode) {
            dbdatabutton.setVisibility(View.GONE);
            dbdatahelpbutton.setVisibility(View.GONE);
            dbschemabutton.setVisibility(View.GONE);
            dbschemahelpbutton.setVisibility(View.GONE);
        } else {
            dbdatabutton.setVisibility(View.VISIBLE);
            dbschemabutton.setVisibility(View.VISIBLE);
            if(!helpoffmode) {
                dbdatahelpbutton.setVisibility(View.GONE);
                dbschemahelpbutton.setVisibility(View.GONE);
            } else {
                dbdatahelpbutton.setVisibility(View.GONE);
                dbschemahelpbutton.setVisibility(View.GONE);
            }
        }
    }

    //==============================================================================================
    // Get statistics # of shops, aisles, products, products in aisles, rules and shopping list entries.
    // Also show date and time and then timestamp. Only shown in developer mode.
    private void handleStats() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"handleStats",developermode);

        shopcount = shopperdb.numberOfShops();
        aislecount = shopperdb.numberOfAisles();
        productcount = shopperdb.numberOfProducts();
        productsinaisles = shopperdb.numberOfProductUsages();
        rulecount = shopperdb.numberOfRules();
        shoppinglistcount = shopperdb.numberofShoppingListEntries();

        if(!developermode) {
            stats.setText("");
            return;
        }

        Date date = new Date();
        String now = date.toString();
        long now_as_millisecs = date.getTime();
        String statstr = ("Number of Shops=" + shopcount
                + "\nNumber of Aisles=" + aislecount
                + "\nNumber of Products=" + productcount
                + "\nNumber of Products in Aisles=" + productsinaisles
                + "\nNumber of Rules Setup=" + rulecount
                + "\nNumber of Shopping List Entries=" + shoppinglistcount
                + "\n\n" + now
                + "\n " + now_as_millisecs);
        stats.setText(statstr);
    }

    //==============================================================================================
    // Do Initialisation checks -
    // expand database (add any new tables and/or columns)
    // if no stores(shops) then invoke store add
    private void initialisationChecks() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"initialisationChecks",developermode);

        ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
        shopperdb.onExpand();

        if(shopcount < 1 ) {
            Intent intent = new Intent(this, ShopAddActivity.class);
            intent.putExtra("Caller",THIS_ACTIVITY);
            startActivity(intent);
        }
        shopperdb.close();
    }

    //==============================================================================================
    // Button Clicks Handled Here note onClick instigated via layout
    public void buttonClicked(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"buttonClicked",developermode);

        switch (view.getId()) {
            case R.id.am_stores_button:
                Intent intent = new Intent(this,ShopListByCursorActivity.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_aisles_button:
                intent = new Intent(this,AisleListByCursorActivity.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_produce_button:
                intent = new Intent(this,ProductListByCursorActivity.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_toget_button:
                intent = new Intent(this,AddPurchasableProductsToShopList.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_data_button:
                intent = new Intent(this,Database_Inspector_Activity.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_schema_button:
                intent = new Intent(this,DatabaseSchema.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_shop_button:
                intent = new Intent(this,ShoppingListActivity.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            case R.id.am_ruleadd_button:
                intent = new Intent(this,RuleAddEditList.class);
                intent.putExtra("Caller",THIS_ACTIVITY);
                intent.putExtra("DEVELOPERMODE",developermode);
                startActivity(intent);
                break;
            // Note redundant as removed from layout
            case R.id.am_storeshelp_button:
                break;
            case R.id.am_aisleshelp_button:
                break;
            case R.id.am_producehelp_button:
                break;
            case R.id.am_togethelp_button:
                break;
            case R.id.am_datahelp_button:
                break;
            case R.id.am_schemahelp_button:
                break;
            case R.id.am_shophelp_button:
                break;
            case R.id.am_ruleaddhelp_button:
                break;
            default:
                break;
        }
    }

    // Check if Database exists
    public boolean doesDatabaseExist(Context context, String dbName) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"doesDatabaseExist",developermode);
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    //----------------------------------------------------------------------------------------------
    // Unimplemented/Unused methods
    @Override
    public void onStart() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onStart",developermode);
        super.onStart();
    }
    @Override
    public void onStop() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"onStop",developermode);
        super.onStop();
    }
}