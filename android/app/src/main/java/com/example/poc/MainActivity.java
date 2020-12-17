package com.example.poc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import androidx.annotation.NonNull;
import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import android.opengl.GLSurfaceView;

public class MainActivity extends FlutterActivity implements Session.SessionListener, PublisherKit.PublisherListener {
    private static final String CHANNEL = "vonage";
    private static String API_KEY = "47043564"; 
    private static String SESSION_ID = "2_MX40NzA0MzU2NH5-MTYwODE5MTcwMTYyOX5mWU9pdVlqbUNuQm5XbENreFVITWZjcjV-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NzA0MzU2NCZzaWc9MDY1OWIxZTZkYjUwODkzMjRmYjU2NjA2MGI2NDk2ZGM5MzI0MTczODpzZXNzaW9uX2lkPTJfTVg0ME56QTBNelUyTkg1LU1UWXdPREU1TVRjd01UWXlPWDVtV1U5cGRWbHFiVU51UW01WGJFTnJlRlZJVFdaamNqVi1mZyZjcmVhdGVfdGltZT0xNjA4MTkyNDk3Jm5vbmNlPTAuODkxMjUzODM5ODA5Njk5NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjA4MTk2MDk4JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private FrameLayout mFrameLayout;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            // TODO
                        }
                );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrameLayout = new FrameLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(300, 300);
        this.addContentView(mFrameLayout,layoutParams);
        requestPermissions();
    }


    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout

            // initialize and connect to the session
            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }


    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mFrameLayout.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }
}