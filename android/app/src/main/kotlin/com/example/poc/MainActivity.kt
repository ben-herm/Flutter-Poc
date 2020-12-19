package com.example.poc

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.opentok.android.*
import com.opentok.android.PublisherKit.PublisherListener
import com.opentok.android.Session.SessionListener
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : FlutterActivity(), SessionListener, PublisherListener {
    private lateinit var mSession: Session
//    private var mSession: Session? = null
    private val mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    private var mFrameLayout: FrameLayout? = null
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
                .setMethodCallHandler { call: MethodCall?, result: MethodChannel.Result? -> }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFrameLayout = FrameLayout(this)
        val layoutParams = ViewGroup.LayoutParams(300, 300)
        addContentView(mFrameLayout, layoutParams)
        requestPermissions()
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // initialize view objects from your layout

            // initialize and connect to the session
            mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
            mSession.setSessionListener(this)
            mSession.connect(TOKEN)
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, *perms)
        }
    }

    override fun onConnected(session: Session) {
        Log.i(LOG_TAG, "Session Connected")
    }

    override fun onDisconnected(session: Session) {
        Log.i(LOG_TAG, "Session Disconnected")
    }

    override fun onStreamReceived(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Received")
        Log.i(LOG_TAG, "Stream Received")
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession!!.subscribe(mSubscriber)
            val subscriberView = mSubscriber!!.view
            mFrameLayout!!.addView(subscriberView)
        }
    }

    override fun onStreamDropped(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Dropped")
    }

    override fun onError(session: Session, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.message)
    }

    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {}
    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {}
    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {}

    companion object {
        private const val CHANNEL = "vonage"
        private const val API_KEY = "47043564"
        private const val SESSION_ID = "2_MX40NzA0MzU2NH5-MTYwODM4NTU1ODYxMn5LTkJVc0p0bjZibXd6dVpxekllSWR2d1F-fg"
        private const val TOKEN = "T1==cGFydG5lcl9pZD00NzA0MzU2NCZzaWc9MDU1ODkzNTA3NGJjOWU5OTZlNTZlNTJmODg3YWQzZWI4Mjc0NjU1OTpzZXNzaW9uX2lkPTJfTVg0ME56QTBNelUyTkg1LU1UWXdPRE00TlRVMU9EWXhNbjVMVGtKVmMwcDBialppYlhkNmRWcHhla2xsU1dSMmQxRi1mZyZjcmVhdGVfdGltZT0xNjA4Mzg4NDQ1Jm5vbmNlPTAuNzI4NTkzMjA4Nzc2MDE4MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjEwOTgwNDQ4JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9"
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
    }
}