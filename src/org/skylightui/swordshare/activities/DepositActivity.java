package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.skylightui.swordshare.R;
import org.skylightui.swordshare.util.SimpleSWORDDeposit;
import org.skylightui.swordshare.util.StackTraceLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

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

        // The URL of the deposit, if applicable
        String url = "unknown";

        // Is the Intent an action being sent from somewhere else?
        if ((i.getAction() != null) && (i.getAction().equals(Intent.ACTION_SEND))) {
            try {
                Context context = getApplicationContext();
                // Retrieve the Uri of the file being referenced
                Uri uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);
                String filename = uri.toString().substring(uri.toString().indexOf(':') + 1);

                CharSequence text = "Data: " + i.getType() + " from " + filename;
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                FileOutputStream fosmets = openFileOutput("mets.xml", Context.MODE_PRIVATE);

                Hashtable<String, String> metadata = new Hashtable<String, String>();
                metadata.put("creator", "Lewis, Stuart");
                metadata.put("title", "Test title");
                metadata.put("description", "Test description");

                Log.d(TAG, "About to initiate deposit");
                SimpleSWORDDeposit deposit = new SimpleSWORDDeposit(filename, i.getType(), metadata, fosmets);

                InputStream content = context.getContentResolver().openInputStream(uri);

                Log.d(TAG, "About to call makePackage");
                FileOutputStream foszip = openFileOutput("package.zip", Context.MODE_PRIVATE);
                FileInputStream fismets = openFileInput("mets.xml");
                deposit.makePackage(content, uri.toString(), foszip, fismets);

                Log.d(TAG, "About to call deposit");
                FileInputStream fispackage = openFileInput("package.zip");
                deposit.deposit(fispackage, "http://192.168.2.247:8080/sword/deposit/123456789/766", "sword@swordapp.org", "sword");
                url = deposit.getURL();
                Log.d(TAG, "identifier = " + url);
            } catch (Exception e) {
                StackTraceLogger.getStackTraceString(e, TAG);
            }
        }

        setContentView(R.layout.deposit);
        TextView turl = (TextView)findViewById(R.id.url);
        turl.setText("URL: " + url);
    }
}
