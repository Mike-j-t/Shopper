package mjt.shopper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ShopAddActivity extends AppCompatActivity {

    public final static String THIS_ACTIVITY = "ShopAddActivity";
    public boolean devmode;
    public boolean helpoffmode;
    public final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    // Variables to store shops table offsets as obtained via the defined column names by
    // call to setShopsOffsets (shops_shopid_offset set -1 to act as notdone flag )
    public static int shops_shopid_offset = -1;
    public static int shops_shopname_offset;
    public static int shops_shoporder_offset;
    public static int shops_shopstreet_offset;
    public static int shops_shopcity_offset;
    public static int shops_shopstate_offset;
    public static int shops_shopphone_offset;
    public static int shops_shopnotes_offset;

    public int mode = 0;
    public ListView shoplist;
    public Cursor shoplistcsr;
    public ShopsCursorAdapter shoplistcsradapter;
    public boolean developermode;
    public String caller;
    public EditText storename_input;
    public EditText storeorder_input;
    public EditText storestreet_input;
    public EditText storecity_input;
    public EditText storestate_input;
    public EditText storephone_input;
    public EditText storenotes_input;
    public String storename;
    public String storeorder;
    public String storestreet;
    public String storecity;
    public String storestate;
    public String storephone;
    public String storenotes;
    public LinearLayout shopaddeditlayout;
    public String storelistsortorder = Constants.STORELISTORDER_BY_STORE;
    //TODO could add options to edit or delete shop from current list??????? not important

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_add);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        shopaddeditlayout = (LinearLayout) findViewById(R.id.shopaddeedit_help_layout);
        if(!helpoffmode) {
            shopaddeditlayout.setVisibility(View.VISIBLE);
        } else {
            shopaddeditlayout.setVisibility(View.GONE);
        }

        // Get data passed via intent extras
        developermode = getIntent().getBooleanExtra(getString(R.string.sharedpreferencekey_developermode), false);
        caller = getIntent().getStringExtra(getString(R.string.activitycaller));
        storename_input = (EditText) findViewById(R.id.ase_storename_input);
        storeorder_input = (EditText) findViewById(R.id.ase_storeorder_input);
        storestreet_input = (EditText) findViewById(R.id.ase_storestreet_input);
        storecity_input = (EditText) findViewById(R.id.ase_storecity_input);
        storestate_input = (EditText) findViewById(R.id.ase_storestate_input);
        storephone_input = (EditText) findViewById(R.id.ase_storephone_input);
        storenotes_input = (EditText) findViewById(R.id.ase_storenotes_input);
        // Get View id's
        shoplist = (ListView) findViewById(R.id.ase_shoplist);
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        setShopsOffsets(shoplistcsr);
        shoplistcsradapter = new ShopsCursorAdapter(this,shoplistcsr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        shoplist.setAdapter(shoplistcsradapter);

        // Check Intent Extras to detremine what activity called this activity and thus
        // what processing is to be undertaken
        // Options are:
        // mode=0 - Add from MainActivity (Initialisation determined that a shop should be added as there weren't any)
        // mode=0 - Add from ShopListByCursorActivity (Add a shop vis Add button)
        // mode=10 - Update from ShoplistByCursorActivityUpdate (Touched a listed shop)
        //String caller = getIntent().getStringExtra("Caller");
        if(getIntent().getStringExtra("Caller").equals("ShopListByCursorActivityUpdate")) {
            storename_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storename)));
            storeorder_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storeorder)));
            storestreet_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storestreet)));
            storecity_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storecity)));
            storestate_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storestate)));
            storephone_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storephone)));
            storenotes_input.setText(getIntent().getStringExtra(getString(R.string.intentkey_storenotes)));
            mode = 10;
            setTitle(getResources().getString(R.string.title_activity_shop_edit));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shoplistcsr.close();
        shopperdb.close();
    }

    public void saveClicked(View view) {
        storeorder = "1000";

        storename = storename_input.getText().toString();
        storeorder = storeorder_input.getText().toString();
        storestreet = storestreet_input.getText().toString();
        storecity = storecity_input.getText().toString();
        storestate = storestate_input.getText().toString();
        storephone = storephone_input.getText().toString();
        storenotes = storenotes_input.getText().toString();

        // Validation of shopname, required if not given do not save and position cursor to the shop name.
        if(storename.isEmpty() | storename.length() < 1) {
            AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
            okdialog.setTitle(getString(R.string.shopnonametitle));
            okdialog.setMessage(getString(R.string.shopnonametext001));
            okdialog.setCancelable(true);
            okdialog.setPositiveButton("Continue",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            okdialog.create();
            okdialog.show();

            //Toast.makeText(this,"Shop Name is Required. Shop was not added. Please input a Shop Name.",Toast.LENGTH_LONG).show();
            storename_input.requestFocus();
            findViewById(R.id.ase_storename_input).requestFocus();
        } else {

            // Validattion of shoporder, required if not given then default value is used.
            if(storeorder.isEmpty() | storeorder.length() < 1 ) {
                storeorder = "1000";
                Toast.makeText(this, "Store order was not given. Using the default value of " + storeorder + ". Note! this is just informing you; there is no error.", Toast.LENGTH_SHORT).show();
            }

            // Add (ie an add shop button was clicked). Use database insert and clear input data.
            // Additionally prompt for the addition of Aisles (TODO as below)
            if (mode == 0) {
                shopperdb.insertShop(storename, storeorder, storestreet, storecity, storestate, storephone, storenotes);
                final long lastshop = shopperdb.getLastShopId();
                shopperdb.insertAisle("Default Aisle",lastshop,"1000");
                Toast.makeText(this, "Store " + storename + " was Added. An Aisle called Default Aisle was also added.", Toast.LENGTH_LONG).show();

                findViewById(R.id.asbtn02).setVisibility(View.VISIBLE);
                findViewById(R.id.ase_storename_input).requestFocus();
                ((EditText) findViewById(R.id.ase_storename_input)).setText("");
                ((EditText) findViewById(R.id.ase_storeorder_input)).setText("");
                ((EditText) findViewById(R.id.ase_storestreet_input)).setText("");
                ((EditText) findViewById(R.id.ase_storecity_input)).setText("");
                ((EditText) findViewById(R.id.ase_storestate_input)).setText("");
                ((EditText) findViewById(R.id.ase_storephone_input)).setText("");
                ((EditText) findViewById(R.id.ase_storenotes_input)).setText("");
            }

            // Update (ie Shop was clicked on from the Shop list). Use database update and don't clear the input
            if (mode == 10) {
                shopperdb.updateShop(getIntent().getStringExtra("ShopID"), storename, storeorder, storestreet, storecity, storestate, storephone, storenotes);
                Toast.makeText(this, "Store " + storename + " Updated.",Toast.LENGTH_SHORT).show();
            }
            // refresh the list of current shops
            shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
            shoplistcsradapter.swapCursor(shoplistcsr);
        }
    }
    public void doneAdding(View view) {
        this.finish();
    }

    public void orderByStore(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_STORE;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByOrder(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_ORDER;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByCity(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_CITY;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByStreet(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_STREET;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByState(View view) {
        storelistsortorder = Constants.STORELIST_ORDER_BY_STATE;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByPhone(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_PHONE;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }
    public void orderByNotes(View view) {
        storelistsortorder = Constants.STORELISTORDER_BY_NOTES;
        shoplistcsr = shopperdb.getShopsAsCursor(storelistsortorder);
        shoplistcsradapter.swapCursor(shoplistcsr);
    }

    // Set Shops Table query offsets into returned cursor, if not already set
    public void setShopsOffsets(Cursor cursor) {
        // If not -1 then already done
        if(shops_shopid_offset != -1) {
            return;
        }
        shops_shopid_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ID);
        shops_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        shops_shoporder_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_ORDER);
        shops_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
        shops_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        shops_shopstate_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STATE);
        shops_shopphone_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_PHONE);
        shops_shopnotes_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NOTES);
    }
}