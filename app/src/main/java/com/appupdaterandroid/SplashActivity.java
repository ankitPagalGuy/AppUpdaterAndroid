package com.appupdaterandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
  String message;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);


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

   fetch();


  }



  /**
   * Fetch discount from server.
   */
  private void fetch() {


    long cacheExpiration = 3600; // 1 hour in seconds.
    // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
    // the server.
    if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
      cacheExpiration = 0;
    }

    // [START fetch_config_with_callback]
    // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
    // fetched and cached config would be considered expired because it would have been fetched
    // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
    // throttling is in progress. The default expiration duration is 43200 (12 hours).
    mFirebaseRemoteConfig.fetch(cacheExpiration)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Toast.makeText(SplashActivity.this, "Fetch Succeeded",
                  Toast.LENGTH_SHORT).show();

              // Once the config is successfully fetched it must be activated before newly fetched
              // values are returned.
              mFirebaseRemoteConfig.activateFetched();
            } else {
              Toast.makeText(SplashActivity.this, "Fetch Failed",
                  Toast.LENGTH_SHORT).show();
            }
            checkApp();
          }
        });
    // [END fetch_config_with_callback]
  }



  private void checkApp(){


    boolean shouldKill = mFirebaseRemoteConfig.getBoolean(SHOULD_KILL_KEY);
    boolean shouldUpdate = mFirebaseRemoteConfig.getBoolean(SHOULD_UPDATE_KEY);
    long versionCode = mFirebaseRemoteConfig.getLong(VERSION_CODE_KEY);
    message = mFirebaseRemoteConfig.getString(MESSAGE_KEY);

    if ((versionCode > BuildConfig.VERSION_CODE) && (shouldKill))
      activateKillSwitch();
    else if ((versionCode > BuildConfig.VERSION_CODE) && (shouldUpdate))
      updateApp();
    else {
      Intent a = new Intent(SplashActivity.this, MainActivity.class);
      startActivity(a);
    }
  }


  private void activateKillSwitch(){
    showDialog(false);
  }

  private void updateApp(){
   showDialog(true);
  }


  private void showDialog(boolean dismiss){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage(message);
    alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        Toast.makeText(SplashActivity.this,"You clicked yes button", Toast.LENGTH_LONG).show();
      }
    });


    if (dismiss) {
      alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
          finish();
        }
      });
    }
    else {
      alertDialogBuilder.setCancelable(false);
    }

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }



}
