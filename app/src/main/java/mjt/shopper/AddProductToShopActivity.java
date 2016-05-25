package mjt.shopper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mike092015 on 11/02/2016.
 * AddProductToShopActivity
 * Allows the addition of a product or products to an Aisle(s) within a shop.
 * Uses a spinner to select a shop
 * Uses a spinner to select an Ailse (no Aisles then nothing to be or can be selected)
 * Uses a spinner to select a Product (no Aisles then no products will be available)
 * Has two edittext's to set the cost(price) and order of the product
 * ADD button will attempt to add according to the shop/aisle/product/cost and order
 * Uses a listview to display the current items in the shop/aisle selection combination
 *      clicking an item in the listview allows the product to be edited BUT restricted to
 *          the specific/unique details for this shop/aisle/product ie the cost can be changed,
 *          the product's name cannot be changed (as the product can be used elsewhere).
 *      long clicking allows the product to be removed from the aisle (not removal of the
 *          product)
 * Can be invoked from :
 *      The Shoplist by clicking on a listed shop and then selecting STOCK SHOP - <shopname>
 *          dialog option.
 *      The Aislelist by clicking on a listed aisle (shop spinner to select shop) and then
 *           selecting STOCK Aisle dialog option.
 * Note!!! onResume handles resfresh of the
 */
public class AddProductToShopActivity extends AppCompatActivity {
    private final static String THIS_ACTIVITY = "AddProductToShopActivity";
    public boolean devmode;
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this, null, null, 1);
    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_PRODUCTUSAGEEDIT = 1;
    public final static String[] RESUMESTATE_DESCRIPTIONS = {"Nothing","ProductUsageEdit"};
    public int resume_state = RESUMESTATE_NOTHING;

    public long currentshopid = -1;
    public Spinner current_shoplistspinner;
    public ShopListSpinnerAdapter current_shoplistspinneradapter;
    public ShopsCursorAdapter current_shoplistcursoradapter;
    public Cursor currentshoplistcursor;

    public long currentaisleid = -1;
    public Spinner current_aislelistspinner;
    public AisleListSpinnerAdapter current_aislelistspinneradapter;
    public AislesCursorAdapter current_aislelistcursoradapater;
    public Cursor currentaislelistcursor;

    public long currentproductid = -1;
    public Spinner current_productlistspinner;
    public ProductListSpinnerAdapter current_productlistspinneradapter;
    public ProductsCursorAdapter current_productscursoradapter;
    public Cursor currentproductlistcursor;
    
    public long currentproductperaisleaisleid = -1;
    public long currentproductperaisleproductid = -1;
    public ListView current_productsperaislelistview;
    public ProductsPerAisleCursorAdapter current_productsperaislecursoradapter;
    public Cursor currentproductsperaisleecursor;
    public String productsperaislesortorder = Constants.PRODUCTSPERAISLELISTORDER_BY_PRODUCT;
    public String productselectionstr = "";

    public EditText priceinput;
    public EditText orderinput;
    public EditText productselectioninput;
    public TextView addbutton;

    protected void onResume() {
        super.onResume();
        if(devmode) {
            Log.i(Constants.LOG, " onResume invoked in " + THIS_ACTIVITY + "-  Current State=" + resume_state + "-" + RESUMESTATE_DESCRIPTIONS[resume_state]);
        }
        switch (resume_state) {
            case RESUMESTATE_NOTHING: {
                break;
            }
            case RESUMESTATE_PRODUCTUSAGEEDIT: {
                // Get cursor from Database
                currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
                // Swap to this cursor
                current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_shop);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);

        //==========================================================================================
        // Setup Shoplist Spinner
        if(devmode) {
            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                    "-STARTED - Intent SHOPID=" + getIntent().getLongExtra("SHOPID",-1));
        }
        final String caller = getIntent().getStringExtra("Caller");
        long passedshopid = getIntent().getLongExtra("SHOPID",-1);
        if(devmode) {
            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER " +
                    "-passed variables extracted - Caller=" + caller + " Intent SHOPID=" + passedshopid );
        }

        priceinput = (EditText) findViewById(R.id.productusageedit_priceinput);
        orderinput = (EditText) findViewById(R.id.productusageedit_orderinput);
        productselectioninput = (EditText) findViewById(R.id.productusageedit_selection_input);
        addbutton = (TextView) findViewById(R.id.productusageedit_add);

        // Shoplist Spinner Creation
        currentshoplistcursor = shopperdb.getShopsAsCursor("");
        current_shoplistspinner = (Spinner) findViewById(R.id.productusageedit_storeselector);
        current_shoplistspinneradapter = new ShopListSpinnerAdapter(this,currentshoplistcursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        current_shoplistspinner.setAdapter(current_shoplistspinneradapter);
        if(devmode) {
            Log.d(Constants.LOG, "ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                    "-Spinner added - Intent SHOPID=" + getIntent().getLongExtra("SHOPID", -1));
        }

        // Created now determine the shopid of the selected item. However, if shopid is passed,
        // we first need to set the selection to that passed shopid.
        // To do that requires traversal of the database cursor to find the cursro row that
        // contains that shopid. The selection can then be set to the position as
        // the cursor and spinner positions match(???? can they differ, not found so as yet).
        // Note!!! If passedid = -1 then shopid has not been successfully passed vie the Intent.
        // Note!!! Assumption is that passed shopid, will exist. As such handling an a shopid
        // that doesn't exist is ignored.
        if(passedshopid > -1) {
            currentshoplistcursor.moveToPosition(-1); // Move to cursor to initialised position, otherwsie might not catch all rows.
            boolean shopid_set = false;
            while(currentshoplistcursor.moveToNext()) {
                if(devmode) {
                    Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                            "-Processing cursor to find passed shopid(" + passedshopid +
                            ") within the cursor. Cursor row=" + currentshoplistcursor.getPosition());
                }
                if(currentshoplistcursor.getLong(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX) == passedshopid ) {
                    if(devmode) {
                        Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                                "-Matched passed shopid(" + passedshopid + "). Cursor row=" + currentshoplistcursor.getPosition());
                    }
                    shopid_set = true;
                    current_shoplistspinner.setSelection(currentshoplistcursor.getPosition());
                    currentshopid = passedshopid;
                    break;
                }
            }
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                        "-Processing cursor finished. Match Status =" + shopid_set);
            }

        } else {
            // Passed shopid was not greater than -1 (should always be 1 or greater if passed,
            // else -1 indicating that it was not passed.
            // If not passed then set the shop id to subsequently be used to the current selected item.
            // Note!!! Should always be a shop and therefore a shopid as a shop needs to exist to
            // invoke this, however this is invoked.
            currentshopid = currentshoplistcursor.getLong(current_shoplistspinner.getSelectedItemPosition());
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP SHOPLIST SPINNER" +
                        "-No SHOPID in Intent; cursor not processed. Shopid set as Cursor Position " +
                        currentshoplistcursor.getPosition() + " Shopid=" +currentshopid);
            }

        }
        if(devmode) {
            Log.d(Constants.LOG,"Activity=" + THIS_ACTIVITY + " Section=SETUP SHOPLIST SPINNER" +
                    "-COMPLETED SHOPID==>" + currentshopid + "<==.");
        }


        // Set Spinner's onItemSelectedListener ie to act when a shop is selected.
        // Note!!! run on post, as a runnable, so that initial onItemSelected event
        // (at initialisation) is not captured
        current_shoplistspinner.post(new Runnable() {
            @Override
            public void run() {
                current_shoplistspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentshoplistcursor.moveToPosition(position);
                        currentshopid = currentshoplistcursor.getLong(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX);
                        if(devmode) {
                            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: Running - SHOPLIST OnItemSelectedLIstener" +
                                    "- SHOPID Extracted=" + currentshopid);
                        }
                        currentaislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid,"");
                        current_aislelistspinneradapter.swapCursor(currentaislelistcursor);
                        // if no aisles for this shop then don't show any products
                        // TODO dialog to allow Aisle Add???
                        if(devmode) {
                            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: Running - SHOPLIST OnItemSelectedListener" +
                                    "- NEW AISLE COUNT=" + currentaislelistcursor.getCount());
                        }
                        if(currentaislelistcursor.getCount() < 1) {
                            currentproductlistcursor = shopperdb.getNoProductsAsCursor();
                            current_productlistspinneradapter.swapCursor(currentproductlistcursor);
                            // Also need to clear products per aisle as no ailse so no products
                            // So use -1 as the aisleid when getting new cursor
                            currentproductsperaisleecursor = shopperdb.getProductsperAisle(-1,productsperaislesortorder);
                            current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
                            // Disable the ADD button as cannot add if no aisle or prodcuts
                            addbutton.setVisibility(View.INVISIBLE);
                        } else {
                            currentproductlistcursor = shopperdb.getProductsAsCursor("",productselectionstr);
                            current_productlistspinneradapter.swapCursor(currentproductlistcursor);
                            addbutton.setVisibility(View.VISIBLE);
                            //Note!! as aislelist spinner has a new selecteditem it's listener will
                            //handle products per aisle refresh (unlike if no aisle)
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        //==========================================================================================
        // Setup AilseList Spinner but first check that the shopid is valid
        if(currentshopid <= -1) {
            String errmsg = "ERROR - No Valid SHOP ID. Shopid (" + currentshopid + ") Should be positive. " +
                    "\n\tA negative number indicates a program logic error which requires developer investigation." +
                    "\n\tPlease notify the developres of this issue.";
            if(devmode) {
                Log.e(Constants.LOG,errmsg);
            }
            Toast.makeText(this,errmsg,Toast.LENGTH_LONG).show();
            finish();
        }
        // Aislelist Spinner Creation
        long passedaisleid = getIntent().getLongExtra("AISLEID",-1);
        currentaislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid,"");
        current_aislelistspinner = (Spinner) findViewById(R.id.productusageedit_aisleselector);
        current_aislelistspinneradapter = new AisleListSpinnerAdapter(this,currentaislelistcursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        current_aislelistspinner.setAdapter(current_aislelistspinneradapter);
        if(devmode) {
            Log.d(Constants.LOG, "ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP AISLELIST SPINNER" +
                    "-Spinner added - Intent AISLEID=" + getIntent().getLongExtra("AISLEID", -1));
        }
        // Aislelist Spinner created. Howevever, if an aisleid has been passed (1-nnn, ie not -1,
        // should never be 0) then set the aisle as the selected aisle.
        // To set the aisle need to find which cursor row, and therefore spinner Item,
        // contains the aisleid.
        if(passedaisleid > -1) {
            // Ensure that we start with the cursor at it's initialised position
            // (setting adpater may/will move the cursor)
            currentaislelistcursor.moveToPosition(-1);
            boolean aisleid_set = false; // Flag not really used
            // Lopp through/traverse the cursor
            while(currentaislelistcursor.moveToNext()) {
                if(devmode) {
                    Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP AISLELIST SPINNER" +
                            "-Processing cursor to find passed aisleid(" + passedaisleid +
                            ") within the cursor. Cursor row=" + currentaislelistcursor.getPosition());
                }
                // Check for match with this row
                if(currentaislelistcursor.getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX) == passedaisleid ) {
                    if(devmode) {
                        Log.d(Constants.LOG, "ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP AISLELIST SPINNER" +
                                "-Matched passed aisleid(" + passedaisleid + "). Cursor row=" + currentaislelistcursor.getPosition());
                    }
                    // Set the Spinner's selected Item according to the cursor's current position.
                    current_aislelistspinner.setSelection(currentaislelistcursor.getPosition());
                    // Make sure that the currentaisleid (subsequently used, too allow passed
                    // aisleid to be referenced if needed) reflects the passed aisleid
                    currentaisleid = passedaisleid;
                    aisleid_set = true; // set flag
                    break;
                }
            }
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP AISLELIST SPINNER" +
                        "-Processing cursor finished. Match Status =" + aisleid_set);
            }
        } else {
            // Passed aisleid was not greater than -1 (should always be 1 or greater if passed,
            // else -1 indicating that it was not passed.
            // If not passed then set the ailse id to subsequently be used to the current selected item.
            // Note!!! Should always be an aisle and therefore an aisleid as an aisle needs to
            // exist to invoke this, however this is invoked.
            currentaisleid = currentaislelistcursor.getLong(current_aislelistspinner.getSelectedItemPosition());
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP AILSELIST SPINNER" +
                        "-No AILSEID in Intent; cursor not processed. Aisleid set as Cursor Position " +
                        currentaislelistcursor.getPosition() + " Aisleid=" +currentaisleid);
            }
        }
        // Set Spinner's onItemSelectedListener ie to act when an aisle is selected.
        // Note!!! run on post, as a runnable, so that initial onItemSelected event (at initialisation) is not captured.
        current_aislelistspinner.post(new Runnable() {
            @Override
            public void run() {
                current_aislelistspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentaislelistcursor.moveToPosition(position);
                        currentaisleid = currentaislelistcursor.getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX);
                        if(devmode) {
                            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: Running - AISLELIST OnItemSelectedLIstener" +
                                    "- AILSEID Extracted=" + currentaisleid);
                        }
                        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
                        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
                        current_productsperaislecursoradapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        //==========================================================================================
        // Setup ProductList Spinner
        // Should have a valid shopid
        if(currentshopid <= -1) {
            String errmsg = "ERROR - No Valid SHOP ID. Shopid (" + currentshopid + ") Should be positive. " +
                    "\n\tA negative number indicates a program logic error which" +
                    " requires developer investigation." +
                    "\n\tPlease notify the developres of this issue.";
            if(devmode) {
                Log.e(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate SETUP SHOPLIST SPINNER " + errmsg);
            }
            Toast.makeText(this,errmsg,Toast.LENGTH_LONG).show();
            finish();
        }
        // Should have a valid aisleid
        if(currentaisleid <- -1) {
            String errmsg = "ERROR - No Valid AILSE ID. Ailseid (" + currentaisleid + ") Should be positive. " +
                    "\n\tA negative number indicates a program logic error " +
                    "which requires developer investigation." +
                    "\n\tPlease notify the developres of this issue.";
            if(devmode) {
                Log.e(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate SETUP SHOPLIST SPINNER " + errmsg);
            }
            Toast.makeText(this,errmsg,Toast.LENGTH_LONG).show();
            finish();
        }
        long passedproductid = getIntent().getLongExtra("PRODUCTID",-1);
        // Create and populate Product List Spinner
        currentproductlistcursor = shopperdb.getProductsAsCursor("",productselectionstr);
        current_productlistspinner = (Spinner) findViewById(R.id.productuseageedit_productselector);
        current_productlistspinneradapter = new ProductListSpinnerAdapter(this,currentproductlistcursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        current_productlistspinner.setAdapter(current_productlistspinneradapter);
        if(devmode) {
            Log.d(Constants.LOG, "ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP PRODUCTLIST SPINNER" +
                    "-Spinner added - Intent PRODUCTID=" + getIntent().getLongExtra("PRODUCTID", -1));
        }

        // Productlist Spinner created. Howevever, if a productid has been passed (1-nnn,
        // ie not -1, should never be 0) then set the product as the selected product.
        // To set the product need to find which cursor row, and therefore spinner Item, contains the productid.
        if(passedproductid > -1) {
            currentproductlistcursor.moveToPosition(-1);
            boolean productid_set = false;
            while(currentproductlistcursor.moveToNext()) {
                if(devmode) {
                    Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP PRODUCTLIST SPINNER" +
                            "-Processing cursor to find passed productid(" + passedproductid +
                            ") within the cursor. Cursor row=" + currentproductlistcursor.getPosition());
                }
                // Check for match with this row
                if(currentproductlistcursor.getLong(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX) == passedproductid) {
                    if(devmode) {
                        Log.d(Constants.LOG, "ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP PRODUCTLIST SPINNER" +
                                "-Matched passed productid(" + passedproductid + "). Cursor row=" + currentproductlistcursor.getPosition());
                    }
                    // Set the Spinner's selected Item according to the cursor's current position.
                    current_productlistspinner.setSelection(currentproductlistcursor.getPosition());
                    // Make sure that the currentaisleid (subsequently used, too allow passed
                    // aislid to be referenced if needed) reflects the passed aisleid
                    currentproductid = passedproductid;
                    productid_set = true;
                    break;
                }

            }
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP PRODUCTLIST SPINNER" +
                        "-Processing cursor finished. Match Status =" + productid_set);
            }

        } else {
            // Passed productid was not greater than -1 (should always be 1 or greater if passed,
            // else -1 indicating that it was not passed.
            // If not passed then set the product id to subsequently be used to the current selected item.
            // Note!!! Will not be an product id if invoked via shop or aisle list.
            currentproductid = currentproductlistcursor.getLong(current_productlistspinner.getSelectedItemPosition());
            if(devmode) {
                Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: onCreate - SETUP PRODUCTLIST SPINNER" +
                        "-No PRODUCTID in Intent; cursor not processed. Productid set as Cursor Position " +
                        currentproductlistcursor.getPosition() + " Productid=" +currentproductid);
            }
        }
        // Set Spinner's onItemSelectedListener ie to act when a product is selected.
        // Note!!! run on post, as a runnable, so that initial onItemSelected event
        // (at initialisation) is not captured.
        current_productlistspinner.post(new Runnable() {
            @Override
            public void run() {
                current_productlistspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentproductlistcursor.moveToPosition(position);
                        currentproductid = currentproductlistcursor.getLong(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX);
                        if(devmode) {
                            Log.d(Constants.LOG,"ACTIVITY: " + THIS_ACTIVITY + " SECTION: Running - PRODUCTLIST OnItemSelectedLIstener" +
                                    "- PRODUCTID Extracted=" + currentproductid);
                        }
                        //TODO  Need to refresh Product List due to changed Aisle (Note check for conflicts/doing this twice if also coded in Aisle)
                        //TODO  Will/May also need to refresh prodcuts per aisle
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        //==========================================================================================
        // Handle product selection (reduces/limits) products shown by spinner
        productselectioninput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productselectionstr = productselectioninput.getText().toString();
                currentproductlistcursor = shopperdb.getProductsAsCursor("",productselectionstr);
                current_productlistspinneradapter.swapCursor(currentproductlistcursor);
                current_productlistspinner.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //==========================================================================================
        // Create and populate Products per Aisle List
        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
        current_productsperaislelistview = (ListView) findViewById(R.id.productusageedit_currentproductslist);
        current_productsperaislecursoradapter = new ProductsPerAisleCursorAdapter(this,currentproductsperaisleecursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        current_productsperaislelistview.setAdapter(current_productsperaislecursoradapter);

        // Set Productsperaisle ListView onItemclick Listener to allow a product usage entry to be edited
        current_productsperaislelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resume_state = RESUMESTATE_PRODUCTUSAGEEDIT;
                currentproductsperaisleecursor.moveToPosition(position);
                AlertDialog.Builder okdialog = new AlertDialog.Builder(current_productsperaislelistview.getContext());
                okdialog.setTitle("Confirm Edit Product in Aisle");
                okdialog.setMessage("Do you really wish to Edit Product :-\n\t"
                        + currentproductsperaisleecursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX)
                        + " in Aisle " + currentaislelistcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                okdialog.setCancelable(true);

                okdialog.setNegativeButton(R.string.standardcanceltext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                okdialog.setPositiveButton(R.string.standardedittext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(current_productsperaislelistview.getContext(), ProductUsageEdit.class);
                        intent.putExtra("CALLER", THIS_ACTIVITY);
                        intent.putExtra("CALLERCALLER", caller);
                        intent.putExtra("AISLENAME", currentaislelistcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                        intent.putExtra("AISLEID", currentaisleid);
                        intent.putExtra("SHOPID", currentshopid);
                        intent.putExtra("SHOPNAME", currentshoplistcursor.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
                        intent.putExtra("PRODUCTNAME", currentproductsperaisleecursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX));
                        intent.putExtra("PRODUCTID", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX));
                        //Note! as the tables are joined need to offset the second (productusage)
                        // relative to the first (products) table in the join
                        int productusageoffsetintocursor = ShopperDBHelper.PRODUCTS_COLUMN_NOTES_INDEX + 1;
                        intent.putExtra("PRODUCTPRICE", currentproductsperaisleecursor.getFloat(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST_INDEX + productusageoffsetintocursor));
                        intent.putExtra("PRODUCTORDER", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER_INDEX + productusageoffsetintocursor));
                        intent.putExtra("PRODUCTPURCHASECOUNT", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTUSAGE_COLUMN_BUYCOUNT_INDEX + productusageoffsetintocursor));
                        intent.putExtra("PRODUCTFIRSTPURCHASED", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTUSAGE_COLUMN_FIRSTBUYDATE_INDEX + productusageoffsetintocursor));
                        intent.putExtra("PRODUCTLASTPURCHASED", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTUSAGE_COLUMN_LASTBUYDATE_INDEX + productusageoffsetintocursor));
                        intent.putExtra("PRODUCTMINCOST", currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTUSAGE_COLUMN_MINCOST_INDEX + productusageoffsetintocursor));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                okdialog.show();
            }
        });
        current_productsperaislelistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentaislelistcursor.moveToPosition(current_aislelistspinner.getSelectedItemPosition());
                String aislename = currentaislelistcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX);
                currentproductsperaisleecursor.moveToPosition(position);
                AlertDialog.Builder okdialog = new AlertDialog.Builder(current_productsperaislelistview.getContext());
                okdialog.setTitle(R.string.productconfirmdeletetitle);
                okdialog.setMessage(getString(R.string.productconfirmdeletetext001) +
                        currentproductsperaisleecursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX)
                        + " in Aisle " + aislename + " #=" + currentaisleid);
                okdialog.setCancelable(true);
                okdialog.setNegativeButton(R.string.standardcanceltext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                okdialog.setPositiveButton(R.string.standarddeletetext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopperdb.deleteSingleProductFromAisle(currentaisleid, currentproductsperaisleecursor.getLong(ShopperDBHelper.PRODUCTS_COLUMN_ID_INDEX));
                        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
                        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
                        dialog.cancel();
                    }
                });
                okdialog.show();
                return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentshoplistcursor.close();
        currentaislelistcursor.close();
        currentproductlistcursor.close();
        currentproductsperaisleecursor.close();
        shopperdb.close();
    }

    // Done Button = finish the activity;
    public void aaptsdone(View view) {
        this.finish();
    }


    //Refresh the product list
    private void refreshProductList(long shopid) {
        if(shopperdb.aislesPerShop(shopid) > 0) {
            currentproductlistcursor = shopperdb.getProductsAsCursor("",productselectionstr);
        } else {
            currentproductlistcursor = shopperdb.getNoProductsAsCursor();
        }
        current_productlistspinneradapter.swapCursor(currentproductlistcursor);
        refreshProductsInAisle(shopid);
    }

    // Refresh Products in the Aisle
    private void refreshProductsInAisle(long shopid) {
        if(shopperdb.aislesPerShop(shopid) > 0) {
            currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
            addbutton.setVisibility(View.VISIBLE);
        } else {
            currentproductsperaisleecursor = shopperdb.getProductsperAisle(-1,productsperaislesortorder);
            addbutton.setVisibility(View.INVISIBLE);
        }
        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
    }


    // Handle the Add button by adding a new entry in the product usage table
    public void addProductToAisle(View view) {

        // Get the Cost of the product
        float itemcost = 0f;
        try {
            itemcost = Float.valueOf(priceinput.getText().toString());
        } catch(NumberFormatException e) {
                Toast.makeText(view.getContext(), "Invalid Amount! You Input >>" + priceinput.getText().toString() +"<<. Please Try Again",Toast.LENGTH_LONG).show();
            priceinput.requestFocus();
            return;
        }

        // Get the Order of the product in the aisle
        String strorderinaisle = orderinput.getText().toString();
        Emsg validateorderinaisle = mjtUtils.validateInteger(strorderinaisle);
        int orderinaisle = 100;
        if(!validateorderinaisle.getErrorIndicator()) {
            orderinaisle = Integer.parseInt(strorderinaisle);
        }

        // Add the product to the aisle (ie add the row to the productusage table)
        boolean addedok = shopperdb.insertProductIntoAisle(currentaisleid, currentproductid, itemcost, orderinaisle);

        // Report on the status ie Aisle added or Aisle not added (assuming the latter is due to duplicate (product already added to that aisle)
        if(addedok) {
            Toast.makeText(view.getContext(), "Product " + currentproductlistcursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX) +
                    " Added to " + currentaislelistcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX),Toast.LENGTH_LONG).show();
            refreshProductsInAisle(currentshopid);
        } else {
            Toast.makeText(view.getContext(), "Product " + currentproductlistcursor.getString(ShopperDBHelper.PRODUCTS_COLUMN_NAME_INDEX) +
                    " NOT added to " + currentaislelistcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX)
                    + "; as it already exists in the aisle.", Toast.LENGTH_LONG).show();
        }
    }
    public void orderByProduct(View view) {
        productsperaislesortorder = Constants.PRODUCTSPERAISLELISTORDER_BY_PRODUCT;
        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
    }
    public void orderByCost(View view) {
        productsperaislesortorder = Constants.PRODUCTSPERAISLELISTORDER_BY_COST;
        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
    }
    public void orderByOrder(View view) {
        productsperaislesortorder = Constants.PRODUCTSPERAISLELISTORDER_BY_ORDER;
        currentproductsperaisleecursor = shopperdb.getProductsperAisle(currentaisleid,productsperaislesortorder);
        current_productsperaislecursoradapter.swapCursor(currentproductsperaisleecursor);
    }
}