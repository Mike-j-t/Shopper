package mjt.shopper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import java.util.Date;

/**
 * Class to hold the checkRuleSuggestions method. This
 * will invoke the RuleSuggestionsActivity if criteria is met.
 *
 * Criteria is dependent upon a number of factors
 *
 * Whether or not user preferences allow rule sugestion
 * Whether or not Rule Suggestion is now due
 * Whether or not Rule Suggestion has been forced (param to checkRuleSuggestions)
 * Only if there are Rules to Suggest
 *
 */
@SuppressLint("Registered")
public class RuleSuggestion extends AppCompatActivity{

    private final static String LOGTAG = "RULESUGGEST";
    private final static String THIS_ACTIVITY = "RuleSuggestion";

    /**
     * Invoke RuleSuggestionActivity if criteria met.
     *
     * 1) User preference allowrulesuggest must be true
     * 2) If Force is false then only if RuleSuggestion is now due
     *      that is, if the current date is or is greater than the date
     *      stored in the values table as the last date rulesuggestion
     *      was actioned
     *     If Force is true then RuleSuggestion being due is ignored hence
     *     "Force".
     * 3) Only if there are Rules to can be suggested according to the criteria
     *        of the SQL select as per ShopperDBHelper.getSuggestedRules
     * @param context   context from the caller
     * @param force     true if to force Rule Suggestion, otherwiese false
     */
    static public void checkRuleSuggestions(Context context, boolean force) {
        // Prepare to get values from Shared Preferences
        PreferenceManager.setDefaultValues(context,R.xml.usersettings,false);
         SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // Get whether or not shared preferences allow Rule suggestion
        boolean allowrulesuggestion = sp.getBoolean(
                context.getResources().getString(
                        R.string.sharedpreferencekey_allowrulesuggest
                ),
                true);
        // If Rule suggesion not allowed then that's it, so return
        Log.i(LOGTAG,"Allowed = " + Boolean.toString(allowrulesuggestion));
        if((!allowrulesuggestion) && (!force)) {
            return;
        }

        // Need to get rule suggestion frequency in days
        long rsf = Long.parseLong(
                sp.getString(
                context.getResources().getString(
                        R.string.sharedpreferencekey_rulesuggestfrequency),
                        "30")
        );
        //Convert rule suggestion frequency to microseconds
        rsf = rsf * 1000 * 60 * 60 * 24;

        // Retrieve the last time rules were suggested from the values table
        ShopperDBHelper db = new ShopperDBHelper(context,null,null,1);
        long lastrulesuggestiondate = db.getLongValue(Constants.LASTRULESUGGESTION);
        long rulesuggestnext = lastrulesuggestiondate + rsf;

        // Get the current date/time as long
        Date date = new Date();
        String now = date.toString();
        long now_as_millisecs = date.getTime();
        boolean todorulesuggest = now_as_millisecs >= rulesuggestnext;

        //TODO remove debugging info when all is OK
        // Debugging info
        Log.i(LOGTAG,
                "Rule last suggested on: " +
                        Long.toString(lastrulesuggestiondate) +
                        "\n\tNext Suggestion should be on: " +
                        Long.toString(rulesuggestnext) +
                        "\n\tDate now is                 : " +
                        Long.toString(now_as_millisecs) +
                        "\nTherefore to do rule suggestion now is" +
                        Boolean.toString(todorulesuggest)
        );
        // Not date/time yet to look at rule suggestion so finish
        if ((!todorulesuggest) && (!force)) {
            db.close();
            return;
        }

        // Get flag for how to determine next rule suggestion date/time
        boolean nextfromwhendone = sp.getBoolean(
                context.getResources().getString(
                        R.string.sharedpreferencekey_nextdatefromwhendone),
                true
        );

        // Get rule suggestions from database
        if(todorulesuggest || force) {
            db = new ShopperDBHelper(context,null,null,1);
            Cursor csr = db.getSuggestedRules(false,true,ShopperDBHelper.RULESUGGESTFLAG_ONLYCLEAR);
            Toast.makeText(context, "Number of Rule Suggestions is " +
                            Integer.toString(csr.getCount()),
                    Toast.LENGTH_LONG).show();
            // If rule suggestions (i.e. rows from Database returned)
            // then do the rule suggestion
            if(csr.getCount() > 0 ) {
                csr.close();
                Intent intent = new Intent(context,RuleSuggestionActivity.class);
                intent.putExtra(context.getResources().getString(R.string.intentkey_activitycaller),THIS_ACTIVITY);
                intent.putExtra(context.getResources().getString(R.string.intentkey_rulesuggestionsforced),force);
                String ikey = context.getResources().getString(R.string.intentkey_nextrulesuggestion);
                if(nextfromwhendone) {
                    intent.putExtra(ikey,(now_as_millisecs + rsf));
                } else {
                    intent.putExtra(ikey,rulesuggestnext);
                }
                context.startActivity(intent);
            } else {
                if(force) {
                    AlertDialog.Builder nosuggestions = new AlertDialog.Builder(context);
                    nosuggestions.setTitle("No Rule Suggestions");
                    nosuggestions.setMessage("No suggestions have been found." +
                            "\n\nFor a rule to be suggested for a stocked product. " +
                            "This is very likely not due to an error, rather that the " +
                            "selection process has excluded all potential suggestions." +
                            "\n\nFor a stocked product to be included, it:"+
                            "\n\tMust have been purchased (marked as bought on the Shopping" +
                            " List)." +
                            "\n\tThe period between the first and last purchase must be " +
                            " 90 days or more." +
                            "\n\tThere must not be an existing Rule for the stocked product." +
                            "\n\tFinally, the stocked product must not be marked as not being " +
                            "eligible for suggestion (i.e DISABLED was clicked in the Rule " +
                            " suggestion list)."
                    );
                    nosuggestions.setCancelable(true);
                    nosuggestions.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    nosuggestions.show();
                }
            }
            csr.close();
            db.close();
        }
    }
}
