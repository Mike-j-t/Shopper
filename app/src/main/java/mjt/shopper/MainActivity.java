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

//TODO Add ability to suggest Rule modifications???
//TODO Help display for Rule Suggestion and for Enabling disabled rules

public class MainActivity extends AppCompatActivity {

    // Activity/Shared preferences variables
    private final static String THIS_ACTIVITY = "MainActivity"; // Allows ActivityNames to be sent
    private SharedPreferences sharedPreferences;
    private boolean developermode = false;
    private boolean helpoffmode = false;
    private boolean rulesuggestion = true;
    private int rulesuggestionfrequency = 30;

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

    private MenuItem reviewdisabledrulesuggestions;

    //==============================================================================================
    //==============================================================================================
    protected void onCreate(Bundle savedInstanceState) {
        final String THIS_METHOD = "onCreate";
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Shopper Application Starting", THIS_ACTIVITY, THIS_METHOD, true);
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
        //this.invalidateOptionsMenu();


        //Setup AutoAdd Periods for dropdown selection
        //To add another repeat
        //Retrieve via getStringArrayListValue method
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_DAYS,true,false,"");
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_WEEKS,true,false,"");
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_FORTNIGHTS,true,false,"");
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_MONTHS,true,false,"");
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_QUARTERS,true,false,"");
        shopperdb.insertValuesEntry(Constants.RULEPERIODS,Constants.PERIOD_YEARS,true,false,"");
        shopperdb.insertValuesEntry(Constants.LASTRULESUGGESTION,(long)0,false,false,"");
        // Settings
        // Debug option note sharedpreferences are used
        shopperdb.insertValuesEntry(Constants.DEBUGFLAG,(long) 0,false,true,"Turn On Debugging Options");

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

        shopperdb.enableDismissedRules();
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

    //TODO look into checking whether RuleSuggestions should be enabled
    /**
     * Disable/Enable menu options depending upon states
     * @param menu
     * @return always true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem reviewdisabledsuggestions = menu.findItem(R.id.reviewdisabledsuggestions);
        MenuItem forcerulesuggestion = menu.findItem(R.id.forcesuggestions);

        /**
         * Enable Review Disabled Rule Suggestions only if some rules are disabled
         * Note really it's productusages that are disbabled
         */
        if(shopperdb.getDisabledRuleCount() > 0 ) {
            reviewdisabledsuggestions.setVisible(true);
        } else {
            reviewdisabledsuggestions.setVisible(false);
        }
        /**
         * Enable Force Rule Suggestions only if there are rules that can be suggested.
         */
        if(shopperdb.getSuggestedRulesCount() > 0 ) {
            forcerulesuggestion.setVisible(true);
        } else {
            forcerulesuggestion.setVisible(false);
        }
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
            case R.id.reviewdisabledsuggestions:
                new ReviewDisabledRuleSuggestions(this);
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
        //this.invalidateOptionsMenu();
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
        //Developer Mode - Allows Data and Schema buttons does additional log writing
        developermode = sharedPreferences.getBoolean(
                getString(
                        R.string.sharedpreferencekey_developermode
                ),
                false
        );
        // HelpOffMode - DIsable/Enable help displays
        helpoffmode = sharedPreferences.getBoolean(
                getString(
                        R.string.sharedpreferencekey_showhelpmode
                ),
                false
        );
        // Diable/Enable Regular Rule suggestions
        rulesuggestion = sharedPreferences.getBoolean(
                getString(
                        R.string.sharedpreferencekey_allowrulesuggest
                ),
                true
        );
        // Number of days between rule suggestions
        rulesuggestionfrequency =
                Integer.parseInt(
                        sharedPreferences.getString(getResources().getString(
                                R.string.sharedpreferencekey_rulesuggestfrequency),
                                "30"
                        )
                );
    }

    /**
     * Method - selectButtons - Turn buttons on/off according to various states
     * Stores Button is always available.
     * The Aisles button will only be available if at least 1 store exists
     * Products Button is always available.
     * To Get Button is only available when at least one product is assigned
     *      to an aisle. As such at least one aisle and one product exists and
     *      therefore that at least one shop exists.
     *
     */
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

        // Only show ShoppingList button if at least one shopping list row wxists
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

    /**
     * Set database row count display views
     * Also Date and Timestamps
     * Note! Data is only displayed when developer Mode is on.
     */
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

    /**
     * Method - initialisationChecks - invoke the onExpand method
     * The onExpand method checks the actual database against a
     *      proposed structure adding tables and columns if any
     *      are missing.
     * This is an alternative method to using the SQLIte onUpgrade.
     * Classes ShopperDBHelper.DBColumn, DBTable and DBDatabase,
     *      all in ShopperDBHelper.java, have additional information
     * Note! invoking the ShopAddActivity if no stores exists has
     *      been commented out as it may be confusing.
     */
    private void initialisationChecks() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,"initialisationChecks",developermode);

        ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
        shopperdb.onExpand();

        /*
        if(shopcount < 1 ) {
            Intent intent = new Intent(this, ShopAddActivity.class);
            intent.putExtra("Caller",THIS_ACTIVITY);
            startActivity(intent);
        }
        */
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

    /**
     * method doesDatabaseExist - Checks if the database exists
     * @param context the context
     * @param dbName The database name to check
     * @return true if the database file exists, false if not
     */
    public boolean doesDatabaseExist(Context context, String dbName) {
        String THIS_METHOD = "doesDatabaseExist";
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                THIS_ACTIVITY,THIS_METHOD,developermode);
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