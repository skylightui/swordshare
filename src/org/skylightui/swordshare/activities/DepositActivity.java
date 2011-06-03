package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

    ProgressDialog dialog;

    Button button;

    Context context;

    Intent i;

    String name;
    String username;
    String password;
    String url;
    String resultUrl;

    Uri uri;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.DepositActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.describe);
        Uri uri = null;

        context = this;

        // The Intent that started this activity
        i = this.getIntent();

        // Is the Intent an action being sent from somewhere else?
        if ((i.getAction() != null) && (i.getAction().equals(Intent.ACTION_SEND))) {
            try {
                // Retrieve the Uri of the file being referenced
                uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);

            } catch (Exception e) {
                StackTraceLogger.getStackTraceString(e, TAG);
            }
        }

        ImageView image = (ImageView)findViewById(R.id.image);
        image.setImageURI(uri);

        // Load the preferences
        SharedPreferences settings = getSharedPreferences("SWORDShare", Context.MODE_PRIVATE);
        name = settings.getString("name", "");
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        url = settings.getString("url", "");

        // Check there is at least a URL
        if ((url == null) || ("".equals(url.trim()))) {
            Toast toast = Toast.makeText(getApplicationContext(), "No URL set - please visit settings page!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        title = (TextView)this.findViewById(R.id.title);
        description = (TextView)this.findViewById(R.id.description);

        button = (Button)this.findViewById(R.id.depositbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check the boxes are filled in
                CharSequence text = "";
                if (("".equals(title.getText().toString().trim())) &&
                    ("".equals(description.getText().toString().trim()))) {
                    text = "Please complete the title and description!";
                } else if ("".equals(title.getText().toString().trim())) {
                    text = "Please complete the title!";
                } else if ("".equals(description.getText().toString().trim())) {
                    text = "Please complete the description!";
                }
                if (text.length() > 0) {
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    button.setText("Please wait...");
                    button.setEnabled(false);
                    dialog = ProgressDialog.show(context, "", "Depositing file. Please wait...", true);
                    new DepositTask().execute();
                }
        }});
    }

    private class DepositTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                // Get the context
                Context context = getApplicationContext();

                // Retrieve the Uri of the file being referenced
                uri = (Uri)i.getExtras().get(Intent.EXTRA_STREAM);
                String filename = uri.toString().substring(uri.toString().indexOf(':') + 1);

                FileOutputStream fosmets = openFileOutput("mets.xml", Context.MODE_PRIVATE);

                Hashtable<String, String> metadata = new Hashtable<String, String>();
                metadata.put("creator", name);
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
                deposit.deposit(fispackage, url, username, password);
                resultUrl = deposit.getURL();
                Log.d(TAG, "identifier = " + url);
            } catch (Exception e) {
                dialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Error with deposit - " + e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
                StackTraceLogger.getStackTraceString(e, TAG);
            }
            return "";
        }

         protected void onPostExecute(String result) {
             // Kills the dialog
             dialog.dismiss();

            // Show the deposit receipt page
            setContentView(R.layout.deposit);

            // Set the URL
            TextView turl = (TextView)findViewById(R.id.url);
            resultUrl = resultUrl.replace("http://hdl.handle.net/", url.substring(0, url.indexOf("123456789")));
            resultUrl = resultUrl.replace("sword/deposit", "jspui/handle");
            turl.setText("URL: " + resultUrl);

            // Set the image
            ImageView image = (ImageView)findViewById(R.id.image);
            image.setImageURI(uri);

            // Let the button press take the user to the deposit page
            Button visiturl = (Button)findViewById(R.id.visiturlbutton);
            visiturl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(resultUrl));
                    startActivity(i);
                }});
         }
    }
}