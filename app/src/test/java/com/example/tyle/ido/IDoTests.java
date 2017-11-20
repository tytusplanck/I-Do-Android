package com.example.tyle.ido;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static org.junit.Assert.*;


@PrepareForTest({FirebaseDatabase.class})
@RunWith(RobolectricTestRunner.class)
public class IDoTests {

    private ListActivity listActivity;

    @Mock
    Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        FirebaseApp.initializeApp(context);
       listActivity = Robolectric.setupActivity(ListActivity.class);
    }

    @Test
    public void listActivityNotNull() throws Exception {
        assertNotNull(listActivity);
    }

    @Test
    public void progressDialogShownOnStart () throws Exception {
        listActivity.onStart();
        assertTrue(listActivity.progress.isShowing());
    }

    @Test
    public void checkActivityFinishes () throws Exception {
        listActivity.onResume();
        listActivity.isDestroyed();
    }
}