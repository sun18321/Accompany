package com.play.accompany.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.play.accompany.R
import com.play.accompany.fragment.TopLivingSoundFragment

class EggActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_egg)

        supportFragmentManager.beginTransaction().replace(R.id.frame, TopLivingSoundFragment()).commitAllowingStateLoss()
    }
}
