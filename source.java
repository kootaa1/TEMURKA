import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;
import java.security.InvalidParameterException;

public class ScheduleService extends IntentService {
    private final String TAG = "ScheduleService";
    public static final String ACTION_SCHEDULE_CHECK = "ACTION_SCHEDULE_CHECK";
    public static final String ACTION_SCHEDULE_SET = "ACTION_SCHEDULE_SET";
    public static final String ACTION_START_WORK = "ACTION_START_WORK";
    public static final String ACTION_END_WORK  = "ACTION_END_WORK";
    public static final String ACTION_START_RECORD = "ACTION_START_RECORD";
    public static final String ACTION_STOP_RECORD = "ACTION_STOP_RECORD";
    ScheduleRepository repository;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public ScheduleService() {
        super("ScheduleService");
        repository = new ScheduleRepository();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service is created");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onHandleIntent action: " + action.toString());
        if (action == ACTION_SCHEDULE_CHECK) {
//            repository.CreateSelectItemRequest();
            //если время работы поменялось, то устанавливаем пробуждение на новое время работы
            //иначе ничего не делаем
        } else if (action == ACTION_SCHEDULE_SET) {

        } else if (action == ACTION_START_WORK) {
            NotificationFactory.getInstance().createImportantNotification("Ваш рабочий день начался",
                    "",
                    true,
                    new NotificationButtonDescription(R.drawable.ic_location_on,
                            "Начать запись пути",
                            null));

        } else if (action == ACTION_STOP_RECORD) {
            MyApplication.WebAppInterface.stopGeoTracking();

        } else throw new InvalidParameterException();
    }
}


=====================================================================================================================================


<service
    android:name=".services.ScheduleService"
    android:enabled="true"
    android:exported="true"
    android:stopWithTask="false">
    <intent-filter>
        <action android:name="ACTION_STOP_RECORD" />
    </intent-filter>
</service>

=====================================================================================================================================

public Notification createImportantNotification(String title, String text, Boolean dismissible, @Nullable NotificationButtonDescription nbd) {
    Intent notificationIntent = new Intent(context.getApplicationContext(), MainWebViewActivity.class);
    notificationIntent.setAction(Intent.ACTION_MAIN);
    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    return createImportantNotification(notificationIntent, title, text, dismissible, nbd);
}

public Notification createImportantNotification(Intent intent, String title, String text, Boolean dismissible, @Nullable NotificationButtonDescription nbd) {
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, IMPORTANT_CHANNEL_ID);
    builder.setContentIntent(contentIntent)
            .setOngoing(!dismissible)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.notification_app_icon)
            .setTicker("GeoWFM")
            .setContentText(text) // Текст уведомления
            .setWhen(System.currentTimeMillis())
            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
            .setContentTitle(title);
    if (Build.VERSION.SDK_INT < 26) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sp.getBoolean(NOTIFICATION_SOUND_OFF, false)) {
            builder.setSound(Uri.parse(sp.getString(NOTIFICATION_RINGTONE,
                    RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION).toString())));
        } else {
            builder.setDefaults(0);
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
    }
    if (nbd != null) {
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(nbd.buttonIconResourse,
                nbd.buttonText,
                nbd.buttonAction)
                .build();
        builder.addAction(action);
    }
    return builder.build();
}

=====================================================================================================================================

NotificationFactory.getInstance().createImportantNotification(null, "Запись маршрута включена", false,
                new NotificationButtonDescription(R.drawable.ic_location_off,
                "Выключить запись маршрута",
                        pIntent));
