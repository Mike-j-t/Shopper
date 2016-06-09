package mjt.shopper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

/**
 * Created by Mike092015 on 9/02/2016.
 */
public class ProductListByCursorActivity extends AppCompatActivity {
    private final static String THIS_ACTIVITY = "ProductListByCursorActivity";
    final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null, null, 1);

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_PRODUCTADD = 1;
    public final static int RESUMESTATE_PRODUCTUPDATE = 2;
    public final static int RESUMESTATE_PRODUCTDELETE = 2;
    public int resume_state = RESUMESTATE_NOTHING;
    SharedPreferences sp;
    public boolean devmode;
    public boolean helpoffmode;

    //==============================================================================================
    // Cursor Offsets.
    // Cursor offsets are set to the offset ino the respective cursor. They are set, once when the
    // respective cursor is invoked, by obtaining the actual index via the columns name, thus
    // negating a need to alter offsets if column orders are changed (e.g. column added/deleted)
    // Note! column use changes may still be required if adding or deleting columns from tables or
    //     queries.

    public static int products_productid_offset = -1;
    public static int products_productname_offset;
    public static int products_productorder_offset;
    public static int products_productaisleref_offset;
    public static int products_productuses_offset;
    public static int products_productnotes_offset;

    public ListView productlistview;
    public EditText productselectioninput;
    public Context productlistview_context;
    public ProductsCursorAdapter currentpca;
    public Cursor productlist_csr;
    public long productid;
    public String productname;
    public String productnotes;
    public LinearLayout productlisthelplayout;
    public String productslistsortorder = Constants.PRODUCTLISTORDER_BY_PRODUCT;
    public String productselectionstr = "";

    protected void onResume() {
        super.onResume();
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);
        if(!helpoffmode) {
            productlisthelplayout.setVisibility(View.VISIBLE);
        } else {
            productlisthelplayout.setVisibility(View.GONE);
        }
        switch(resume_state) {
            case RESUMESTATE_PRODUCTADD:case RESUMESTATE_PRODUCTUPDATE: {
                productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
                setProductsOffsets(productlist_csr);
                currentpca.swapCursor(productlist_csr);
                resume_state = RESUMESTATE_NOTHING;
                break;
            }
            default: {
                resume_state = RESUMESTATE_NOTHING;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_by_cursor);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        productlisthelplayout = (LinearLayout) findViewById(R.id.productlist_help_layout);
        if(!helpoffmode) {
            productlisthelplayout.setVisibility(View.VISIBLE);
        } else {
            productlisthelplayout.setVisibility(View.GONE);
        }


        productlistview = (ListView) findViewById(R.id.aplbclv01);
        productlistview_context = productlistview.getContext();

        productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
        setProductsOffsets(productlist_csr);
        currentpca = new ProductsCursorAdapter(this, productlist_csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        productlistview.setAdapter(currentpca);
        productselectioninput = (EditText) findViewById(R.id.productlist_selection_input);

        productlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder okdialog = new AlertDialog.Builder(productlistview_context);
                okdialog.setTitle(R.string.productlistentryclicktitle);
                okdialog.setMessage(R.string.productlistentryclickmessage001);
                okdialog.setCancelable(true);
                final int pos = position;
                okdialog.setNegativeButton(R.string.standardstockproductlist, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(productlistview_context, AddProductToShopActivity.class);
                        productlist_csr.moveToPosition(pos);
                        intent.putExtra("Caller", THIS_ACTIVITY);
                        intent.putExtra("PRODUCTID", productlist_csr.getLong(products_productid_offset));
                        intent.putExtra("ProductName", productlist_csr.getString(products_productname_offset));
                        intent.putExtra("ProductNotes", productlist_csr.getString(products_productnotes_offset));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                okdialog.setPositiveButton(R.string.standardedittext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resume_state = RESUMESTATE_PRODUCTUPDATE;
                        Intent intent = new Intent(productlistview_context, ProductAddActivity.class);
                        intent.putExtra("Caller", THIS_ACTIVITY + "Update");
                        productlist_csr.moveToPosition(pos);
                        intent.putExtra("ProductName", productlist_csr.getString(products_productname_offset));
                        intent.putExtra("ProductNotes", productlist_csr.getString(products_productnotes_offset));
                        intent.putExtra("PRODUCTID", productlist_csr.getLong(products_productid_offset));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                okdialog.setNeutralButton(R.string.standardbacktext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                okdialog.show();
            }
        });

        productlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO add delete a product code here.
                //TODO only allow deletes if product is unused NOTE! could be quite complex.
                resume_state = RESUMESTATE_PRODUCTDELETE;
                productlist_csr.moveToPosition(position);
                productid = productlist_csr.getLong(products_productid_offset);
                productname = productlist_csr.getString(products_productname_offset);
                AlertDialog.Builder okdialog = new AlertDialog.Builder(findViewById(R.id.aplbclv01).getContext());
                okdialog.setTitle(getString(R.string.productconfirmdeletetitle));
                okdialog.setMessage(getString(R.string.productconfirmdeletetext001) + productname + getString(R.string.productconfirmdeletetext002));
                okdialog.setCancelable(true);
                okdialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopperdb.deleteProduct(productid);
                        productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
                        currentpca.swapCursor(productlist_csr);
                    }
                });
                okdialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                okdialog.show();
                resume_state = RESUMESTATE_NOTHING;
                return true;
            }
        });

        productselectioninput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productselectionstr = productselectioninput.getText().toString();
                productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
                currentpca.swapCursor(productlist_csr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productlist_csr.close();
        shopperdb.close();
    }

    public void aplbcadd(View view) {
        resume_state = RESUMESTATE_PRODUCTADD;
        Intent intent = new Intent(this, ProductAddActivity.class);
        intent.putExtra("Caller",THIS_ACTIVITY);
        startActivity(intent);
    }
    public void aplbcdone(View view) { this.finish(); }

    public void orderByProduct(View view) {
        productslistsortorder = Constants.PRODUCTLISTORDER_BY_PRODUCT;
        productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
        currentpca.swapCursor(productlist_csr);
    }
    public void orderByNotes(View view) {
        productslistsortorder = Constants.PRODUCTLISTORDER_BY_NOTES;
        productlist_csr = shopperdb.getProductsAsCursor(productslistsortorder,productselectionstr);
        currentpca.swapCursor(productlist_csr);
    }

    // Set Products Table query offsets into returned cursor, if not already set
    public void setProductsOffsets(Cursor cursor) {
        if(products_productid_offset != -1) {
            return;
        }
        products_productid_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ID);
        products_productname_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME);
        products_productorder_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_ORDER);
        products_productaisleref_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_AISLE);
        products_productuses_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_USES);
        products_productnotes_offset = cursor.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NOTES);
    }
}