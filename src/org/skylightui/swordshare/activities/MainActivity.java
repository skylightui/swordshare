package org.skylightui.swordshare.activities;

import android.app.Activity;
import android.os.Bundle;
import org.skylightui.swordshare.R;

public class MainActivity extends Activity
{
    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.activities.MainActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
