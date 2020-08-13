package ru.palestra.longroad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.palestra.engine_support.AndroidGameEngine


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            AndroidGameEngine.createGame(this, this)
        )
    }
}