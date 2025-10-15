package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageSearch = findViewById<Button>(R.id.search)
        val imageMedia = findViewById<Button>(R.id.media)
        val imageSettings = findViewById<Button>(R.id.settings)

        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        imageSearch.setOnClickListener(imageClickListener)

        imageMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на медиатеку!", Toast.LENGTH_SHORT).show()
        }

        imageSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на настройки!", Toast.LENGTH_SHORT).show()
        }

    }


}