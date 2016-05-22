package mjt.shopper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Mike092015 on 17/02/2016.
 */
public class Database_Inspector_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_inspect);


        final ShopperDBHelper shopperdb = new ShopperDBHelper(this, null, null, 1);
        final Cursor shops_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.SHOPS_TABLE_NAME);
        final Cursor aisles_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.AISLES_TABLE_NAME);
        final Cursor products_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.PRODUCTS_TABLE_NAME);
        final Cursor productusage_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.PRODUCTUSAGE_TABLE_NAME);
        final Cursor rules_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.RULES_TABLE_NAME);
        final Cursor shoplist_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.SHOPLIST_TABLE_NAME);
        final Cursor appvalues_csr = shopperdb.getAllRowsFromTable(ShopperDBHelper.VALUES_TABLE_NAME);

        ((TextView) findViewById(R.id.adi_shopsdb_rowcount)).setText("Rows= " + shopperdb.numberOfShops());
        ListView shops_lv = (ListView) findViewById(R.id.adi_shopsdb_tablelist);
        Database_Inspector_ShopsDB_Adapter shopsdb_adapter = new Database_Inspector_ShopsDB_Adapter(this,shops_csr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        shops_lv.setAdapter(shopsdb_adapter);

        ((TextView) findViewById(R.id.adi_aislesdb_rowcount)).setText("Rows= " + shopperdb.numberOfAisles());
        ListView aisles_lv = (ListView) findViewById(R.id.adi_aislesdb_tablelist);
        Database_Inspector_AislesDB_Adapter aislesdb_adapter = new Database_Inspector_AislesDB_Adapter(this, aisles_csr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        aisles_lv.setAdapter(aislesdb_adapter);

        ((TextView) findViewById(R.id.adi_productsdb_rowcount)).setText("Rows= " + shopperdb.numberOfProducts());
        ListView products_lv = (ListView) findViewById(R.id.adi_productsdb_tablelist);
        Database_Inspector_ProductsDB_Adadpter products_adapter = new Database_Inspector_ProductsDB_Adadpter(this, products_csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        products_lv.setAdapter(products_adapter);

        ((TextView) findViewById(R.id.adi_productusagedb_rowcount)).setText("Rows= " + shopperdb.numberOfProductUsages());
        ListView productusage_lv = (ListView) findViewById(R.id.adi_productusagedb_tablelist);
        Database_Inspector_ProductUsageDB_Adapter productusage_adapter = new Database_Inspector_ProductUsageDB_Adapter(this, productusage_csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        productusage_lv.setAdapter(productusage_adapter);

        ((TextView) findViewById(R.id.adi_rulesdb_rowcount)).setText("Rows= " + shopperdb.numberOfRules());
        ListView rules_lv = (ListView) findViewById(R.id.adi_rulesdb_tablelist);
        Database_Inspector_RulesDB_Adapter rules_adapter = new Database_Inspector_RulesDB_Adapter(this,rules_csr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        rules_lv.setAdapter(rules_adapter);

        ((TextView) findViewById(R.id.adi_shoplistdb_rowcount)).setText("Rows= " + shopperdb.numberofShoppingListEntries());
        ListView shoplist_lv = (ListView) findViewById(R.id.adi_shoplistdb_tablelist);
        Database_Inspector_ShopListDB_Adapter shoplist_adapter = new Database_Inspector_ShopListDB_Adapter(this,shoplist_csr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        shoplist_lv.setAdapter(shoplist_adapter);

        ((TextView) findViewById(R.id.adi_appvaluesdb_rowcount)).setText("Rows= " + shopperdb.numberofAppValues());
        ListView appvalues_lv = (ListView) findViewById(R.id.adi_appvaluesdb_tablelist);
        Database_Inspector_ValuesDB_Adapter appvalues_adapter = new Database_Inspector_ValuesDB_Adapter(this,appvalues_csr,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        appvalues_lv.setAdapter(appvalues_adapter);

    }

    // Onclick handlers
    public void showshops(View view) { flipviews(ShopperDBHelper.SHOPS_TABLE_NAME); }
    public void showaisles(View view) { flipviews(ShopperDBHelper.AISLES_TABLE_NAME); }
    public void showproducts(View view) { flipviews(ShopperDBHelper.PRODUCTS_TABLE_NAME); }
    public void showproductusage(View view) { flipviews(ShopperDBHelper.PRODUCTUSAGE_TABLE_NAME); }
    public void showrules(View view) { flipviews(ShopperDBHelper.RULES_TABLE_NAME); }
    public void showshoplist(View view) { flipviews(ShopperDBHelper.SHOPLIST_TABLE_NAME);}
    public void showappvalues(View view) { flipviews(ShopperDBHelper.VALUES_TABLE_NAME); }

    public void flipviews(String viewtosee) {

        // Turn all off
        findViewById(R.id.adi_stores).setVisibility(View.GONE);
        findViewById(R.id.adi_aisles).setVisibility(View.GONE);
        findViewById(R.id.adi_products).setVisibility(View.GONE);
        findViewById(R.id.adi_productusage).setVisibility(View.GONE);
        findViewById(R.id.adi_rules).setVisibility(View.GONE);
        findViewById(R.id.adi_shoplist).setVisibility(View.GONE);
        findViewById(R.id.adi_appvalues).setVisibility(View.GONE);

        switch(viewtosee) {
            case ShopperDBHelper.SHOPS_TABLE_NAME:
                findViewById(R.id.adi_stores).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.AISLES_TABLE_NAME:
                findViewById(R.id.adi_aisles).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.PRODUCTS_TABLE_NAME:
                findViewById(R.id.adi_products).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.PRODUCTUSAGE_TABLE_NAME:
                findViewById(R.id.adi_productusage).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.RULES_TABLE_NAME:
                findViewById(R.id.adi_rules).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.SHOPLIST_TABLE_NAME:
                findViewById(R.id.adi_shoplist).setVisibility(View.VISIBLE);
                break;
            case ShopperDBHelper.VALUES_TABLE_NAME:
                findViewById(R.id.adi_appvalues).setVisibility(View.VISIBLE);
            default:
                break;
        }
    }
    public void adibtndone(View view) {
        finish();
    }
}