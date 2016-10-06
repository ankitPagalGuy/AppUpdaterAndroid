package com.appupdaterandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Created by techteam on 04/10/16.
 */

public class SplashActivity  extends AppCompatActivity{

  public static final String TAG = "SplashActivity";

  //Remote Config keys
  private static final String SHOULD_UPDATE_KEY = "should_update";
  private static final String SHOULD_KILL_KEY = "should_kill";
  private static final String VERSION_CODE_KEY = "versionCode";
  private static final String MESSAGE_KEY = "message";
  private FirebaseRemoteConfig mFirebaseRemoteConfig;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    // Get Remote Config instance.
    // [START get_remote_config_instance]
    mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    // [END get_remote_config_instance]

    // Create Remote Config Setting to enable developer mode.
    // Fetching configs from the server is normally limited to 5 requests per hour.
    // Enabling developer mode allows many more requests to be made per hour, so developers
    // can test different config values during development.
    // [START enable_dev_mode]
    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
        .setDeveloperModeEnabled(BuildConfig.DEBUG)
        .build();
    mFirebaseRemoteConfig.setConfigSettings(configSettings);
    // [END enable_dev_mode]

    // Set default Remote Config values. In general you should have in app defaults for all
    // values that you may configure using Remote Config later on. The idea is that you
    // use the in app defaults and when you need to adjust those defaults, you set an updated
    // value in the App Manager console. Then the next time you application fetches from the
    // server, the updated value will be used. You can set defaults via an xml file like done
    // here or you can set defaults inline by using one of the other setDefaults methods.S
    // [START set_default_values]
    mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    // [END set_default_values]


    boolean shouldKill = mFirebaseRemoteConfig.getBoolean(SHOULD_KILL_KEY);
    boolean shouldUpdate = mFirebaseRemoteConfig.getBoolean(SHOULD_UPDATE_KEY);
    long versionCode = mFirebaseRemoteConfig.getLong(VERSION_CODE_KEY);

    if ((versionCode > BuildConfig.VERSION_CODE) && (shouldKill))
      activateKillSwitch();
    else if ((versionCode > BuildConfig.VERSION_CODE) && (shouldUpdate))
      updateApp();


    showDialog();

  }


  private void activateKillSwitch(){

  }

  private void updateApp(){

  }


  private void showDialog(){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");
    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        Toast.makeText(SplashActivity.this,"You clicked yes button", Toast.LENGTH_LONG).show();
      }
    });

    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }



}
