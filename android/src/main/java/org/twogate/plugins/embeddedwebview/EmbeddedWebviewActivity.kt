package org.twogate.plugins.embeddedwebview

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.WindowMetrics
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

public class EmbeddedWebviewActivity : AppCompatActivity() {
    public fun createIntent(context: Context, url: String): Intent {
        val intent = Intent(context, EmbeddedWebviewActivity::class.java)
        return intent;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = Resources.getSystem().displayMetrics
        val width = displayMetrics.widthPixels / displayMetrics.density
        val height = (displayMetrics.heightPixels - 400) / displayMetrics.density

        val sampleLayoutParams = LinearLayout.LayoutParams(width.toInt(), height.toInt())
        val sampleLayout = LinearLayout(this)
        sampleLayout.layoutParams = sampleLayoutParams
        setContentView(sampleLayout)

        // create containe

//        val dip = 14.0f
//        val r: Resources = resources
//        val px = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            dip,
//            r.displayMetrics
//        )
//        Log.i(EmbeddedWebview)
//        container.orientation = LinearLayout.VERTICAL
//        val containerLayoutParams =
//
//        Log.i("EmbeddedWebview", "onCreate")
    }
}