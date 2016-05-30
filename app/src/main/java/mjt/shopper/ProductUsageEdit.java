package mjt.shopper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mike092015 on 29/02/2016.
 */
public class ProductUsageEdit extends AppCompatActivity {
    private final static String THIS_ACTIVITY = "ProductUsageEdit";
    private final ShopperDBHelper shopperdb = new ShopperDBHelper(this,null,null,1);
    public ProductsPerAisleCursorAdapter plpa_adapter;
    public boolean devmode;
    public boolean helpoffmode;

    public LinearLayout productusageedit_helplayout;

    /*==============================================================================================
        Main processing
     =============================================================================================*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productusage_edit);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),false);

        productusageedit_helplayout = (LinearLayout) findViewById(R.id.productlist_help_layout);

        if(!helpoffmode) {
            productusageedit_helplayout.setVisibility(View.VISIBLE);
        } else {
            productusageedit_helplayout.setVisibility(View.GONE);
        }

        final String caller = this.getIntent().getStringExtra("CALLER");
        final long shopid = this.getIntent().getLongExtra("SHOPID", -1);
        final long aisleid = this.getIntent().getLongExtra("AISLEID", -1);
        final long productid = this.getIntent().getLongExtra("PRODUCTID", -1);
        float productprice = this.getIntent().getFloatExtra("PRODUCTPRICE", -1f);
        long productorder = this.getIntent().getLongExtra("PRODUCTORDER", -1);
        long productpurchasecount = this.getIntent().getLongExtra("PRODUCTPURCHASECOUNT", -1);
        long productfirstpurchased = this.getIntent().getLongExtra("PRODUCTFIRSTPURCHASED", -1);
        long productlastpurchased = this.getIntent().getLongExtra("PRODUCTLASTPURCHASED", -1);
        long productmincost = this.getIntent().getLongExtra("PRODUCTMINCOST", -1);
        //Cursor sidcsr = shopperdb.getShopIDFromAisleID(aisleid);
        //final long shopid = sidcsr.getLong(ShopperDBHelper.AISLES_COLUMN_SHOP_INDEX);
        final Date firstpurchased = new Date(productfirstpurchased);
        final Date lastpurchased = new Date(productlastpurchased);
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.STANDARD_DDMMYYY_FORMAT);

        ((TextView) this.findViewById(R.id.apue_shopname_data)).setText(getIntent().getStringExtra("SHOPNAME"));
        ((TextView) this.findViewById(R.id.apue_aislename_data)).setText(getIntent().getStringExtra("AISLENAME"));
        ((TextView) this.findViewById(R.id.apue_product_data)).setText(getIntent().getStringExtra("PRODUCTNAME"));
        ((TextView) this.findViewById(R.id.apue_shop_id)).setText(Long.toString(shopid));
        ((TextView) this.findViewById(R.id.apue_aisle_id)).setText(Long.toString(aisleid));
        ((TextView) this.findViewById(R.id.apue_product_id)).setText(Long.toString(productid));

        ((EditText) this.findViewById(R.id.apue_productusage_orderinaisle_data)).setText(Long.toString(productorder));
        ((EditText) this.findViewById(R.id.apue_productcost_data)).setText(Float.toString(productprice));
        ((EditText) this.findViewById(R.id.apue_productusage_buycount_data)).setText(Long.toString(productpurchasecount));
        ((EditText) this.findViewById(R.id.apue_productusage_firstbuydate_data)).setText(sdf.format(firstpurchased));
        ((EditText) this.findViewById(R.id.apue_productusage_lastbuydate_data)).setText(sdf.format(lastpurchased));
        ((EditText) this.findViewById(R.id.apue_productusage_mincost_data)).setText(Long.toString(productmincost));

        this.findViewById(R.id.apue_productusage_buycount_container).setVisibility(View.GONE);
        this.findViewById(R.id.apue_productusage_firstbuydate_container).setVisibility(View.GONE);
        this.findViewById(R.id.apue_productusage_lastbuydate_container).setVisibility(View.GONE);
        this.findViewById(R.id.apue_productusage_mincost_container).setVisibility(View.GONE);
        this.findViewById(R.id.apue_advancedoptions_explian_text_container).setVisibility(View.GONE);
        ((TextView) this.findViewById(R.id.apue_advancedoptions)).setText(getResources().getString(R.string.standardmoretext));
        if(!devmode) {
            this.findViewById(R.id.apue_advancedoptions).setVisibility(View.GONE);
        }


        this.findViewById(R.id.apue_productusage_firstbuydate_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productusage_firstbuydate_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_firstbuydate_check);
                if (!hasFocus) {
                    String givendate = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateDate(givendate);
                    if (emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                        return;
                    }
                    try {
                        Date date = sdf.parse(givendate);
                        long timestamp = date.getTime();
                        ((TextView) findViewById(R.id.apue_firstbuydate_check)).setText(Long.toString(timestamp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.findViewById(R.id.apue_productusage_lastbuydate_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productusage_lastbuydate_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_lastbuydate_check);
                if (!hasFocus) {
                    String givendate = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateDate(givendate);
                    if (emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                        return;
                    }
                    try {
                        Date date = sdf.parse(givendate);
                        long timestamp = date.getTime();
                        ((TextView) findViewById(R.id.apue_lastbuydate_check)).setText(Long.toString(timestamp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.findViewById(R.id.apue_productcost_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productcost_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_productcost_check);
                if(!hasFocus) {
                    String givencost = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateMonetary(givencost);
                    if(emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                    }
                } else {
                    check_tv.setText(new Emsg(false,0,"").getErrorMessage());
                }
            }
        });
        this.findViewById(R.id.apue_productusage_orderinaisle_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productusage_orderinaisle_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_product_orderinaisle_check);
                if(!hasFocus) {
                    String givenint = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateInteger(givenint);
                    if(emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                    }
                } else {
                    check_tv.setText(new Emsg(false,0,"").getErrorMessage());
                }
            }
        });
        this.findViewById(R.id.apue_productusage_buycount_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productusage_buycount_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_product_buycount_check);
                if(!hasFocus) {
                    String givenint = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateInteger(givenint);
                    if(emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                    } else {
                        check_tv.setText(new Emsg(false,0,"").getErrorMessage());
                    }
                }
            }
        });
        this.findViewById(R.id.apue_productusage_mincost_data).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText data_et = (EditText) findViewById(R.id.apue_productusage_mincost_data);
                TextView check_tv = (TextView) findViewById(R.id.apue_productusage_mincost_check);
                if(!hasFocus) {
                    String givencost = data_et.getText().toString();
                    Emsg emsg = mjtUtils.validateMonetary(givencost);
                    if(emsg.getErrorIndicator()) {
                        check_tv.setText(emsg.getErrorMessage());
                    } else {
                        check_tv.setText(new Emsg(false,0,"").getErrorMessage());
                    }
                }
            }
        });
    }

    /*==============================================================================================
        Cleanup
     =============================================================================================*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        shopperdb.close();
    }

    /*==============================================================================================
        Handle DONE button click ie finish this activity
     =============================================================================================*/
    public void apue_done(View view) {
        // Get the ailse and product id's (primary key)
        long aisleid = Long.parseLong(((TextView) findViewById(R.id.apue_aisle_id)).getText().toString());
        long productid = Long.parseLong(((TextView) findViewById(R.id.apue_product_id)).getText().toString());
        finish();
    }

    /*==============================================================================================
        Handle MORE/LESS (Advanced options available in developer mode)
        ie if button text is MORE then clicking button will
            a) set button text to LESS and
            b) make the advanced input options available by changing thier visibility to VISIBLE
           if button text is LESS then clicking it will
            a) set button text to MORE
            b) make the advanced input options unavailable by setting their visibility to GONE
     =============================================================================================*/
    public void apue_advancedoptions_flip(View view) {
        String buttontext = ((TextView) view.findViewById(R.id.apue_advancedoptions)).getText().toString();
        if (buttontext.equals(getResources().getString(R.string.standardmoretext))) {
            ((TextView) view.findViewById(R.id.apue_advancedoptions)).setText(getResources().getString(R.string.standardlesstext));
            findViewById(R.id.apue_advancedoptions_explian_text_container).setVisibility(View.VISIBLE);
            findViewById(R.id.apue_productusage_buycount_container).setVisibility(View.VISIBLE);
            findViewById(R.id.apue_productusage_firstbuydate_container).setVisibility(View.VISIBLE);
            findViewById(R.id.apue_productusage_lastbuydate_container).setVisibility(View.VISIBLE);
            findViewById(R.id.apue_productusage_mincost_container).setVisibility(View.VISIBLE);
        } else {
            ((TextView) view.findViewById(R.id.apue_advancedoptions)).setText(getResources().getString(R.string.standardmoretext));
            findViewById(R.id.apue_advancedoptions_explian_text_container).setVisibility(View.GONE);
            findViewById(R.id.apue_productusage_buycount_container).setVisibility(View.GONE);
            findViewById(R.id.apue_productusage_firstbuydate_container).setVisibility(View.GONE);
            findViewById(R.id.apue_productusage_lastbuydate_container).setVisibility(View.GONE);
            findViewById(R.id.apue_productusage_mincost_container).setVisibility(View.GONE);
        }
    }

    //Save Button
    /*==============================================================================================
        Handle SAVE button click ie validate and save the data (if data validates ok)
     =============================================================================================*/
    public void apue_save(View view) {
        long fbd = 0;
        long lbd = 0;
        float pc = 0.0f;
        int bc =0;
        float mc = 0.0f;
        int oia = 0;
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.STANDARD_DDMMYYY_FORMAT);

        // Check that the firstbuydate is a valid date, if not set message, issue Toast and return
        EditText fbd_et = (EditText) findViewById(R.id.apue_productusage_firstbuydate_data);
        Emsg fbd_emsg = mjtUtils.validateDate(fbd_et.getText().toString());
        if (fbd_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_firstbuydate_check)).setText(fbd_emsg.getErrorMessage());
            fbd_et.requestFocus();
            Toast.makeText(getApplicationContext(),R.string.standardNotSavedToast,Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                Date date = sdf.parse(fbd_et.getText().toString());
                fbd = date.getTime();
            } catch(ParseException e) {
                e.printStackTrace();
            }
        }
        // Check that the lastbuydate is a valid date, if not set message, issue Toast and return
        EditText lbd_et = (EditText) findViewById(R.id.apue_productusage_lastbuydate_data);
        Emsg lbd_emsg = mjtUtils.validateDate(lbd_et.getText().toString());
        if (lbd_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_lastbuydate_check)).setText(lbd_emsg.getErrorMessage());
            lbd_et.requestFocus();
            Toast.makeText(getApplicationContext(), R.string.standardNotSavedToast, Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                Date date = sdf.parse(lbd_et.getText().toString());
                lbd = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Check that the product cost is a valid monetary value, if not set message, issue Toast and return
        EditText pc_et = (EditText) findViewById(R.id.apue_productcost_data);
        Emsg pc_emsg = mjtUtils.validateMonetary(pc_et.getText().toString());
        if(pc_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_productcost_check)).setText(pc_emsg.getErrorMessage());
            pc_et.requestFocus();
            Toast.makeText(getApplicationContext(), R.string.standardNotSavedToast, Toast.LENGTH_LONG).show();
            return;
        } else {
            pc = Float.parseFloat(pc_et.getText().toString());
        }
        // Check buy count is a valid integer
        EditText bc_et = (EditText) findViewById(R.id.apue_productusage_buycount_data);
        Emsg bc_emsg = mjtUtils.validateInteger(bc_et.getText().toString());
        if(bc_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_product_buycount_check)).setText(bc_emsg.getErrorMessage());
            bc_et.requestFocus();
            Toast.makeText(getApplicationContext(),R.string.standardNotSavedToast,Toast.LENGTH_LONG).show();
            return;
        } else {
            bc = Integer.parseInt(bc_et.getText().toString());
        }
        // Check minimum cost is float
        EditText mc_et = (EditText) findViewById(R.id.apue_productusage_mincost_data);
        Emsg mc_emsg = mjtUtils.validateMonetary(mc_et.getText().toString());
        if(mc_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_productusage_mincost_check)).setText(mc_emsg.getErrorMessage());
            mc_et.requestFocus();
            Toast.makeText(getApplicationContext(),R.string.standardNotSavedToast,Toast.LENGTH_LONG).show();
            return;
        } else {
            mc = Float.parseFloat(mc_et.getText().toString());
        }
        // Check OrderinAisle is Integer
        EditText oia_et = (EditText) findViewById(R.id.apue_productusage_orderinaisle_data);
        Emsg oia_emsg = mjtUtils.validateInteger(oia_et.getText().toString());
        if(oia_emsg.getErrorIndicator()) {
            ((TextView) findViewById(R.id.apue_product_orderinaisle_check)).setText(oia_emsg.getErrorMessage());
            oia_et.requestFocus();
            Toast.makeText(getApplicationContext(),R.string.standardNotSavedToast,Toast.LENGTH_LONG).show();
            return;
        } else {
            oia = Integer.parseInt(oia_et.getText().toString());
        }
        // Get the ailse and product id's (primary key)
        long aisleid = Long.parseLong(((TextView) findViewById(R.id.apue_aisle_id)).getText().toString());
        long productid = Long.parseLong(((TextView) findViewById(R.id.apue_product_id)).getText().toString());

        shopperdb.updateProductInAisle(aisleid, productid, pc, bc, fbd, lbd, oia, mc);
        //TODO first attempt to save worked. However, need to refresh the products in the aisle list, perhaps on done?
        Toast.makeText(getApplicationContext(),R.string.standardSavedToast,Toast.LENGTH_LONG).show();
    }
}
