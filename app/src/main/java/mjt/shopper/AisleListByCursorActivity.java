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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * Created by Mike092015 on 6/02/2016.
 */
public class AisleListByCursorActivity extends AppCompatActivity {

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_AISLEADD = 1;
    public final static int RESUMESTATE_AISLESTOCK = 2;
    public final static int RESUMESTATE_AISLEDELETE =3;
    public final static int RESUMESTATE_AISLEUPDATE = 4;
    public int resume_state = RESUMESTATE_NOTHING;
    public boolean devmode;
    public boolean helpoffmode;
    public ShopsCursorAdapter currentsca;
    public AislesCursorAdapter currentaca;
    public ShopListSpinnerAdapter currentslspa;
    public long currentshopid;
    public LinearLayout aislelisthelplayout;
    public SharedPreferences sp;
    public String aislelistsortorder = Constants.AISLELISTORDER_BY_ORDER;

    private final static String THIS_ACTIVITY = "AisleListByCursorActivity";
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null, null, 1);

    public ListView aisleslistview;
    public Cursor aislelistcursor;
    public Cursor shopspinnercursor;
    private int shopid = 0;

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

    protected void onResume() {
        super.onResume();
        switch (resume_state) {
            case RESUMESTATE_AISLEADD:case RESUMESTATE_AISLEUPDATE: {
                aislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid, aislelistsortorder);
                setAislesOffsets(aislelistcursor);
                currentaca.swapCursor(aislelistcursor);
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
            default: {
                resume_state = RESUMESTATE_NOTHING;
            }
        }
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        if(!helpoffmode) {
            aislelisthelplayout.setVisibility(View.VISIBLE);
        } else {
            aislelisthelplayout.setVisibility(View.GONE);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aisle_list_by_cursor);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode), false);

        aislelisthelplayout = (LinearLayout) findViewById(R.id.aislelist_help_layout);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode), false);

        if (!helpoffmode) {
            aislelisthelplayout.setVisibility(View.VISIBLE);
        } else {
            aislelisthelplayout.setVisibility(View.GONE);
        }

        final Spinner aisleshopname = (Spinner) findViewById(R.id.aislelist_storeselect_selector);
        shopspinnercursor = shopperdb.getShopsAsCursor("");
        setShopsOffsets(shopspinnercursor);
        final ShopListSpinnerAdapter adapter = new ShopListSpinnerAdapter(this, shopspinnercursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        aisleshopname.setAdapter(adapter);


        // Click on the spinner (dropdown) to select a Shop who's Aisles are to be displayed
        aisleshopname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shopspinnercursor.moveToPosition(position);
                currentshopid = shopspinnercursor.getLong(shops_shopid_offset);
                aislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid, aislelistsortorder);
                setAislesOffsets(aislelistcursor);
                final ListView lv = (ListView) findViewById(R.id.aislelist_listview);
                AislesCursorAdapter aisleadapter = new AislesCursorAdapter(lv.getContext(), aislelistcursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                currentaca = aisleadapter;
                lv.setAdapter(aisleadapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Click on an Aisle to update the Aisle or Stock the Aisle
        // Note products must exist in order to stock the Aisle
        aisleslistview = (ListView) findViewById(R.id.aislelist_listview);
        aisleslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                boolean products_exist = false;
                boolean aisles_exist = false;
                long aislespershopcount = shopperdb.aislesPerShop(currentshopid);
                long productcount = shopperdb.numberOfProducts();
                if (aislespershopcount > 0) {
                    aisles_exist = true;
                }
                if (productcount > 0) {
                    products_exist = true;
                    //msg = getString(R.string.shoplistclicknoproductsmessage);
                }

                AlertDialog.Builder okdialog = new AlertDialog.Builder(view.getContext());
                okdialog.setTitle(getString(R.string.aislelistclicktitle));
                okdialog.setMessage(getString(R.string.aislelistclicktext001));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton(getString(R.string.standardedittext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_AISLEUPDATE;
                        Intent intent = new Intent(findViewById(R.id.aislelist_listview).getContext(), AisleAddActivity.class);
                        AislesCursorAdapter aisleadapter = (AislesCursorAdapter) ((ListView) findViewById(R.id.aislelist_listview)).getAdapter();
                        intent.putExtra("Caller", THIS_ACTIVITY + "Update");
                        intent.putExtra("AisleID", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AISLEID", aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                        intent.putExtra("AisleName", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                        intent.putExtra("AisleOrder", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_ORDER_INDEX));
                        intent.putExtra("AisleShopRef", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        intent.putExtra("SHOPID", aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                if (aisles_exist & products_exist) {
                    int test = 1;
                    okdialog.setNegativeButton(getString(R.string.standardstockaisletext), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resume_state = RESUMESTATE_AISLESTOCK;
                            Intent intent = new Intent(findViewById(R.id.aislelist_listview).getContext(), AddProductToShopActivity.class);
                            AislesCursorAdapter aisleadapter = (AislesCursorAdapter) ((ListView) findViewById(R.id.aislelist_listview)).getAdapter();
                            intent.putExtra("Caller", THIS_ACTIVITY + "Stock");
                            intent.putExtra("AisleID", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                            intent.putExtra("AISLEID", aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX));
                            intent.putExtra("AisleName", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX));
                            intent.putExtra("AisleOrder", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_ORDER_INDEX));
                            intent.putExtra("AisleShopRef", aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                            intent.putExtra("AISLESHOPREF", aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                            intent.putExtra("SHOPID", aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX));
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
                }

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

        // Long Click on an Aisle to delete the Aisle

        aisleslistview = (ListView) findViewById(R.id.aislelist_listview);
        aisleslistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int positions, long id) {
                long aislespershopcount = shopperdb.aislesPerShop(currentshopid);
                resume_state = RESUMESTATE_AISLEDELETE;
                final AislesCursorAdapter aisleadapter = (AislesCursorAdapter) ((ListView) findViewById(R.id.aislelist_listview)).getAdapter();
                final String aisleidasstring = aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_ID_INDEX);
                final long aisleid = aisleadapter.getCursor().getLong(ShopperDBHelper.AISLES_COLUMN_ID_INDEX);
                final String aislename = aisleadapter.getCursor().getString(ShopperDBHelper.AISLES_COLUMN_NAME_INDEX);
                final int aisleshopref = aisleadapter.getCursor().getInt(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX);
                if(aislespershopcount > 1) {
                    AlertDialog.Builder okdialog = new AlertDialog.Builder(findViewById(R.id.aislelist_listview).getContext());
                    okdialog.setTitle(getString(R.string.aisleconfirmdeletetitle));
                    okdialog.setMessage(getString(R.string.aisleconfirmdeletetext001) + aislename);
                    //okdialog.setMessage("Do you really want to delete AISLE " + aislename + "?");
                    okdialog.setCancelable(true);
                    okdialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shopperdb.deleteAisle(aisleid);
                            aislelistcursor = shopperdb.getAislesPerShopAsCursor(aisleshopref, aislelistsortorder);
                            setAislesOffsets(aislelistcursor);
                            aisleadapter.swapCursor(aislelistcursor);
                            resume_state = RESUMESTATE_NOTHING;
                            dialog.cancel();
                        }
                    });
                    okdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resume_state = RESUMESTATE_NOTHING;
                            dialog.cancel();
                        }
                    });
                    okdialog.show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aislelistcursor.close();
        shopspinnercursor.close();
        shopperdb.close();
    }

    public void orderByAisle(View view) {
        aislelistsortorder = Constants.AISLELISTORDER_BY_AISLE;
        aislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid,aislelistsortorder);
        currentaca.swapCursor(aislelistcursor);
    }
    public void orderByOrder(View view) {
        aislelistsortorder = Constants.AISLELISTORDER_BY_ORDER;
        aislelistcursor = shopperdb.getAislesPerShopAsCursor(currentshopid,aislelistsortorder);
        currentaca.swapCursor(aislelistcursor);
    }

    public void aalbcadd(View view) {
        resume_state = RESUMESTATE_AISLEADD;
        Intent intent = new Intent(this,AisleAddActivity.class);
        intent.putExtra("Caller",THIS_ACTIVITY);
        intent.putExtra("SHOPID", currentshopid);
        startActivity(intent);
    }
    public void aalbcdone(View view) { this.finish(); }

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