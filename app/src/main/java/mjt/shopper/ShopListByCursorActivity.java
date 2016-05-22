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
import android.widget.TextView;

public class ShopListByCursorActivity extends AppCompatActivity {

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_SHOPADD = 1;
    public final static int RESUMESTATE_SHOPSTOCK = 2;
    public final static int RESUMESTATE_SHOPDELETE =3;
    public final static int RESUMESTATE_SHOPUPDATE = 4;
    public int resume_state = RESUMESTATE_NOTHING;
    public boolean devmode;
    public boolean helpoffmode;
    public ShopsCursorAdapter currentsca;
    public Cursor shoplistcursor;
    public ListView shoplistlistview;
    public LinearLayout shoplisthelplayout;
    public TextView storelist_store_heading;
    public TextView storelist_order_heading;
    public TextView storelist_city_heading;
    public TextView storelist_street_heading;
    public TextView storelist_state_heading;
    public TextView storelist_phone_heading;
    public TextView storelist_notes_heading;
    public String storelistorder = Constants.STORELISTORDER_BY_STORE;

    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
    private final static String THIS_ACTIVITY = "ShopListByCursorActivity";

    @Override
    protected void onResume() {
        super.onResume();
        if(!helpoffmode) {
            shoplisthelplayout.setVisibility(View.VISIBLE);
        } else {
            shoplisthelplayout.setVisibility(View.GONE);
        }
        switch(resume_state) {
            case RESUMESTATE_SHOPADD: case RESUMESTATE_SHOPUPDATE: {
                Cursor csr = shopperdb.getShopsAsCursor(storelistorder);
                currentsca.swapCursor(csr);
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
            default: {
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list_by_cursor);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode), false);
        shoplistlistview = (ListView) findViewById(R.id.storelist);

        shoplisthelplayout = (LinearLayout) findViewById(R.id.shoplist_help_layout);
        if(!helpoffmode) {
            shoplisthelplayout.setVisibility(View.VISIBLE);
        } else {
            shoplisthelplayout.setVisibility(View.GONE);
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        storelist_store_heading = (TextView) findViewById(R.id.storelist_store_heading);
        storelist_order_heading = (TextView) findViewById(R.id.storelist_order_heading);
        storelist_city_heading = (TextView) findViewById(R.id.storelist_city_heading);
        storelist_street_heading = (TextView) findViewById(R.id.storelist_street_heading);
        storelist_state_heading = (TextView) findViewById(R.id.storelist_state_heading);
        storelist_phone_heading = (TextView) findViewById(R.id.storelist_phone_heading);
        storelist_notes_heading = (TextView) findViewById(R.id.storelist_notes_heading);

        // Populate the ListView with shop data
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        //final ShopsCursorAdapter adapter = new ShopsCursorAdapter(this,shoplistcursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        currentsca = new ShopsCursorAdapter(this,shoplistcursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        shoplistlistview.setAdapter(currentsca);

        // List item click handling to edit
        shoplistlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shopname = currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX);
                long shopid = currentsca.getCursor().getLong(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX);
                //String msg = getString(R.string.shoplistclicknoproductsmessage);
                boolean products_exist = false;
                boolean aisles_exist = false;
                if(shopperdb.aislesPerShop(shopid) > 0) {
                    aisles_exist = true;
                }
                if (shopperdb.numberOfProducts() > 0) {
                    products_exist = true;
                    //msg = getString(R.string.shoplistclicknoproductsmessage);
                }


                AlertDialog.Builder optionsdialg = new AlertDialog.Builder(view.getContext());
                optionsdialg.setTitle(getString(R.string.shoplistclicktitle));
                optionsdialg.setMessage(getString(R.string.shoplistclickmessage));
                optionsdialg.setCancelable(true);
                //Shop Edit
                optionsdialg.setPositiveButton(getString(R.string.standardedittext) + " - " + shopname, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_SHOPUPDATE;
                        Intent intent = new Intent(findViewById(R.id.storelist).getContext(), ShopAddActivity.class);
                        intent.putExtra("Caller", THIS_ACTIVITY + "Update");
                        intent.putExtra("ShopID", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storeid), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storename), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storeorder), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storestreet), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storecity), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storestate), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storephone), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
                        intent.putExtra(getResources().getString(R.string.intentkey_storenotes), currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
                        intent.putExtra("ShopName", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
                        intent.putExtra("ShopOrder", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
                        intent.putExtra("ShopStreet", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
                        intent.putExtra("ShopCity", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
                        intent.putExtra("ShopState", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
                        intent.putExtra("ShopPhone", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
                        intent.putExtra("ShopNotes", currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                // Stock Shop
                if (products_exist & aisles_exist) {
                    optionsdialg.setNegativeButton(getString(R.string.standardstockshoptext) + " - " + shopname, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resume_state = RESUMESTATE_SHOPSTOCK;
                            Cursor csr = currentsca.getCursor();
                            //Intent intent = new Intent(((View) findViewById(R.id.aslbclv01)).getContext(), ShopStockActivity.class);
                            Intent intent = new Intent(findViewById(R.id.storelist).getContext(), AddProductToShopActivity.class);
                            intent.putExtra("Caller",THIS_ACTIVITY);
                            intent.putExtra("ShopID", csr.getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
                            intent.putExtra("ShopName", csr.getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
                            intent.putExtra("ShopOrder", csr.getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
                            intent.putExtra("ShopStreet",csr.getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
                            intent.putExtra("ShopCity", csr.getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
                            intent.putExtra("ShopState", csr.getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
                            intent.putExtra("ShopPhone", csr.getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
                            intent.putExtra("ShopNotes", csr.getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
                            intent.putExtra("SHOPID",csr.getLong(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storeid),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storename),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storeorder),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_ORDER_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storestreet),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storecity),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storestate),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STATE_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storephone),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_PHONE_INDEX));
                            intent.putExtra(getResources().getString(R.string.intentkey_storenotes),currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COULMN_NOTES_INDEX));
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
                } else {
                    if(!aisles_exist) {
                        //TODO allow aisles to be added??????
                    }
                }
                optionsdialg.setNeutralButton(getString(R.string.standardbacktext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                optionsdialg.show();
            }
        });

        // List Item Long Click handling ie to Delete the shop
        shoplistlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                resume_state = RESUMESTATE_SHOPDELETE;
                final String shopid = currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMNN_ID_INDEX);
                final String shopname = currentsca.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_NAME_INDEX);
                //final String shopstreet = adapter.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_STREET_INDEX);
                //final String shopcity = adapter.getCursor().getString(ShopperDBHelper.SHOPS_COLUMN_CITY_INDEX);

                AlertDialog.Builder okdialog = new AlertDialog.Builder(v.getContext());
                okdialog.setTitle(getString(R.string.shoplistlongclicktitle));
                okdialog.setMessage(getString(R.string.shoplistlongclickmessage) + " " + shopname + " " + getString(R.string.shoplistlongclickmessageextra));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton("DELETE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shopperdb.deleteShop(shopid);
                                shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
                                currentsca.swapCursor(shoplistcursor);
                                dialog.cancel();
                            }
                        });
                okdialog.setNeutralButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                okdialog.create();
                okdialog.show();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shoplistcursor.close();
        shopperdb.close();
    }

    // Add button to allow new sops to be added.
    public void aslbcAddShop(View view) {
        resume_state = RESUMESTATE_SHOPADD;
        Intent intent = new Intent(this,ShopAddActivity.class);
        intent.putExtra("Caller", THIS_ACTIVITY);
        startActivity(intent);
    }
    //Order by Store
    public void orderByStore(View view) {
        storelistorder = Constants.STORELISTORDER_BY_STORE;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByOrder(View view) {
        storelistorder = Constants.STORELISTORDER_BY_ORDER;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByCity(View view) {
        storelistorder = Constants.STORELISTORDER_BY_CITY;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByStreet(View view) {
        storelistorder = Constants.STORELISTORDER_BY_STREET;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByState(View view) {
        storelistorder = Constants.STORELIST_ORDER_BY_STATE;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByPhone(View view) {
        storelistorder = Constants.STORELISTORDER_BY_PHONE;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    public void orderByNotes(View view) {
        storelistorder = Constants.STORELISTORDER_BY_NOTES;
        shoplistcursor = shopperdb.getShopsAsCursor(storelistorder);
        currentsca.swapCursor(shoplistcursor);
    }
    // Done button will finish this activity
    public void aslbcDone(View view) { this.finish(); }
}