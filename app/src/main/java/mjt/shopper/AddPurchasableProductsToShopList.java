package mjt.shopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Created by Mike092015 on 16/03/2016.
 * AddPurchasableProductsToShopList AKA TO GET
 * Displays List of Products that can be purchased i.e. productusage rows
 * If clicked then a Shopping List entry is made if one doesn't exist or
 *  the quantity is increased by 1 (handled by insertShoppingListEntry )
 */
public class AddPurchasableProductsToShopList extends AppCompatActivity{

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    //Purchasable Products Query
    public static int purchasableproducts_productusageaisleref_offset = -1; //**
    public static int purchasableproducts_productusageproductref_offset;
    public static int purchasableproducts_productusagecost_offset;
    public static int purchasableproducts_productid_offset; //**
    public static int purchasableproducts_productname_offset;
    public static int purchasableproducts_aisleid_offset; //**
    public static int purchasableproducts_aislename_offset;
    public static int purchasableproducts_shopname_offset;
    public static int purchasableproducts_shopcity_offset;
    public static int purchasableproducts_shopstreet_offset;

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_PRODUCTADDTOSHOPLIST = 1;
    public int resume_state = RESUMESTATE_NOTHING;
    public boolean devmode;
    public boolean helpoffmode;
    public PurchaseableProductsAdapter currentppa;

    private final static String THIS_ACTIVITY = "AddPurchasableProductsToShopList";
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this, null, null, 1);
    private ListView currentppalv;
    private Cursor csr;
    private EditText productselect;
    private EditText shopselect;
    private LinearLayout purchaseableproductslist_help_layout;
    public String purchaseableproductslistsortorder = Constants.PURCHASABLEPRODUCTSLISTORDER_BY_PRODUCT;
    public String currentshopid = "";
    public String currentproductid = "";


    protected  void onResume() {
        super.onResume();
        if(!helpoffmode) {
            purchaseableproductslist_help_layout.setVisibility(View.VISIBLE);
        } else {
            purchaseableproductslist_help_layout.setVisibility(View.GONE);
        }
        switch (resume_state) {
            default: {
                resume_state = RESUMESTATE_NOTHING;
            }
        }
    }

    protected void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);
        setContentView(R.layout.purchaseable_product_list);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        purchaseableproductslist_help_layout = (LinearLayout) findViewById(R.id.purchaeableproductlist_help_layout);
        if(!helpoffmode) {
            purchaseableproductslist_help_layout.setVisibility(View.VISIBLE);
        } else {
            purchaseableproductslist_help_layout.setVisibility(View.GONE);
        }

        csr = shopperdb.getPurchasableProducts("","",purchaseableproductslistsortorder);
        setPurchasableProductsOffsets(csr);
        currentppa = new PurchaseableProductsAdapter(this, csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        currentppalv = (ListView) findViewById(R.id.purchaseable_product_list);
        currentppalv.setAdapter(currentppa);

        productselect = (EditText) findViewById(R.id.productselector);
        shopselect = (EditText) findViewById(R.id.shopselector);

        //Handle Product name input. Whenever a character is typed refresh data by requerying
        productselect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentproductid = productselect.getText().toString();
                currentshopid = shopselect.getText().toString();
                csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
                currentppa.swapCursor(csr);
            }
        });

        // Handle Shop input. Whenever a character is typed then refresh data by requerying
        shopselect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentproductid = productselect.getText().toString();
                currentshopid = shopselect.getText().toString();
                csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
                currentppa.swapCursor(csr);
            }
        });

        // Add Item Select listener too add the item to the shopping list
        currentppalv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                Toast.makeText(context,"You clicked on a product to add the product",Toast.LENGTH_LONG).show();
                csr.moveToPosition(position);
                long currentproductid = csr.getLong(purchasableproducts_productusageproductref_offset);
                long currentaisleid = csr.getLong(purchasableproducts_aisleid_offset);
                double currentprice = csr.getDouble(purchasableproducts_productusagecost_offset);
                shopperdb.insertShopListEntry(csr.getLong(purchasableproducts_productusageproductref_offset),
                        csr.getLong(purchasableproducts_aisleid_offset),
                        1,
                        csr.getDouble(purchasableproducts_productusagecost_offset),
                        true);
                int test = 0;

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        csr.close();
        shopperdb.close();
    }

    public void orderByProduct(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASABLEPRODUCTSLISTORDER_BY_PRODUCT;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByStore(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STORE;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByCity(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_CITY;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByCost(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_COST;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByStreet(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STREET;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByAisle(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_AISLE;
        csr = shopperdb.getPurchasableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }

    //==============================================================================================
    public void ppl_done(View view) {
        this.finish();
    }
    public void ppl_show_shoplist(View view) {
        Toast.makeText(getApplicationContext(),"This will show the shopping list",Toast.LENGTH_LONG).show();
    }

    public void setPurchasableProductsOffsets(Cursor cursor) {
        if(purchasableproducts_productusageaisleref_offset != -1) {
            return;
        }
        purchasableproducts_productusageaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRIMARY_KEY_NAME);
        purchasableproducts_productusageproductref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF);
        purchasableproducts_productusagecost_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST);
        purchasableproducts_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID_FULL);
        purchasableproducts_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        purchasableproducts_aisleid_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_ID_FULL);
        purchasableproducts_aislename_offset = cursor.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME);
        purchasableproducts_shopname_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME);
        purchasableproducts_shopcity_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY);
        purchasableproducts_shopstreet_offset = cursor.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET);
    }
}
