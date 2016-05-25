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
 */
public class AddPurchaseableProductsToShopList extends AppCompatActivity{

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
    public String purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_PRODUCT;
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

        csr = shopperdb.getPurchaseableProducts("","",purchaseableproductslistsortorder);
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
                csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
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
                csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
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
                long currentproductid = csr.getLong(1);
                long currentaisleid = csr.getLong(0);
                double currentprice = csr.getDouble(2);
                shopperdb.insertShopListEntry(csr.getLong(1),csr.getLong(0),1,csr.getDouble(2),true);
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
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_PRODUCT;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByStore(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STORE;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByCity(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_CITY;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByCost(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_COST;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByStreet(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_STREET;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }
    public void orderByAisle(View view) {
        purchaseableproductslistsortorder = Constants.PURCHASEABLEPRODUCTSLISTORDER_BY_AISLE;
        csr = shopperdb.getPurchaseableProducts(currentproductid,currentshopid,purchaseableproductslistsortorder);
        currentppa.swapCursor(csr);
    }

    //==============================================================================================
    public void ppl_done(View view) {
        this.finish();
    }
    public void ppl_show_shoplist(View view) {
        Toast.makeText(getApplicationContext(),"This will show the shopping list",Toast.LENGTH_LONG).show();
    }
}
