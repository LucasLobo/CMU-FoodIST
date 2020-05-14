package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.FoodServiceActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.FetchImagesRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.FetchMenusRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.JoinQueueRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.LeaveQueueRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.receivers.SimWifiP2pBroadcastReceiver;

import static org.threeten.bp.temporal.ChronoUnit.SECONDS;

public class Data extends Application implements SimWifiP2pManager.PeerListListener {

    private static final String TAG = "Data";

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private SimWifiP2pBroadcastReceiver mReceiver;

    HashMap<Integer, FoodService> foodServiceHashMap;
    HashMap<String, FoodService> foodServiceBeaconNames;

    LruCache<Integer, Bitmap> cachedImages = new LruCache<Integer, Bitmap>(100 * 1024 * 1024) {
        protected int sizeOf(Integer key, Bitmap value) {
            return value.getByteCount();
        }
    };

    User user;

    public static final String CHANNEL_ID = "channel";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        createNotificationChannel();
    }

    public ArrayList<FoodService> getFoodServiceList() {
        return new ArrayList<>(foodServiceHashMap.values());
    }

    public FoodService getFoodService(Integer id) {
        return foodServiceHashMap.get(id);
    }

    public User getUser() {
        return user;
    }

    public void joinQueue(final Integer fsId){
        new GrpcTask(new JoinQueueRunnable(fsId) {
            @Override
            protected void callback(Integer userQueueId) {
                Log.i(TAG, "callback: " + userQueueId);
                getUser().addActiveQueue(fsId, userQueueId, LocalTime.now());
            }
        }).execute();
    }

    public void leaveQueue(Integer userQueueId, final Integer fsId){
        LocalTime queueJoinTime = user.getQueueJoinTime(fsId);
        int durationSeconds = (int) SECONDS.between(queueJoinTime, LocalTime.now());
        new GrpcTask(new LeaveQueueRunnable(userQueueId, fsId, durationSeconds) {
            @Override
            protected void callback(String result) {
                Log.i(TAG, "callback: " + result);
                getUser().removeActiveQueue(fsId);
            }
        }).execute();
    }


    public Bitmap getImage(Integer imageId) {
        return cachedImages.get(imageId);
    }

    public void addImage(Integer imageId, Bitmap bitmap) {
        cachedImages.put(imageId, bitmap);
    }

    private void initData() {
        initUser();
        initFoodServices();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initUser() {
        this.user = new User();
    }

    private void initFoodServices() {
        this.foodServiceHashMap = new HashMap<>();
        this.foodServiceBeaconNames = new HashMap<>();

        int id = 1;

        FoodService alwaysOpen = new FoodService(id++, "Always Open", 38.7352722896, -9.13268566132);
        alwaysOpen.addSchedule(0,0,23,59);
        foodServiceHashMap.put(alwaysOpen.getId(), alwaysOpen);
        foodServiceBeaconNames.put("AlwaysOpen", alwaysOpen);

        FoodService centralBar = new FoodService(id++, "Central Bar", 38.736606, -9.139532);
        centralBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(centralBar.getId(), centralBar);
        foodServiceBeaconNames.put("CentralBar", centralBar);

        FoodService civilBar = new FoodService(id++, "Civil Bar", 38.736988, -9.139955);
        civilBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(civilBar.getId(), civilBar);
        foodServiceBeaconNames.put("CivilBar", civilBar);

        FoodService civilCafeteria = new FoodService(id++, "Civil Cafeteria", 38.737650, -9.140384);
        civilCafeteria.addSchedule(12,0,15,0);
        foodServiceHashMap.put(civilCafeteria.getId(), civilCafeteria);
        foodServiceBeaconNames.put("CivilBar", civilCafeteria);

        FoodService senaPastryShop = new FoodService(id++, "Sena Pastry Shop", 38.737677, -9.138672);
        senaPastryShop.addSchedule(8,0,19,0);
        foodServiceHashMap.put(senaPastryShop.getId(), senaPastryShop);
        foodServiceBeaconNames.put("Sena", senaPastryShop);

        FoodService mechyBar = new FoodService(id++, "Mechy Bar", 38.737247, -9.137434);
        mechyBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(mechyBar.getId(), mechyBar);
        foodServiceBeaconNames.put("MechyBar", mechyBar);

        FoodService aeistBar = new FoodService(id++, "AEIST Bar", 38.736542, -9.137226);
        aeistBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(aeistBar.getId(), aeistBar);
        foodServiceBeaconNames.put("AEISTBar", aeistBar);

        FoodService aeistEsplanade = new FoodService(id++, "AEIST Esplanade", 38.736318, -9.137820);
        aeistEsplanade.addSchedule(9,0,17,0);
        foodServiceHashMap.put(aeistEsplanade.getId(), aeistEsplanade);
        foodServiceBeaconNames.put("AEISTEsplanade", aeistEsplanade);

        FoodService chemyBar = new FoodService(id++, "Chemy Bar", 38.736240, -9.138302);
        chemyBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(chemyBar.getId(), chemyBar);
        foodServiceBeaconNames.put("ChemyBar", chemyBar);

        FoodService sasCafeteria = new FoodService(id++, "SAS Cafeteria", 38.736571, -9.137036);
        sasCafeteria.addSchedule(9,0,21,0);
        foodServiceHashMap.put(sasCafeteria.getId(), sasCafeteria);
        foodServiceBeaconNames.put("SASCafeteria", sasCafeteria);

        FoodService mathCafeteria = new FoodService(id++, "Math Cafeteria", 38.735508, -9.139645);
        mathCafeteria.addSchedule(User.UserStatus.STUDENT,13,30,15,0);
        mathCafeteria.addSchedule(User.UserStatus.GENERAL_PUBLIC, 13,30,15,0);
        mathCafeteria.addSchedule(User.UserStatus.PROFESSOR,12,0,15,0);
        mathCafeteria.addSchedule(User.UserStatus.RESEARCHER,12,0,15,0);
        mathCafeteria.addSchedule(User.UserStatus.STAFF,12,0,15,0);
        foodServiceHashMap.put(mathCafeteria.getId(), mathCafeteria);
        foodServiceBeaconNames.put("MathCafeteria", mathCafeteria);

        FoodService complexBar = new FoodService(id++, "Complex Bar", 38.736050, -9.140156);
        complexBar.addSchedule(User.UserStatus.STUDENT,9,0,12,0);
        complexBar.addSchedule(User.UserStatus.STUDENT,14,0,17,0);
        complexBar.addSchedule(User.UserStatus.GENERAL_PUBLIC,9,0,12,0);
        complexBar.addSchedule(User.UserStatus.GENERAL_PUBLIC,14,0,17,0);
        complexBar.addSchedule(User.UserStatus.PROFESSOR,9,0,17,0);
        complexBar.addSchedule(User.UserStatus.RESEARCHER,9,0,17,0);
        complexBar.addSchedule(User.UserStatus.STAFF,9,0,17,0);
        foodServiceHashMap.put(complexBar.getId(), complexBar);
        foodServiceBeaconNames.put("ComplexBar", complexBar);

        FoodService tagusCafeteria = new FoodService(id++, "Tagus Cafeteria", 38.737802, -9.303223);
        tagusCafeteria.addSchedule(12,0,15,0);
        foodServiceHashMap.put(tagusCafeteria.getId(), tagusCafeteria);
        foodServiceBeaconNames.put("TagusCafeteria", tagusCafeteria);

        FoodService redBar = new FoodService(id++, "Red Bar", 38.736546, -9.302207);
        redBar.addSchedule(8,0,22,0);
        foodServiceHashMap.put(redBar.getId(), redBar);
        foodServiceBeaconNames.put("RedBar", redBar);

        FoodService greenBar = new FoodService(id++, "Green Bar", 38.738004, -9.303058);
        greenBar.addSchedule(7,0,19,0);
        foodServiceHashMap.put(greenBar.getId(), greenBar);
        foodServiceBeaconNames.put("GreenBar", greenBar);

        FoodService ctnCafeteria = new FoodService(id++, "CTN Cafeteria", 38.812522, -9.093773);
        ctnCafeteria.addSchedule(12,0,14,0);
        foodServiceHashMap.put(ctnCafeteria.getId(), ctnCafeteria);
        foodServiceBeaconNames.put("CTNCafeteria", ctnCafeteria);

        FoodService ctnBar = new FoodService(id++, "CTN Bar", 38.812522, -9.093773);
        ctnBar.addSchedule(8,30,12,0);
        ctnBar.addSchedule(15,30,16,30);
        foodServiceHashMap.put(ctnBar.getId(), ctnBar);
        foodServiceBeaconNames.put("CTNBar", ctnBar);

        initMenus();
    }

    private void initMenus() {
        for (final Map.Entry<Integer, FoodService> entry : foodServiceHashMap.entrySet()) {
            new GrpcTask(new FetchMenusRunnable(entry.getKey()) {
                @Override
                protected void callback(List<MenuItem> result) {
                    if (result == null) return;
                    for (MenuItem menuItem : result) {
                        entry.getValue().addMenuItem(menuItem);
                        fetchMenuImages(menuItem);
                    }
                }
            }).execute();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(Data.this, getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
        }
    };

    public void peersChanged(){
        mManager.requestPeers(mChannel, Data.this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers){
        ArrayList<Integer> activeDevicesIds = new ArrayList<>();

        HashMap<Integer,Integer> activeQueues = getUser().getActiveQueuesId();

        String title = "";
        String msg = "";

        int id = 1;

        boolean setNotif = false;
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            System.out.println("FoodService ID: " + device.deviceName);

            FoodService foodService = foodServiceBeaconNames.get(device.deviceName);
            if (foodService == null) continue;
            id = foodService.getId();

            activeDevicesIds.add(id);
            if (getUser().getQueueId(id) == null) {
                joinQueue(id);

                title = "You just joined " + foodService.getName() + "'s queue!";
                msg = "Why don't you start by adding new items to the menu?";
                setNotif = true;
            }
        }

        for (Map.Entry<Integer,Integer> entry: activeQueues.entrySet()) {
            Integer fsId = entry.getKey();
            if (!activeDevicesIds.contains(fsId)) {
                leaveQueue(getUser().getQueueId(fsId), fsId);

                id = fsId;
                title = "You just left " + getFoodService(id).getName() + "'s queue!";
                msg = "Would you like to share some photos of your experience?";
                setNotif = true;
            }
        }

        if(setNotif) {
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, FoodServiceActivity.class);
            resultIntent.putExtra("foodServiceId", id);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_directions_walk)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        }

    }


    public void fetchMenuImages(final MenuItem menuItem) {
        Set<Integer> imageIdsCopy = new HashSet<>(menuItem.getImageIds());
        imageIdsCopy.removeAll(this.cachedImages.snapshot().keySet());
        if (imageIdsCopy.size() == 0) return;
        new GrpcTask(new FetchImagesRunnable(imageIdsCopy) {
            @Override
            protected void callback(HashMap<Integer, Bitmap> result) {
                if (result != null) {
                    for (Map.Entry<Integer, Bitmap> entry : result.entrySet()) {
                        cachedImages.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }).execute();
    }
}
