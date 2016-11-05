package mjt.shopper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 */
public class RuleSuggestionActivity extends AppCompatActivity {

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMMESTATE_ADJUSTED = 1;
    public static int resume_state = RESUMESTATE_NOTHING;
    public final static String THIS_ACTIVITY = "RuleSuggestionActivity";

    public boolean devmode;
    public SharedPreferences sp;
    public boolean helpoffmode;

    private ShopperDBHelper db;
    private Cursor csr;
    private TextView done_button;
    private ListView suggestedrulelist;
    private RuleSuggestionAdapter rsa;
    private long nextrulesuggestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Rule Suggestion Invoked",
                THIS_ACTIVITY,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_suggestion);
        Intent intent = getIntent();

        done_button = (TextView) findViewById(R.id.rulesuggestion_done_button);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_developermode),
                false);
        helpoffmode = sp.getBoolean(getResources().getString(R.string.sharedpreferencekey_showhelpmode),
                false);
        nextrulesuggestion = intent.getLongExtra(getResources().getString(R.string.intentkey_nextrulesuggestion),0);

        db = new ShopperDBHelper(this,null,null,1);
        csr = db.getSuggestedRules();
        suggestedrulelist = (ListView) findViewById(R.id.rulesuggestion_proposedlist);
        rsa = new RuleSuggestionAdapter(this,csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        suggestedrulelist.setAdapter(rsa);
    }

    @Override
    protected void onResume() {

        super.onResume();
        switch (resume_state) {
            case RESUMMESTATE_ADJUSTED:
                csr = db.getSuggestedRules();
                rsa.swapCursor(csr);
                resume_state = RESUMESTATE_NOTHING;
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!csr.isClosed()) {
            try {
                csr.close();
                db.enableDismissedRules();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }
    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.rulesuggestion_done_button: {
                mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,"Method Call",
                        THIS_ACTIVITY,
                        "done button",
                        devmode);
                db.alterLongValue(Constants.LASTRULESUGGESTION,
                        nextrulesuggestion);
                csr.close();
                db.close();
                this.finish();
            }
        }
    }
    public void onCLickAddButton(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Method Call",
                THIS_ACTIVITY,
                "Add Button",false);
        Integer tag = (Integer) view.getTag();
        int csrpos = csr.getPosition();
        csr.moveToPosition(tag);
        Toast.makeText(this,"Add Button Clicked tag=" + tag.toString() + ")",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,RuleAddEdit.class);
        intent.putExtra(getResources().getString(R.string.intentkey_productid),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF)));
        intent.putExtra(getResources().getString(R.string.intentkey_productname),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.PRODUCTS_COLUMN_NAME)));
        intent.putExtra(getResources().getString(R.string.intentkey_aislseid),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF)));
        intent.putExtra(getResources().getString(R.string.intentkey_aislename),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.AISLES_COLUMN_NAME)));
        intent.putExtra(getResources().getString(R.string.intentkey_storename),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_NAME)));
        intent.putExtra(getResources().getString(R.string.intentkey_storecity),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_CITY)));
        intent.putExtra(getResources().getString(R.string.intentkey_storestreet),
                csr.getString(csr.getColumnIndex(ShopperDBHelper.SHOPS_COLUMN_STREET)));
        intent.putExtra(getResources().getString(R.string.intentkey_rulemultiplier),
                csr.getInt(csr.getColumnIndex("suggestedinterval")));
        intent.putExtra(getResources().getString(R.string.intentkey_activitycaller),
                THIS_ACTIVITY + "_ADD");
        startActivity(intent);
        resume_state = RESUMMESTATE_ADJUSTED;
    }

    public void onClickSkipButton(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Method Call",
                THIS_ACTIVITY,
                "Dismiss Button",false);
        Integer tag = (Integer) view.getTag();
        csr.moveToPosition(tag);
        db.dismissRule(csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF)),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF)));
        csr = db.getSuggestedRules();
        rsa.swapCursor(csr);
        resume_state = RESUMESTATE_NOTHING;
    }

    public void onClickDisableButton(View view) {
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Method Call",
                THIS_ACTIVITY,
                "Disable Button",false);
        Integer tag = (Integer) view.getTag();
        int csrpos = csr.getPosition();
        csr.moveToPosition(tag);
        db.disableRule(csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF)),
                csr.getLong(csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF)));
        csr = db.getSuggestedRules();
        rsa.swapCursor(csr);
        resume_state = RESUMESTATE_NOTHING;
    }
}
