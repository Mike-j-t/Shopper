package mjt.shopper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    private final static String THIS_ACTIVITY = "MainActivity";
    private SharedPreferences sharedPreferences;
    private boolean developermode = false;
    private boolean helpoffmode = false;


    public ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
    private int shopcount;
    private int aislecount;
    private int productcount;
    private int productsinaisles;
    private int rulecount;
    private int shoppinglistcount;
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
    private boolean devmode;

    //==============================================================================================
    //==============================================================================================
    protected void onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Shopper Application Starting", THIS_ACTIVITY, "onCreate", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        PreferenceManager.setDefaultValues(this, R.xml.usersettings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sharedPreferences.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode), false);
        helpoffmode = sharedPreferences.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode), false);
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Primary Activity Initialisation Complete", THIS_ACTIVITY, "onCreate",devmode);

        // Check to see if database exists
        if(!doesDatabaseExist(this,ShopperDBHelper.DATABASE_NAME)) {
            Toast.makeText(this,"Welcome to Shopper. Creating Shopper Database and underlying tables.",Toast.LENGTH_LONG).show();
            mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG, "Shopper Database will be created as it didn't exist", THIS_ACTIVITY, "onCreate",devmode);
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
        // Settings
        // Debug option note sharedpreferences are used
        valuesdb.insertValuesEntry(Constants.DEBUGFLAG,(long) 0,false,true,"Turn On Debugging Options");
        valuesdb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onCreateOptionsMenu",devmode);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usersettingsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onOptionsItemSelected",devmode);
        Intent intent = new Intent(this,UserSettings.class);
        startActivity(intent);
        return true;
    }

    //==============================================================================================
    @Override
    protected  void onResume() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onResume",devmode);
        super.onResume();
        getSettings();
        handleStats();
        selectButtons();
    }

    //==============================================================================================
    @Override
    protected void onDestroy() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onDestroy",devmode);
        //SQLiteStudioService.instance().stop();
        super.onDestroy();
        shopperdb.close();
    }
    private void getSettings() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"getSettings",devmode);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        developermode = sharedPreferences.getBoolean(getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sharedPreferences.getBoolean(getString(R.string.sharedpreferencekey_showhelpmode),false);
    }

    //==============================================================================================
    // Select Buttons that are to be dissplayed
    private void selectButtons() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"selectButtons",devmode);

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


        // Only Show ProductUsage button if products exist
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
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"handleStats",devmode);

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
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"initialisationChecks",devmode);

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
    // Button Clicks Handled Here
    public void buttonClicked(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"buttonClicked",devmode);

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
                intent = new Intent(this,AddPurchaseableProductsToShopList.class);
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

    @Override
    public void onStart() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onStart",devmode);
        super.onStart();
    }

    @Override
    public void onStop() {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"onStop",devmode);
        super.onStop();
    }

    public boolean doesDatabaseExist(Context context, String dbName) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",THIS_ACTIVITY,"doesDatabaseExist",devmode);
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}