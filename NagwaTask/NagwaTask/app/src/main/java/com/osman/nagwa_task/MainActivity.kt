package com.osman.nagwa_task

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osman.nagwa_task.databinding.ActivityMainBinding
import rx.Observable
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Expand activity under the gesture navigation bar and toolbar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Fixes the Full Screen black bar in screen with notch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Observable.timer(3, TimeUnit.SECONDS).subscribe {
            Log.w("sssss", "timer 1 onNext: $it")
        }

        Observable.timer(3, TimeUnit.SECONDS).subscribe({
            Log.w("sssss", "timer 2 onNext: $it")
        }, {
            Log.w("sssss", "timer 2 onError: ${it.message}", it)
        })

        Observable.timer(3, TimeUnit.SECONDS).subscribe({
            Log.w("sssss", "timer 3 onNext: $it")
        }, {
            Log.w("sssss", "timer 3 onError: ${it.message}", it)
        }, {
            Log.w("sssss", "timer 3 onComplete")
        })

    }

}