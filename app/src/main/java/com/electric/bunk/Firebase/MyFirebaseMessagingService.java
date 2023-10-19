//Shubhams Code - T00619152

package com.electric.bunk.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.electric.bunk.MainActivity;
import com.electric.bunk.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final static String TAG = "myTag";
    NotificationChannel mChannel;
    Notification notification;
    Uri defaultSound;
    private static final int REQUEST_CODE = 1;
    private Intent[] intent;
    private static final int NOTIFICATION_ID = 6578;
    private String newType;
    String GROUP_KEY_WORK_EMAIL = "com.enzo.careenzo";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


//        add this type , if you need prescribe medicine notification in both sections
//         || remoteMessage.getData().get("type").equalsIgnoreCase("requestMedicine")


//        LoginService.start(this,SharedPrefsHelper.getInstance().getQbUser());


        if (remoteMessage.getData().get("type").equalsIgnoreCase("abc") || remoteMessage.getData().get("type").equalsIgnoreCase("occassion") || remoteMessage.getData().get("type").equalsIgnoreCase("rejectCall") || remoteMessage.getData().get("type").equalsIgnoreCase("incomingCall") || remoteMessage.getData().get("type").equalsIgnoreCase("accept") || remoteMessage.getData().get("type").equalsIgnoreCase("reject") || remoteMessage.getData().get("type").equalsIgnoreCase("done") ||
                remoteMessage.getData().get("type").equalsIgnoreCase("newbooking") || remoteMessage.getData().get("type").equalsIgnoreCase("reschedule")
                || remoteMessage.getData().get("type").equalsIgnoreCase("daignosisReport") || remoteMessage.getData().get("type").equalsIgnoreCase("insurance")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setBookingOreoNotification(remoteMessage.getData().get("type"), remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), remoteMessage.getData().get("subtitle"), remoteMessage.getData().get("sessionId"), remoteMessage.getData().get("bookingId"), remoteMessage.getData().get("patientId"), remoteMessage.getData().get("doctorId"));
            } else {
                showNotification(remoteMessage.getData().get("type"), remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), remoteMessage.getData().get("subtitle"), remoteMessage.getData().get("sessionId"), remoteMessage.getData().get("bookingId"), remoteMessage.getData().get("patientId"), remoteMessage.getData().get("doctorId"));
            }
        } else {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra(AppConstants.OPEN_SCREEN, AppConstants.OPEN_APPOINTMENT_SCREEN);

                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                // Build notification
                // Actions are just fake
                Notification noti = new Notification.Builder(this)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("message"))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pIntent)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(0, noti);

            }
        }

        // Check if message contains a data payload.


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Prepare intent which is triggered if the
            // notification is selected

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void setBookingOreoNotification(String type, String title, String message, String s, String orderId, String bookingId, String patientId, String doctorId) {
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (type.equalsIgnoreCase("abc")) {
            intent = new Intent[]{new Intent(this, MainActivity.class).putExtra("subtitle", s).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)};
        }


        //        startActivities(intent);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, REQUEST_CODE,
                intent, PendingIntent.FLAG_ONE_SHOT);


// Sets an ID for the notification, so it can be updated.
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Enzocare";// The user-visible name of the channel.


        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
// Create a notification and set the notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setSound(defaultSound)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .setStyle(new Notification.BigTextStyle())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .build();


        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

// Issue the notification.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showNotification(String type, String title, String message, String s, String orderId, String bookingId, String patientId, String doctorId) {
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (type.equalsIgnoreCase("abc")) {
            intent = new Intent[]{new Intent(this, MainActivity.class).putExtra("subtitle", s).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)};

        }

        //        startActivities(intent);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);


        notification = new NotificationCompat.Builder(this)
                .setContentText(message)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setSound(defaultSound)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

    }


}