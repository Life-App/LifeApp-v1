
package com.mainpanel.view;

import com.mainpanel.LifeApp_Map;
import com.szugyi.circlemenu.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * PingService creates a notification that includes 2 buttons: one to snooze the
 * notification, and one to dismiss it.
 */
public class PingService extends IntentService {

    private NotificationManager mNotificationManager;
    private String mMessage;
    private int mMillis;
    NotificationCompat.Builder builder;
    
    
    public PingService() {
    	
        // The super call is required. The background thread that IntentService
        // starts is labeled with the string argument you pass.
        super("com.mainpanel");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	Log.d("ping","In onHandleIntent");
        // The reminder message the user set.
        mMessage = "Dinner Time !! \nRecommendation : Panera bread\nPlease click here for more suggestions.";
        // The timer duration the user set. The default is 10 seconds.
        mMillis = 10 * 1000;
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        String action = intent.getAction();
        // This section handles the 3 possible actions:
        // ping, snooze, and dismiss.
        if(action.equals(CommonConstants.ACTION_PING)) {
            issueNotification(intent, mMessage);
        } else if (action.equals(CommonConstants.ACTION_SNOOZE)) {
            nm.cancel(CommonConstants.NOTIFICATION_ID);
            Log.d(CommonConstants.DEBUG_TAG, getString(R.string.snoozing));
            // Sets a snooze-specific "done snoozing" message.
            issueNotification(intent, getString(R.string.done_snoozing));
           

        } else if (action.equals(CommonConstants.ACTION_DISMISS)) {
            nm.cancel(CommonConstants.NOTIFICATION_ID);
        }
    }

    private void issueNotification(Intent intent, String msg) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.
        Intent dismissIntent = new Intent(this, PingService.class);
        dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, PingService.class);
        snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        // Constructs the Builder object.
        builder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.ping))
                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                /*
                 * Sets the big view "big text" style and supplies the
                 * text (the user's reminder message) that will be displayed
                 * in the detail area of the expanded notification.
                 * These calls are ignored by the support library for
                 * pre-4.1 devices.
                 */
                .setStyle(new NotificationCompat.BigTextStyle()
                     .bigText(msg))
                .addAction (R.drawable.ic_stat_dismiss,
                        getString(R.string.dismiss), piDismiss)
                .addAction (R.drawable.ic_stat_snooze,
                        getString(R.string.snooze), piSnooze);

        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
         Intent resultIntent = new Intent(this, LifeApp_Map.class);
         intent.putExtra("pingservice", "true");
         resultIntent.putExtra(CommonConstants.EXTRA_MESSAGE, msg);
         resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

         // Because clicking the notification opens a new ("special") activity, there's
         // no need to create an artificial back stack.
         PendingIntent resultPendingIntent =
                 PendingIntent.getActivity(
                 this,
                 0,
                 resultIntent,
                 PendingIntent.FLAG_UPDATE_CURRENT
         );

         builder.setContentIntent(resultPendingIntent);
         startTimer(mMillis);
    }

    private void issueNotification(NotificationCompat.Builder builder) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(CommonConstants.NOTIFICATION_ID, builder.build());
    }

 // Starts the timer according to the number of seconds the user specified.
    private void startTimer(int millis) {
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_start));
        try {
            Thread.sleep(millis);

        } catch (InterruptedException e) {
            Log.d(CommonConstants.DEBUG_TAG, getString(R.string.sleep_error));
        }
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_finished));
        issueNotification(builder);
    }
}
