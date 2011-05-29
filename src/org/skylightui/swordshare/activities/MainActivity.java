package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.skylightui.swordshare.R;

import java.util.Map;

public class MainActivity extends Activity
{
    private Activity thisActivity;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.MainActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Store the activity for later use
        thisActivity = this;

        this.showPreferences();

        Button button = (Button)this.findViewById(R.id.setupbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(thisActivity, SetupActivity.class));
            }
        });
    }

    public void onResume() {
        super.onResume();
        this.showPreferences();
    }

    private void showPreferences() {
        // Set the preferences
        SharedPreferences settings = getSharedPreferences("SWORDShare", Context.MODE_PRIVATE);
        String name = settings.getString("name", "Unknown");
        String username = settings.getString("username", "Unknown");
        String url = settings.getString("url", "Unknown");
        TextView tvName = (TextView)this.findViewById(R.id.name);
        tvName.setText("Name: " + name);
        TextView tvUsername = (TextView)this.findViewById(R.id.username);
        tvUsername.setText("Username: " + username);
        TextView tvUrl = (TextView)this.findViewById(R.id.url);
        tvUrl.setText("Deposit URL: " + url);
    }
}
