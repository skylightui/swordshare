package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.skylightui.swordshare.R;

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

        thisActivity = this;

        Button button = (Button)this.findViewById(R.id.setupbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(thisActivity, SetupActivity.class));
            }
        });
    }
}
