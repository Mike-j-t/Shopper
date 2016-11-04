package mjt.shopper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 *  ReviewDisabledRuleSuggestions
 *
 *  Note! onlyserves to check whether or not activity should be started
 *  If there are no Disabled Rules (really productusages) then there
 *  is no need to display the activity.
 *  Saying that the menu option will only be available if some are disabled
 */
public class ReviewDisabledRuleSuggestions extends AppCompatActivity {
    private final static String LOGTAG = "REVIEWSUGGEST";

    public ReviewDisabledRuleSuggestions(Context context) {
        ShopperDBHelper db = new ShopperDBHelper(context,null,null,1);
        if(db.getDisabledRuleCount() > 0) {
            Intent intent = new Intent(context,ReviewDisabledRuleSuggestionsActivity.class);
            context.startActivity(intent);
        } else {
            Toast.makeText(context,"No Products are Disabled.",Toast.LENGTH_LONG).show();
        }
        return;
    }
}
