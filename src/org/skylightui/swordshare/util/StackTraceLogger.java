package org.skylightui.swordshare.util;

import android.util.Log;

/**
 * A class to display a traditional Java stack trace in the android debugger
 *
 * @author Stuart Lewis (stuart@stuartlewis.com)
 */
public class StackTraceLogger {

    /**
     * Display a stack trace
     *
     * @param e The Exception
     * @param tag The tag to use in the debugger
     */
    public static void getStackTraceString(Exception e, String tag) {
        Log.e(tag, e.toString());
        for (StackTraceElement ste : e.getStackTrace()) {
           Log.e(tag, ste.toString());
        }
    }
}