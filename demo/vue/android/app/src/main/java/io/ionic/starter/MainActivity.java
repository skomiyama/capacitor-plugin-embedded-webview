package io.ionic.starter;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        registerPlugin(org.twogate.plugins.embeddedwebview.EmbeddedWebViewPlugin.class);
    }
}
