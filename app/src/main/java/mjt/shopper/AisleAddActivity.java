package mjt.shopper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
 * Created by Mike092015 on 5/02/2016.
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
        if (caller.equals("ShopAddActivity")
                | caller.equals("AddProductToShopActivity")
                | caller.equals("AisleListByCursorActivity")
                | caller.equals("AisleListByCursorActivityUpdate")
                | caller.equals(THIS_ACTIVITY + "Update")) {
            long passedshopid = getIntent().getLongExtra("SHOPID", 0);
            if (passedshopid > 0) {
                shoplistcsr.moveToPosition(-1);
                while(shoplistcsr.moveToNext()) {
                    if (shoplistcsr.getLong(0) == passedshopid) {
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
                        String shopname = shoplistcsr.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX);
                        shopid = shoplistcsr.getInt(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX);
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
        aislespershoplistview = (ListView) findViewById(R.id.aisleaddedit_aisleslist);
        aislespershopcursoradapter =  new AislesCursorAdapter(this,aislespershopcursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        aislespershoplistview.setAdapter(aislespershopcursoradapter);

        /*

        // If Item (Aisle) is clicked then allow the aisle to be edited or to be stocked
        // Note!! if edit then starts another instance of this activity as a child and as update
        // If stock then start AddProductToShop activity
        aislespershoplistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder okdialog = new AlertDialog.Builder(view.getContext());
                okdialog.setTitle(getString(R.string.aislelistclicktitle));
                okdialog.setMessage(getString(R.string.aislelistclicktext001));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton(getString(R.string.standardedittext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_AISLEUPDATE;
                        Intent intent = new Intent(aislespershoplistview.getContext(), AisleAddActivity.class);
                        intent.putExtra("Caller", THIS_ACTIVITY + "Update");
                        intent.putExtra("AisleID", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AISLEID", aislespershopcursor.getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AisleName", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                        intent.putExtra("AisleOrder", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_ORDER_INDEX));
                        intent.putExtra("AisleShopRef", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        intent.putExtra("SHOPID", aislespershopcursor.getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                okdialog.setNegativeButton(getString(R.string.standardstockaisletext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_AISLESTOCK;
                        Intent intent = new Intent(aislespershoplistview.getContext(), AddProductToShopActivity.class);
                        intent.putExtra("Caller", THIS_ACTIVITY + "Update");
                        intent.putExtra("AisleID", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AISLEID", aislespershopcursor.getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AisleName", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                        intent.putExtra("AisleOrder", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_ORDER_INDEX));
                        intent.putExtra("AisleShopRef", aislespershopcursor.getString(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        intent.putExtra("SHOPID", aislespershopcursor.getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                okdialog.setNeutralButton(getString(R.string.standardbacktext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_NOTHING;
                        dialog.cancel();
                    }
                });
                okdialog.show();
            }
        });

        // If Item (Aisle) is Long Clicked then allow the aisle to be deleted
        aislespershoplistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                resume_state = RESUMESTATE_AISLEDELETE;
                AlertDialog.Builder okdialog = new AlertDialog.Builder(view.getContext());
                okdialog.setTitle(getString(R.string.aisleconfirmdeletetitle));
                okdialog.setMessage(getString(R.string.aisleconfirmdeletetext001));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton(getString(R.string.standarddeletetext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopperdb.deleteAisle(aislespershopcursor.getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        aislespershopcursor = shopperdb.getAislesPerShopAsCursor(shopid,"");
                        aislespershopcursoradapter.swapCursor(aislespershopcursor);
                        resume_state = RESUMESTATE_NOTHING;
                        dialog.cancel();
                    }
                });
                okdialog.setNegativeButton(getString(R.string.standardcanceltext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_NOTHING;
                        dialog.cancel();
                    }
                });
                okdialog.show();
                return true;
            }
        });

        */

        //TODO click listener long click to delete
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
}
