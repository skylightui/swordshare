package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.skylightui.swordshare.R;
import org.skylightui.swordshare.util.SimpleSWORDDeposit;
import org.skylightui.swordshare.util.StackTraceLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class DepositActivity extends Activity {

    TextView title;
    TextView description;
    Intent i;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.DepositActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.describe);
        String filename = "unknown";
        Uri uri = null;

        // The Intent that started this activity
        i = this.getIntent();

        // Is the Intent an action being sent from somewhere else?
        if ((i.getAction() != null) && (i.getAction().equals(Intent.ACTION_SEND))) {
            try {
                Context context = getApplicationContext();
                // Retrieve the Uri of the file being referenced
                uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);

            } catch (Exception e) {
                StackTraceLogger.getStackTraceString(e, TAG);
            }
        }

        ImageView image = (ImageView)findViewById(R.id.image);
        image.setImageURI(uri);

        title = (TextView)this.findViewById(R.id.title);
        description = (TextView)this.findViewById(R.id.description);

        Button button = (Button)this.findViewById(R.id.depositbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Context context = getApplicationContext();
                    // Retrieve the Uri of the file being referenced
                    Uri uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);
                    String filename = uri.toString().substring(uri.toString().indexOf(':') + 1);

                    FileOutputStream fosmets = openFileOutput("mets.xml", Context.MODE_PRIVATE);

                    Hashtable<String, String> metadata = new Hashtable<String, String>();
                    metadata.put("creator", "Lewis, Stuart");
                    metadata.put("title", title.getText().toString());
                    metadata.put("description", description.getText().toString());

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
                    String url = deposit.getURL();
                    Log.d(TAG, "identifier = " + url);
                    setContentView(R.layout.deposit);
                    TextView turl = (TextView)findViewById(R.id.url);
                    turl.setText("URL: " + url);
                } catch (Exception e) {
                    StackTraceLogger.getStackTraceString(e, TAG);
                }
            }
        });
    }
}