package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import org.skylightui.swordshare.R;

public class DepositActivity extends Activity
{
    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.DepositActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // The Intent that started this activity
        Intent i = this.getIntent();

        // Is the Intent an action being sent from somewhere else?
        if ((i.getAction() != null) && (i.getAction().equals(Intent.ACTION_SEND))) {
            Context context = getApplicationContext();
                // Retrieve the Uri of the file being referenced
                Uri uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);
                String filename = uri.toString();

                CharSequence text = "Data: " + i.getType() + " from " + filename;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
        }

        setContentView(R.layout.deposit);
    }
}
