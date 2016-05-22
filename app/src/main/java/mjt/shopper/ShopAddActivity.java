package mjt.shopper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    public int mode = 0;
    public final Context context = this.context;
    public boolean yesnoresult = false;
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

        if(shopperdb.numberOfShops() < 1 ) {
            AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
            okdialog.setTitle(getString(R.string.shopnonetitle));
            okdialog.setMessage(getString(R.string.noshopsmessage));
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
            findViewById(R.id.asbtn02).setVisibility(View.GONE);
        } else {
            findViewById(R.id.asbtn02).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shoplistcsr.close();
        shopperdb.close();
    }

    public void saveClicked(View view) {

        storename = storename_input.getText().toString();
        storeorder = storeorder_input.getText().toString();
        storestreet = storestreet_input.getText().toString();
        storecity = storecity_input.getText().toString();
        storestate = storestate_input.getText().toString();
        storephone = storephone_input.getText().toString();
        storenotes = storenotes_input.getText().toString();
        EditText et = (EditText) findViewById(R.id.ase_storename_input);
        String shopname = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storeorder_input);
        String shoporder = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storestreet_input);
        String shopstreet = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storecity_input);
        String shopcity = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storestate_input);
        String shopstate = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storephone_input);
        String shopphone = et.getText().toString();
        et = (EditText) findViewById(R.id.ase_storenotes_input);
        String shopnotes = et.getText().toString();


        // Validattion of shoporder, required if not given then default value is used.
        if(storeorder.isEmpty() | storeorder.length() < 1 ) {
            storeorder = "100";
            Toast.makeText(this, "Store order was not given. Using the default value of 100. Note! this is just informing you; there is no error.", Toast.LENGTH_SHORT).show();
        }

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

            // Add (ie and add shop button was clicked). Use database insert and clear input data.
            // Additionally prompt for the addition of Aisles (TODO as below)
            if (mode == 0) {
                shopperdb.insertShop(shopname, shoporder, shopstreet, shopcity, shopstate, shopphone, shopnotes);
                final long lastshop = shopperdb.getLastShopId();
                Toast.makeText(this, "Shop " + shopname + " was Added.", Toast.LENGTH_LONG).show();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(getApplicationContext(),AisleAddActivity.class);
                                intent.putExtra("Caller",THIS_ACTIVITY).putExtra("SHOPID",shopperdb.getLastShopId());
                                startActivity(intent);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                shopperdb.insertAisle("Default",shopperdb.getLastShopId(),"100");
                                yesnoresult = false;
                                break;
                        }
                    }
                };

                AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
                okdialog.setMessage(getString(R.string.shopaddaisletitle));
                //okdialog.setMessage("Do you wish to add Aisles to Shop " + shopname + "?").setCancelable(true).setPositiveButton("YES", dialogClickListener).setNegativeButton("NO",dialogClickListener).show();
                okdialog.setMessage(getString(R.string.shopaddaisletext001) + shopname + getString(R.string.shopaddaisletext002))
                        .setCancelable(true)
                        .setPositiveButton("YES", dialogClickListener)
                        .setNegativeButton("NO",dialogClickListener)
                        .show();

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
                shopperdb.updateShop(getIntent().getStringExtra("ShopID"), shopname, shoporder, shopstreet, shopcity, shopstate, shopphone, shopnotes);
                doneAdding(view);
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
}
