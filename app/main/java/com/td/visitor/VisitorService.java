package com.td.visitor;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.core.view.filter.IndexedFilter;
import com.firebase.client.realtime.Connection;
import com.firebase.client.utilities.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;

public class VisitorService extends Service {

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    boolean flag_service = false;
    String count_str = "";
    String immediate_count_str = "";
    String ordinary_count_str = "";
    String urgent_count_str = "";
    String user_str;

    public VisitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        File dir = new File(Environment.getExternalStorageDirectory(), "/Visitor/Notification/");
        File dir_user = new File(Environment.getExternalStorageDirectory(), "Visitor/User/");

      //  Toast.makeText(getApplicationContext(), "Visitor service 1"+user_str, Toast.LENGTH_SHORT).show();


        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!dir_user.exists()) {
            dir_user.mkdirs();
        } else {
            File file_path_count = new File(dir, "count.txt");
            File file_path_immediate_count = new File(dir, "ImmediateCount.txt");
            File file_path_ordinary_count = new File(dir, "OrdinaryCount.txt");
            File file_path_urgent_count = new File(dir, "UrgentCount.txt");
            File file_user = new File(dir_user, "user.txt");



            if (file_path_count.exists()) {

              //  Toast.makeText(getApplicationContext(), "Visitor service 3"+user_str, Toast.LENGTH_SHORT).show();

                if (file_path_immediate_count.exists()) {

                //    Toast.makeText(getApplicationContext(), "Visitor service 4"+user_str, Toast.LENGTH_SHORT).show();

                    if (file_path_ordinary_count.exists()) {
                        if (file_path_urgent_count.exists()) {
                            if (file_user.exists()) {

                             //   Toast.makeText(getApplicationContext(), "Visitor service 5"+user_str, Toast.LENGTH_SHORT).show();

                                flag_service = true;

                                try {

                                    FileInputStream fileInputStream_count = new FileInputStream(file_path_count);
                                    DataInputStream dataInputStream_count = new DataInputStream(fileInputStream_count);
                                    BufferedReader bufferedReader_count = new BufferedReader(new InputStreamReader(dataInputStream_count));

                                    FileInputStream fileInputStream_immediate = new FileInputStream(file_path_immediate_count);
                                    DataInputStream dataInputStream_immediate = new DataInputStream(fileInputStream_immediate);
                                    BufferedReader bufferedReader_immediate = new BufferedReader(new InputStreamReader(dataInputStream_immediate));

                                    FileInputStream fileInputStream_ordinary = new FileInputStream(file_path_ordinary_count);
                                    DataInputStream dataInputStream_ordinary = new DataInputStream(fileInputStream_ordinary);
                                    BufferedReader bufferedReader_oridnary = new BufferedReader(new InputStreamReader(dataInputStream_ordinary));

                                    FileInputStream fileInputStream_urgent = new FileInputStream(file_path_urgent_count);
                                    DataInputStream dataInputStream_urgent = new DataInputStream(fileInputStream_urgent);
                                    BufferedReader bufferedReader_urgent = new BufferedReader(new InputStreamReader(dataInputStream_urgent));

                                    FileInputStream fileInputStream_user = new FileInputStream(file_user);
                                    DataInputStream dataInputStream_user = new DataInputStream(fileInputStream_user);
                                    BufferedReader bufferedReader_user = new BufferedReader(new InputStreamReader(dataInputStream_user));


                                    count_str = "" + bufferedReader_count.readLine();
                                    immediate_count_str = "" + bufferedReader_immediate.readLine();
                                    ordinary_count_str = "" + bufferedReader_oridnary.readLine();
                                    urgent_count_str = "" + bufferedReader_urgent.readLine();
                                    user_str = "" + bufferedReader_user.readLine();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

            if (flag_service && !user_str.equals("empty")) {


              //  Toast.makeText(getApplicationContext(), "Visitor service"+user_str, Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(), "Visitor Started", Toast.LENGTH_SHORT).show();

                Calendar calendar = Calendar.getInstance();
                int myear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mday = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = mMonth + 1;

                final String today_date = "" + mday + " - " + mMonth + " - " + myear;

                final Firebase mRef_count = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/" + today_date + "/");
                final Firebase immediate_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user_str + "/" + "Immediate/" + today_date + "/");
                final Firebase urgent_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user_str + "/" + "Urgent/" + today_date + "/");
                final Firebase ordinary_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user_str + "/" + "Ordinary/" + today_date + "/");

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

                final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();

                        if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                            //check the internet connection
                            if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                                Toast.makeText(getApplicationContext(), "Visitor Offline", Toast.LENGTH_SHORT).show();
                                if (context != null) {
                                    boolean show = false;
                                    if (ConnectionHelper.lastNoConnectionTs == -1) {
                                        //first time
                                        show = true;
                                        ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                    } else {
                                        if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                            show = true;
                                            ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                        }
                                    }
                                    if (show && ConnectionHelper.isOnline) {
                                        ConnectionHelper.isOnline = false;
                                    }

                                }
                            }else {

                                    mRef_count.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            ArrayList arraylist_count = new ArrayList();
                                            arraylist_count.add(dataSnapshot.getKey());

                                            if (user_str.equals("Reception")) {

                                                if (Integer.parseInt(count_str) < arraylist_count.size())
                                                    showNotification("Visitor Notification",""+arraylist_count.size()+" new messages",001);
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                    immediate_list.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            ArrayList arraylist_count = new ArrayList();
                                            arraylist_count.add(dataSnapshot.getKey());

                                            if (!user_str.equals("Reception")) {
                                                if (Integer.parseInt(immediate_count_str) < arraylist_count.size()) {
                                                    showNotification("Reception","New immediate appointment",002);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                    ordinary_list.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            ArrayList arraylist_count = new ArrayList();
                                            arraylist_count.add(dataSnapshot.getKey());

                                            if (!user_str.equals("Reception")) {
                                                if (Integer.parseInt(ordinary_count_str) < arraylist_count.size()) {
                                                    showNotification("Reception","New ordinary appointment",003);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                    urgent_list.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            ArrayList arraylist_count = new ArrayList();
                                            arraylist_count.add(dataSnapshot.getKey());

                                            if (!user_str.equals("Reception")) {
                                                if (Integer.parseInt(urgent_count_str) < arraylist_count.size()) {
                                                    showNotification("Reception","New urgent appointment",004);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                    ConnectionHelper.isOnline = true;
                                }
                            }
                        }
                };

                registerReceiver(broadcastReceiver, intentFilter);
            }

            return START_STICKY;
    }

    void showNotification(String from,String msg,int a)
    {
//        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        if(!msg.equals("null:)")) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_bowling)
                    .setContentTitle(from)
                    .setContentText(msg);
               //  .setSound(R.raw.sound);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(a, mBuilder.build());

            //vibrator
            Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(50);

            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.sound);
            mediaPlayer.start();
            vib.vibrate(50);
            mediaPlayer.start();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
