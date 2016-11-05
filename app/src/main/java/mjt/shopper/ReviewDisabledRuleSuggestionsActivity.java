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

/**
 *
 */
public class ReviewDisabledRuleSuggestionsActivity  extends AppCompatActivity {

    public final static int RESUMESTATE_NOTHING = 0;
    public final static int RESUMESTATE_ADJUSTED = 1;
    public static int resume_state = RESUMESTATE_NOTHING;
    public final static String THIS_ACTIVITY = "ReviewDisabledRuleSuggestionActivity";

    public boolean devmode;
    public SharedPreferences sp;
    public  boolean helpoffmode;

    private ShopperDBHelper db = new ShopperDBHelper(this,null,null,1);
    private Cursor csr;
    private TextView done_button;
    private ListView disabledrulelist;
    private ReviewDisabledRuleSuggestionsAdapter rdra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String THIS_METHOD = "onCreate";
        mjtUtils.logMsg(mjtUtils.LOG_INFORMATIONMSG,
                "Invoked",THIS_ACTIVITY,THIS_METHOD,true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_rule_suggestions);
        Intent intent = getIntent();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        devmode = sp.getBoolean(getResources().getString(
                R.string.sharedpreferencekey_developermode),
                false
        );
        helpoffmode = sp.getBoolean(getResources().getString(
                R.string.sharedpreferencekey_showhelpmode),
                false
        );
        done_button = (TextView) this.findViewById(R.id.rrs_done_button);
        disabledrulelist = (ListView) this.findViewById(R.id.rrs_proposedlist);

        csr = db.getDisabledRules();
        rdra = new ReviewDisabledRuleSuggestionsAdapter(this,csr, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        disabledrulelist.setAdapter(rdra);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (resume_state) {
            case RESUMESTATE_ADJUSTED:
                csr = db.getDisabledRules();
                rdra.swapCursor(csr);
                resume_state = RESUMESTATE_NOTHING;
                break;
            default:
                break;
        }
    }
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        if (!(csr == null)) {
            if (!csr.isClosed()) {
                try {
                    csr.close();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        db.close();
    }
    public void onButtonClicked(View view) {
        this.finish();
    }

    public void onClickEnableButton(View view) {
        int tag = (int )view.getTag();
        int csrpos = csr.getPosition();
        csr.moveToPosition(tag);
        db.enableDisabledRule(
                csr.getLong(
                        csr.getColumnIndex(
                                ShopperDBHelper.PRODUCTUSAGE_COLUMN_PRODUCTREF)),
                csr.getLong(
                        csr.getColumnIndex(ShopperDBHelper.PRODUCTUSAGE_COLUMN_AISLEREF))
        );
        csr = db.getDisabledRules();
        rdra.swapCursor(csr);
        resume_state = RESUMESTATE_NOTHING;
    }
}
