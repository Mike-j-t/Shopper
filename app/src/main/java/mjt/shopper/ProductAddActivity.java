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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mike092015 on 9/02/2016.
 * Changed for GIT
 */
public class ProductAddActivity extends AppCompatActivity {
    // standard definitions
    public final static String THIS_ACTIVITY = "ProductAddActivity";
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this, null, null, 1);
    // close to satndard definitions
    public final static int RESUMESTATE_NOTHING = 0;
    public int resume_state = RESUMESTATE_NOTHING;
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

    // Layout based variables - created here to allow access throughout this activity
    public TextView productname_label;
    public EditText productname_edittext;
    public TextView productnotes_label;
    public EditText productnotes_edittext;
    public TextView save_button;
    public TextView done_button;
    public String productname;
    public String productnotes;
    public ListView currentproductslist;
    public Cursor currentproductlistcsr;
    public ProductsCursorAdapter currentproductslistadapter;
    public LinearLayout productaddedithelplayout;
    public String productlistsortorder = Constants.PRODUCTLISTORDER_BY_PRODUCT;

    // Data based variables - created here to allow access throughout this activity
    private String passedproductname = "";
    public String passedproductnotes = "";
    private long passedproductid = -1;
    private int mode = 0;

    //
    protected void onResume() {
        super.onResume();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        productaddedithelplayout = (LinearLayout) findViewById(R.id.productaddedit_help_layout);
        if(!helpoffmode) {
            productaddedithelplayout.setVisibility(View.VISIBLE);
        } else {
            productaddedithelplayout.setVisibility(View.GONE);
        }

        String caller = getIntent().getStringExtra("Caller");
        productname_label = (TextView) findViewById(R.id.productaddedit_productname_label);
        productname_edittext = (EditText) findViewById(R.id.productaddedit_productname);
        productnotes_label = (TextView) findViewById(R.id.productaddedit_productnotes_label);
        productnotes_edittext = (EditText) findViewById(R.id.productaddedit_productnotes);
        save_button = (TextView) findViewById(R.id.productaddedit_save_button);
        done_button = (TextView) findViewById(R.id.productaddedit_done_button);
        currentproductslist = (ListView) findViewById(R.id.productaddedit_currentproducts_listview);

        currentproductlistcsr = shopperdb.getProductsAsCursor(productlistsortorder,"");
        setProductsOffsets(currentproductlistcsr);
        currentproductslistadapter = new ProductsCursorAdapter(this,currentproductlistcsr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        currentproductslist.setAdapter(currentproductslistadapter);


        // Was activity invoked as an update rather than add?
        // If so then populate product name and notes fields as they exist
        // Additionally set title
        if(caller.equals("ProductListByCursorActivityUpdate")) {
            mode = 10;
            passedproductname = getIntent().getStringExtra("ProductName");
            passedproductnotes = getIntent().getStringExtra("ProductNotes");
            passedproductid = getIntent().getLongExtra("PRODUCTID",-1);
            productname_edittext.setText(passedproductname);
            productnotes_edittext.setText(passedproductnotes);
            setTitle(getResources().getString(R.string.title_activity_product_edit));
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        currentproductlistcsr.close();
        shopperdb.close();
    }
    // Save button clicked
    public void saveClicked(View view) {

        //EditText et = ((EditText) findViewById(R.id.apaet01));
        productname = productname_edittext.getText().toString();
        //et = ((EditText) findViewById(R.id.apaet02));
        productnotes = productnotes_edittext.getText().toString();
        if(productname.isEmpty() | productname == null | productname.length() < 1) {
            AlertDialog.Builder okdialog = new AlertDialog.Builder(this);
            okdialog.setTitle(getString(R.string.productnonametitle));
            okdialog.setMessage(getString(R.string.productnonametext001));
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
            if(mode == 0) {
                shopperdb.insertProduct(productname, productnotes);
                final long lastproduct = shopperdb.getLastProductId();
                Toast.makeText(this,"Product " + productname + " was Added.",Toast.LENGTH_LONG).show();
                //TODO perhaps allow products to be assigned to shops here.
                productname_edittext.requestFocus();
                productname_edittext.setText("");
                productnotes_edittext.setText("");
                refreshCurrentProductsList();
            }

            if(mode == 10) {
                if(passedproductid > 0) {
                    shopperdb.updateProduct(passedproductid, productname, productnotes);
                    Toast.makeText(this,"Product " + productname + " was Updated",Toast.LENGTH_LONG).show();
                    refreshCurrentProductsList();
                } else {
                    Toast.makeText(this,"Product " + productname + " NOT Updated - Invalid ID(" + passedproductid + ")." +
                            "\n\t Please reoprt this to the developer, as it should never happen.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void refreshCurrentProductsList() {
        currentproductlistcsr = shopperdb.getProductsAsCursor(productlistsortorder,"");
        currentproductslistadapter.swapCursor(currentproductlistcsr);
    }
    public void doneClicked(View view) {
        this.finish();
    }
    public void orderByProduct(View view) {
        productlistsortorder = Constants.PRODUCTLISTORDER_BY_PRODUCT;
        refreshCurrentProductsList();
    }
    public void orderByNotes(View view) {
        productlistsortorder = Constants.PRODUCTLISTORDER_BY_NOTES;
        refreshCurrentProductsList();
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
