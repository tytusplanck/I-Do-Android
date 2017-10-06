package com.example.tyle.ido;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tyle.ido.R;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Session implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static Session singletonObject;
    /** A private Constructor prevents any other class from instantiating. */

    private static final String TAG = "Session";

    // Context
    private Context _context;
    public GoogleApiClient mGoogleApiClient;

    public Session(Context context)
    {
        this._context = context;
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(_context)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .addConnectionCallbacks(this).build();
        Log.d(TAG, "Help me I keep connecting.");
//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("google base class", "onConnected invoked");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("google base class", "onConnectionSuspended invoked");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i("google base class", "onConnectionFailed invoked");

    }

    public void googleLogOut(Session session) {
        Auth.GoogleSignInApi.signOut(session.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(_context, "G+ loggedOut", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
