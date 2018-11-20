package hr.franjkovic.ivan.myway.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import hr.franjkovic.ivan.myway.R;

import static hr.franjkovic.ivan.myway.database.MyWayApp.database;

public class DeleteAllDataWarningDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setTitle(R.string.delete_all_track_data_title);
        builder.setMessage(R.string.delete_all_tracks_msg);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.trackDao().deleteAllData();
                Toast.makeText(getActivity(), R.string.data_is_deleted, Toast.LENGTH_SHORT).show();
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
}
