package mjt.shopper;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by Mike092015 on 11/04/2016.
 */
public class UserSettings extends PreferenceActivity {
    public void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserSettingsPreferenceFragment()).commit();
    }
    public static class UserSettingsPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.usersettings);
        }
    }
}
