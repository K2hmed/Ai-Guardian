package com.example.mysos.ShakeServices;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.mysos.Contacts.ContactModel;
import com.example.mysos.Contacts.DbHelper;
import com.example.mysos.R;
import com.example.mysos.SplashActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class SensorService extends Service {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);

            String msg = "Internet connection Lost, please check up on user";
            notificationAlert(getBaseContext(), "Connection Lost", msg);
            DbHelper db = new DbHelper(SensorService.this);
            List<ContactModel> list = db.getAllContacts();
            for (ContactModel c : list) {
                sendSMS(msg, c.getPhoneNo());
            }
        }


        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        }
    };

    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();

        //start the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);

        FirebaseDatabase.getInstance().getReference("fir")
                .child("Val1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String msg = null;
                            int code = snapshot.getValue(int.class);
                            switch (code) {
                                case 0: {
                                    msg = "I need Immediate help";
                                    break;
                                }
                                case 1: {
                                    msg = "I need Water";
                                    break;
                                }
                                case 2: {
                                    msg = "I need Medicine";
                                    break;

                                }
                                case 3: {
                                    msg = "I need Food";
                                    break;

                                }
                                case 4: {
                                    msg = "I need Assistance";
                                    break;

                                }
                                case 5: {
                                    msg = "I need to use the Bathroom";
                                    break;

                                }
                                case 21: {
                                    msg = "I need to use the Washroom";
                                    break;

                                }
                                case 22: {
                                    msg = "I require Food and Water";
                                    break;

                                }
                                case 23: {
                                    msg = "I need Immediate Help";
                                    break;

                                }
                                case 30: {
                                    msg = "I need Immediate Assistance";
                                    break;

                                }
                            }

                            if (msg != null) {
                                notificationAlert(getBaseContext(), "Alert", msg);
                                DbHelper db = new DbHelper(SensorService.this);
                                List<ContactModel> list = db.getAllContacts();
                                for (ContactModel c : list) {
                                    sendSMS(msg, c.getPhoneNo());
                                }
                                FirebaseDatabase.getInstance().getReference("fir")
                                        .child("Val1")
                                        .setValue(-1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onShake(int count) {
                //check if the user has shaked the phone for 3 time in a row
                if (count == 3) {
                    //vibrate the phone
                    vibrate();
                    DbHelper db = new DbHelper(SensorService.this);
                    List<ContactModel> list = db.getAllContacts();
                    //send SMS to each contact
                    notificationAlert(getBaseContext(), "Alert", "Urgent Help Required");
                    for (ContactModel c : list) {
                        sendSMS("Urgent Help Required", c.getPhoneNo());

                    }
                }

            }
        });
        //register the listener
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void sendSMS(String msg, String number) {
        number = number.replace("+920", "0");
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(msg);
        smsManager.sendMultipartTextMessage(number, null, parts, null, null);
    }

    //method to vibrate the phone
    public void vibrate() {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibEff;
        //Android Q and above have some predefined vibrating patterns
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibEff = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);
            vibrator.cancel();
            vibrator.vibrate(vibEff);
        } else {
            vibrator.vibrate(500);
        }


    }

    private void notificationAlert(Context context, String title, String text) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Object var10000 = context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (var10000 == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.app.NotificationManager");
        } else {
            NotificationManager mNotificationManager = (NotificationManager) var10000;
            NotificationChannel mChannel = null;
            if (Build.VERSION.SDK_INT >= 26) {
                mChannel = new NotificationChannel("123", (CharSequence) "NAMAZ ALERT", NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription("Utils.CHANNEL_SIREN_DESCRIPTION");
                AudioAttributes audioAttributes = (new AudioAttributes.Builder())
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                mChannel.setSound(sound, audioAttributes);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            Intent notificationIntent = new Intent(context, SplashActivity.class);
            PendingIntent contentIntent = Build.VERSION.SDK_INT >= 31 ? PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE) : PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            androidx.core.app.NotificationCompat.Builder var13 = (new NotificationCompat.Builder(context, "123"))
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(contentIntent)
                    .setContentTitle((CharSequence) title).setContentText((CharSequence) text)
                    .setAutoCancel(true)
                    .setLights(-16776961, 300, 1000)
                    .setWhen(System.currentTimeMillis()).setPriority(0);
            Intrinsics.checkNotNullExpressionValue(var13, "NotificationCompat.Build…nCompat.PRIORITY_DEFAULT)");
            NotificationCompat.Builder mBuilder = var13;
            if (Build.VERSION.SDK_INT < 26) {
                mBuilder.setSound(sound);
            }

            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    //For Build versions higher than Android Oreo, we launch
    // a foreground service in a different way. This is due to the newly
    // implemented strict notification rules, which require us to identify
    // our own notification channel in order to view them correctly.
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("You are protected.")
                .setContentText("We are there for you")

                //this is important, otherwise the notification will show the way
                //you want i.e. it will show some default notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public void onDestroy() {

        //create an Intent to call the Broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReactivateService.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}