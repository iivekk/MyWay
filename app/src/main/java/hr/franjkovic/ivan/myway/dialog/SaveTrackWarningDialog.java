package hr.franjkovic.ivan.myway.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import hr.franjkovic.ivan.myway.R;

import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;

public class SaveTrackWarningDialog extends DialogFragment {

    private SharedPreferences dialogSp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setTitle(R.string.track_is_not_saved_warning);
        builder.setMessage(R.string.save_cancel_title);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearSharedPreferences();
                getActivity().finish();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    private void clearSharedPreferences() {
        dialogSp = getActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        dialogSp.edit().clear().apply();
    }
}
