package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.skylightui.swordshare.R;

public class SetupActivity extends Activity {

    // The application preferences
    private SharedPreferences settings;

    // The text fields
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvPassword;
    private TextView tvUrl;

    // This activity (used in inner classes)
    private Activity thisActivity;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.SetupActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        // Set this activity  and context
        thisActivity = this;

        // Load the preferences
        settings = getPreferences(Context.MODE_PRIVATE);
        String name = settings.getString("name", "");
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        String url = settings.getString("url", "http://");

        // Display the preferences
        tvName = (TextView)this.findViewById(R.id.name);
        tvName.setText(name);
        tvUsername = (TextView)this.findViewById(R.id.username);
        tvUsername.setText(username);
        tvPassword = (TextView)this.findViewById(R.id.password);
        tvPassword.setText(password);
        tvUrl = (TextView)this.findViewById(R.id.url);
        tvUrl.setText(url);

        // Listen for the button click
        Button button = (Button)this.findViewById(R.id.donebutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Store the parameters
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("name", tvName.getText().toString());
                editor.putString("username", tvUsername.getText().toString());
                editor.putString("password", tvPassword.getText().toString());
                editor.putString("url", tvUrl.getText().toString());
                editor.commit();

                // Say we've updated the settings
                Toast toast = Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT);
                toast.show();

                // Now close the activity
                thisActivity.finish();
            }
        });
    }
}