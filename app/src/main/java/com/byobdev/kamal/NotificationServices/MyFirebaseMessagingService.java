package com.byobdev.kamal.NotificationServices;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import com.byobdev.kamal.AppHelpers.LocationGPS;
import com.byobdev.kamal.InitiativesActivity;
import com.byobdev.kamal.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    String Titulo;
    String Tipo;
    String Descripcion;
    String Latitud;
    String Longitud;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Boolean isGPSOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSOn) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        // Check if message contains a notification payload.
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(key.equals("titulo")){
                Titulo=value;
            }
            else if(key.equals("tipo")){
                Tipo=value;
            }
            else if(key.equals("descripcion")){
                Descripcion=value;
            }
            else if(key.equals("latitud")){
                Latitud=value;
            }
            else if(key.equals("longitud")){
                Longitud=value;
            }
        }


        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(Latitud));
        loc1.setLongitude(Double.parseDouble(Longitud));

        Location loc2 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        int distanceInMeters = (int)loc1.distanceTo(loc2);
        if(distanceInMeters>1000){
            return;
        }
        String Title="Iniciativa de "+Tipo+" a "+distanceInMeters+" metros!";
        String Body=Titulo+": "+Descripcion;
        sendNotification(Title, Body);
    }


    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, InitiativesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}