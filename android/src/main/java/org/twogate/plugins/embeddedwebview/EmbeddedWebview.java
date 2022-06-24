package org.twogate.plugins.embeddedwebview;

import android.util.Log;

public class EmbeddedWebview {

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }
}
