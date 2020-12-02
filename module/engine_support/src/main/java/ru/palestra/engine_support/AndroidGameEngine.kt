package ru.palestra.engine_support

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import ru.palestra.engine.test.TestLog
import ru.palestra.engine.test.render.SimpleRender
import ru.palestra.engine_support.utils.supportES2

object AndroidGameEngine : LifecycleObserver {

    private lateinit var simpleRender: SimpleRender
    private lateinit var gameView: GLSurfaceView

    fun createGame(context: Context, lifecycleOwner: LifecycleOwner): View {
        Log.e("TAG", "From EDITED2! ru.palestra.engine_support.AndroidGameEngine")
        TestLog().logTest()



        checkSupportES2(context)

        lifecycleOwner.lifecycle.addObserver(this)

        simpleRender = SimpleRender()
        gameView = GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(simpleRender)
        }

        return gameView
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onGamePaused() {
        gameView.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onGameResumed() {
        gameView.onResume()
    }

    private fun checkSupportES2(context: Context) {
        if (!supportES2(context)) {
            Toast.makeText(context, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show()
            return
        }
    }
}