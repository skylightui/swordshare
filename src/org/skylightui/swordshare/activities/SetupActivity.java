package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.skylightui.swordshare.R;

public class SetupActivity extends Activity {

    private Activity thisActivity;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.SetupActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        thisActivity = this;

        Button button = (Button)this.findViewById(R.id.donebutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}