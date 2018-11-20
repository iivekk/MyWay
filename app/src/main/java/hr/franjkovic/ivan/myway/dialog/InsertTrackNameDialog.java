package hr.franjkovic.ivan.myway.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.database.Track;
import hr.franjkovic.ivan.myway.services.SaveTrack;

import static hr.franjkovic.ivan.myway.database.MyWayApp.database;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.TRACK_NAME;

public class InsertTrackNameDialog extends DialogFragment {

    @BindView(R.id.etEditText)
    EditText etTrackName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.input_track_name_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_text_dialog, null);
        builder.setView(v);

        ButterKnife.bind(this, v);
        etTrackName.setText(etTrackNameHint());

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent saveTrackIntent = new Intent(getActivity(), SaveTrack.class);
                saveTrackIntent.putExtra(TRACK_NAME, etTrackName.getText().toString());
                getActivity().startService(saveTrackIntent);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return alertDialog;
    }

    private String etTrackNameHint() {
        List<Track> tracks = database.trackDao().getAll();
        if (tracks.size() > 0) {
            return getString(R.string.default_track_name) + " " + (tracks.get(0).getTrackId() + 1);
        } else {
            return getString(R.string.default_track_name);
        }
    }

}
