package mjt.shopper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 *
 */
public class AisleAddActivity extends AppCompatActivity  {

    private final static int RESUMESTATE_NOTHING = 0;
    private final static int RESUMESTATE_AISLEADD = 1;
    private final static int RESUMESTATE_AISLESTOCK = 2;
    private final static int RESUMESTATE_AISLEDELETE = 3;
    private final static int RESUMESTATE_AISLEUPDATE = 4;
    public int resume_state = RESUMESTATE_NOTHING;

    public final static String THIS_ACTIVITY = "AisleAddActivity";
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

    // Variables to store aisles table offsets as obtained via the defined column names by
    // call to setAislesOffsets (aisles_aisleid_offset set -1 to act as notdone flag )
    public static int aisles_aisleid_offset = -1;
    public static int aisles_aislename_offset;
    public static int aisles_aisleorder_offset;
    public static int aisles_aisleshopref_offset;

    private Cursor shoplistcsr;
    private ShopListSpinnerAdapter shoplistspinneradapter;
    private Spinner shoplistspinner;
    private long shopid = -1;
    private int passedshopposition = -1;
    private int mode = 0;
    private Cursor aislespershopcursor;
    private AislesCursorAdapter aislespershopcursoradapter;
    private ListView aislespershoplistview;
    private LinearLayout aisleaddedithelplayout;
    public String aislelistsortorder = Constants.AISLELISTORDER_BY_ORDER;

    protected void onResume() {
        super.onResume();
        switch (resume_state) {
            case RESUMESTATE_AISLEADD:case RESUMESTATE_AISLEUPDATE: {
                resume_state = RESUMESTATE_NOTHING;
                aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,"");
                setAislesOffsets(aislespershopcursor);
                aislespershopcursoradapter.swapCursor(aislespershopcursor);
                break;
            }
        }
        if(!helpoffmode) {
            aisleaddedithelplayout.setVisibility(View.VISIBLE);
        } else {
            aisleaddedithelplayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aisle_add);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        aisleaddedithelplayout = (LinearLayout) findViewById(R.id.aisleaddedit_help_layout);

        if(!helpoffmode) {
            aisleaddedithelplayout.setVisibility(View.VISIBLE);
        } else {
            aisleaddedithelplayout.setVisibility(View.GONE);
        }

        final String caller = getIntent().getStringExtra("Caller");
        shopid = getIntent().getLongExtra("SHOPID", -1);
        shoplistspinner = (Spinner) findViewById(R.id.aisleaddedit_storeselector);

        //
        if(caller.equals("AisleListByCursorActivityUpdate") | caller.equals(THIS_ACTIVITY + "Update")) {
            mode = 10;
            ((EditText)  findViewById(R.id.aisleaddedit_aislename_input)).setText(getIntent().getStringExtra("AisleName"));
            ((EditText) findViewById(R.id.aisleaddedit_aisleorder)).setText(getIntent().getStringExtra("AisleOrder"));
            setTitle(getResources().getString(R.string.title_activity_aisle_edit));
        }

        if (caller.equals("AisleListByCursorActivity") | caller.equals("AisleListByCursorActivityUpdate")
                | caller.equals("AddProductToShopActivity") | caller.equals(THIS_ACTIVITY + "Update")) {
            findViewById(R.id.aisleaddedit_done_button).setVisibility(View.VISIBLE);
        }
        shoplistcsr = shopperdb.getShopsAsCursor("");
        setShopsOffsets(shoplistcsr);
        if (caller.equals("ShopAddActivity")
                | caller.equals("AddProductToShopActivity")
                | caller.equals("AisleListByCursorActivity")
                | caller.equals("AisleListByCursorActivityUpdate")
                | caller.equals(THIS_ACTIVITY + "Update")) {
            long passedshopid = getIntent().getLongExtra("SHOPID", 0);
            if (passedshopid > 0) {
                shoplistcsr.moveToPosition(-1);
                while(shoplistcsr.moveToNext()) {
                    if (shoplistcsr.getLong(shops_shopid_offset) == passedshopid) {
                        passedshopposition = shoplistcsr.getPosition();
                        break;
                    }
                }
                shoplistcsr.moveToPosition(-1);
            }
        }

        //Create the Cursor Adapter for the Spinner (Dropdown)
        shoplistspinneradapter = new ShopListSpinnerAdapter(this,shoplistcsr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // If updating (mode 10) then disable the spinner selection of another shop
        if(mode == 10) {
            shoplistspinner.setEnabled(false);
        } else {
            shoplistspinner.setEnabled(true);
        }

        // attach/link the adapter
        shoplistspinner.setAdapter(shoplistspinneradapter);
        if (passedshopposition > -1) {
            shoplistspinner.setSelection(passedshopposition);
            passedshopposition = -1;
        }
        shoplistspinner.post(new Runnable() {
            @Override
            public void run() {
                shoplistspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        shoplistcsr.moveToPosition(position);
                        String shopname = shoplistcsr.getString(shops_shopname_offset);
                        shopid = shoplistcsr.getInt(shops_shopid_offset);
                        Toast.makeText(view.getContext(), "You have Selected Shop " + shopname, Toast.LENGTH_LONG).show();
                        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,"");
                        aislespershopcursoradapter.swapCursor(aislespershopcursor);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        //Get the list of aisles in the current shop
        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid, aislelistsortorder);
        setAislesOffsets(aislespershopcursor);
        aislespershoplistview = (ListView) findViewById(R.id.aisleaddedit_aisleslist);
        aislespershopcursoradapter =  new AislesCursorAdapter(this,aislespershopcursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        aislespershoplistview.setAdapter(aislespershopcursoradapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        shoplistcsr.close();
        aislespershopcursor.close();
        shopperdb.close();
    }

    public void saveClicked(View view) {
        String caller = getIntent().getStringExtra("Caller");

        // Get the input data
        EditText et = (EditText) findViewById(R.id.aisleaddedit_aislename_input);
        String aislename = et.getText().toString();
        et = (EditText) findViewById(R.id.aisleaddedit_aisleorder);
        String aisleorder = et.getText().toString();

        // If saving an Aisle with no name. Issue Alert and don't save
        // Else save the Aisle (with defailt order of 100 if the order hasn't been given)
        if(aislename.isEmpty() | aislename.length() < 1) {
            AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
            okdialog.setTitle(getString(R.string.aislenamenotgiventitle));
            //okdialog.setTitle(R.string.aislenamenotgiventitle);
            okdialog.setMessage(getString(R.string.aislenamenotgiventext));
            //okdialog.setMessage("Cannot add Aisle as the Aisle Name has not been given." +
            //        "\n\nPlease enter an Aisle Name and then click on the Save button.");
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
        } else {
            if(aisleorder.length() < 1) {
                aisleorder = "100";
            }
            if(mode == 0) {
                shopperdb.insertAisle(aislename, shopid, aisleorder);
                // Post save, make the done button available and clear input to allow another addition
                ((EditText) findViewById(R.id.aisleaddedit_aislename_input)).setText("");
                findViewById(R.id.aisleaddedit_aislename_input).requestFocus();
                ((EditText) findViewById(R.id.aisleaddedit_aisleorder)).setText("");
            }
            // Update an Aisle
            if(mode == 10 ) {
                shopperdb.updateAisle(getIntent().getStringExtra("AisleID"), aislename, aisleorder, shopid);
            }
            findViewById(R.id.aisleaddedit_done_button).setVisibility(View.VISIBLE);
        }
        // Refresh the current aisle list
        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,"");
        aislespershopcursoradapter.swapCursor(aislespershopcursor);
    }

    public void orderByAisle(View view) {
        aislelistsortorder = Constants.AISLELISTORDER_BY_AISLE;
        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,aislelistsortorder);
        aislespershopcursoradapter.swapCursor(aislespershopcursor);
    }
    public void orderByOrder(View view) {
        aislelistsortorder = Constants.AISLELISTORDER_BY_ORDER;
        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,aislelistsortorder);
        aislespershopcursoradapter.swapCursor(aislespershopcursor);
    }

    public void doneAdding(View view) {
        this.finish();
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

    // Set Aisles Table query offsets into returned cursor, if not already set
    public void setAislesOffsets(Cursor cursor) {
        if(aisles_aisleid_offset != -1) {
            return;
        }
        aisles_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID);
        aisles_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        aisles_aisleorder_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ORDER);
        aisles_aisleshopref_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_SHOP);
    }
}
