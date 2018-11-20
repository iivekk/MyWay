package hr.franjkovic.ivan.myway.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.dialog.DeleteAllDataWarningDialog;

public class CustomPreferenceFragment extends PreferenceFragment {

    private Preference clearData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_settings);

        clearData = findPreference(getString(R.string.clear_data_key));
        clearData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DeleteAllDataWarningDialog dialog = new DeleteAllDataWarningDialog();
                dialog.show(getFragmentManager(), "Clear data");
                return true;
            }
        });
    }
}
