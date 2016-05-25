package mjt.shopper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Mike092015 on 24/02/2016.
 */
public class DatabaseInformation extends AppCompatActivity {
    final ShopperDBHelper db = new ShopperDBHelper(this,null,null,1);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_info);

        // Get the Layout that will be dynamically expanded
        // (note topmost LinearLayout has child ScrollView which then has this layout as child)
        LinearLayout ll = (LinearLayout) findViewById(R.id.dbinfo);
        ll.setOrientation(LinearLayout.VERTICAL);

        //Create LayoutParam, want to use this to progressively indent margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0,0,0,0);

        // Do the Title and then the underlying Database Tables
        dbiSetTitle(ll, this);
        dbiAddDBTables(ll, layoutParams, this);
    }

    // Set the Title ie Database Name - add a TextView to existing XML defined layout
    private void dbiSetTitle(LinearLayout ll, Context context) {
        TextView tv = new TextView(context);
        tv.setText(ShopperDBHelper.DATABASE_NAME + "     (database)");
        //tv.setHeight(Constants.HEADING_TEXT_SIZE + 10);
        //tv.setTextSize(Constants.HEADING_TEXT_SIZE);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNormalButton));
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorNormalButtonText));
        ((LinearLayout)ll).addView((TextView) tv); // Don't want to indent, already done in XML
    }
    private void dbiAddDBTables(LinearLayout ll, LinearLayout.LayoutParams llparams, Context context) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);

        // Get list of tables into cursor
        SQLiteDatabase cdb = db.getReadableDatabase();
        String sqlstr = "SELECT * FROM sqlite_master WHERE type = 'table' and name != 'android_metadata'";
        Cursor csr = cdb.rawQuery(sqlstr,null);
        // traverse the list of tables adding 1 Textview for each. Still adding to existing layout
        while (csr.moveToNext()) {
            TextView tv = new TextView(context);
            tv.setText(csr.getString(1).toUpperCase() + "     (table)");
            //tv.setHeight(Constants.LIST_TEXT_SIZE + 10);
            //tv.setTextSize(Constants.LIST_TEXT_SIZE);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewrowodd));
            tv.setPadding(16,0,0,0);
            ll.addView((TextView) tv, llparams);


            // Now go and get the Table's Columns
            dbiAddDBColumns(ll, layoutParams, context, csr.getString(1), cdb);
        }
        // Done with tables so free the cursor
        csr.close();
        cdb.close();

    }
    private void dbiAddDBColumns(LinearLayout ll, LinearLayout.LayoutParams llparams, Context context, String tablename, SQLiteDatabase cdb) {

        int[] colwidths = new int[] {310,130,110,110,110};
        int rowcount = 0;

        // Aaa a Linear Layout (horizontal) for the fields of the Database column
        LinearLayout columnhdr = new LinearLayout(context);
        columnhdr.setOrientation(LinearLayout.HORIZONTAL);
        Context chcontext = columnhdr.getContext();
        ll.addView(columnhdr, llparams);

        // Get the table columns
        String sqlstr = " PRAGMA table_info (" + tablename + ")";
        Cursor csr = cdb.rawQuery(sqlstr,null);

        // Create headings as per the columns names but show them as uppercase
        for(int i = 1; i < csr.getColumnCount(); i++) {
            TextView ctv = new TextView(chcontext);
            ctv.setText(csr.getColumnName(i).toUpperCase());
            ctv.setWidth(colwidths[i - 1]);
            ctv.setTypeface(null, Typeface.BOLD);
            //ctv.setTextSize(chcontext.getResources().getDimension(R.dimen.standard_listview_text_size));
            ctv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlistviewroweven));
            if(i==1) {
                ctv.setPadding(32,0,0,0);
            }
            columnhdr.addView(ctv);
        }
        // Now create the table column information
        while (csr.moveToNext()) {
            LinearLayout columnitems = new LinearLayout(context);
            columnitems.setOrientation(LinearLayout.HORIZONTAL);
            Context cicontext = columnitems.getContext();
            ll.addView(columnitems, llparams);
            for(int i=1; i < (csr.getColumnCount()); i++) {
                TextView ctv = new TextView(cicontext);
                ctv.setText(csr.getString(i));
                //ctv.setTextSize(cicontext.getResources().getDimension(R.dimen.standard_listview_text_size));
                ctv.setWidth(colwidths[i - 1]);
                if(i==1) {
                    ctv.setPadding(32,0,0,0);
                }
                if(csr.getPosition() % 2 == 1) {
                    ctv.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewroweven));
                } else {
                    ctv.setBackgroundColor(ContextCompat.getColor(context,R.color.colorlistviewrowodd));
                }
                columnitems.addView(ctv);
            }
        }
        csr.close();
    }
    public void adifdone(View view) {
        finish();
    }
}