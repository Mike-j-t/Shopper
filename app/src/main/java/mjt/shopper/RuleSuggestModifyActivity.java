package mjt.shopper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 *
 */
public class RuleSuggestModifyActivity extends AppCompatActivity{

    private final static int RESUMESTATE_NOTHING = 0;
    private final static int RESUMESTATE_ADJUSTED = 1;
    private static int resume_state = RESUMESTATE_NOTHING;
    private final static String THIS_ACTIVTY = "RuleSuggestModifyActivity";

    private boolean devmode;
    private SharedPreferences sp;
    private boolean helpoffmode;

    private ShopperDBHelper db;
    private Cursor csr;
    private ListView suggestedmodifications;
    private LinearLayout helplayout;
    private RuleSuggestModifyAdapter rsma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Rule Suggest Modify invoked",
                THIS_ACTIVTY,
                "onCreate",
                true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rulesuggestmodify);
        suggestedmodifications = (ListView) this.findViewById(R.id.rulesuggestmodify_proposedlist);
        helplayout = (LinearLayout) this.findViewById(R.id.rulesuggestmodify_help_layout);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(
                R.string.sharedpreferencekey_developermode),
                false);
        helpoffmode = sp.getBoolean(getResources().getString(
                R.string.sharedpreferencekey_showhelpmode),
                false);

        if(helpoffmode) {
            helplayout.setVisibility(View.GONE);
        } else {
            helplayout.setVisibility(View.VISIBLE);
        }

        db = new ShopperDBHelper(this,null,null,1);
        csr = db.getSuggestedRules(true,false,30);
        rsma = new RuleSuggestModifyAdapter(this,csr,0);
        suggestedmodifications = (ListView) this.findViewById(R.id.rulesuggestmodify_proposedlist);
        suggestedmodifications.setAdapter(rsma);

        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (resume_state) {
            case RESUMESTATE_ADJUSTED:
                csr = db.getSuggestedRules(true,false,30);
                rsma.swapCursor(csr);
                resume_state = RESUMESTATE_NOTHING;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void onButtonClicked(View view) {
        this.finish();
    }

    public void onModifyClicked(View view) {

        //Tag points to row in cursor
        csr.moveToPosition((int)view.getTag());

        Intent intent = new Intent(this,RuleAddEdit.class);
        intent.putExtra(getResources().getString(
                R.string.intentkey_productid),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_productname),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_aislseid),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_aislename),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_storename),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_storecity),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_storestreet),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_ruleid),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.RULES_TABLE_NAME +
                        ShopperDBHelper.RULES_COLUMN_ID))
                );
        intent.putExtra(getResources().getString(
                R.string.intentkey_rulename),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.RULES_COLUMN_NAME))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_rulemultiplier),
                csr.getInt(csr.getColumnIndex("suggestedinterval"))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_ruleactiveon),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.RULES_COLUMN_ACTIVEON))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_ruleprompt),
                csr.getInt(csr.getColumnIndex(ShopperDBHelper.RULES_COLUMN_PROMPTFLAG))
        );
        intent.putExtra(getResources().getString(
                R.string.intentkey_activitycaller),
                THIS_ACTIVTY + "_MODIFY");
        startActivity(intent);
        resume_state = RESUMESTATE_ADJUSTED;
    }
}
