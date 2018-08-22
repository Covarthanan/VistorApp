package com.td.visitor;

 import android.app.DatePickerDialog;
 import android.app.DialogFragment;
 import android.app.NotificationManager;
 import android.app.ProgressDialog;
 import android.app.TimePickerDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.pm.PackageManager;
 import android.graphics.Color;
 import android.graphics.drawable.ColorDrawable;
 import android.media.Image;
 import android.os.Build;
 import android.os.CountDownTimer;
 import android.os.Environment;
 import android.os.SystemClock;
 import android.os.Vibrator;
 import android.support.v4.app.ActivityCompat;
 import android.support.v4.app.NotificationCompat;
 import android.support.v4.content.ContextCompat;
 import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.Button;
 import android.widget.DatePicker;
 import android.widget.EditText;
import android.widget.ImageView;
 import android.widget.ListView;
 import android.widget.RelativeLayout;
 import android.widget.Spinner;
import android.widget.TextView;
 import android.widget.TimePicker;
 import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.security.KeyStore;
 import java.text.DecimalFormat;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    Firebase mRef_meeting;
    Firebase mRef_priority;
    Firebase mRef_date;
    Firebase mRef_time;


    ImageView tab_home,tab_message,tab_notification,tab_message_list;
    ViewFlipper layout_changer;

    Button btn_push;
    EditText msg_name,msg_info;
    TextView msg_date,msg_time;
    Spinner msg_chairman,msg_type,msg_priority;

    ProgressDialog progressDialog;

    ListView listview_messsage_list,listview_message_time;
    ArrayList arrayList_msg_list = new ArrayList();
    ArrayList arrayList_msg_time = new ArrayList();
    String date_str_firebase,time_str_firebse;

    //reception view visitor

    TextView msg_list_visitor_name,msg_list_visitor_meeting,msg_list_visitor_type,
            msg_list_visitor_info, msg_list_visitor_date,
            msg_list_visitor_time, msg_list_visitor_priority;
    ImageView msg_edit;
    TextView text_view_ring_bell;
    String msg_edit_name,msg_edit_meeting,msg_edit_type,msg_edit_info,msg_edit_date,msg_edit_time,msg_edit_priority;
    String firebase_key_date;
    String firebase_key_time;
    boolean dialog_box_flag = false;

    Calendar calendar = Calendar.getInstance();
    private  int myear,mMonth,mday;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    Button immediate_btn,urgent_btn,ordinary_btn;
    ArrayAdapter adapter_msg_time;

    EditText chairman_msg_edit;
    Button chairman_msg_btn;

    Button dialog_accept_btn,dialog_wait_btn,dialog_reject_btn;
    RelativeLayout chairman_dialog_box;

    String user;
    ArrayList ystday_time_data;
    boolean detele_notify = true;

    ArrayList notification_data = new ArrayList();
    ListView notify_reception;

    ImageView logout;
    int logout_count = 0;

    boolean notification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Notification
        startService(new Intent(getBaseContext(),VisitorService.class));

        //layout
        tab_home = (ImageView) findViewById(R.id.tab_home);
        tab_message = (ImageView) findViewById(R.id.tab_message);
        tab_notification = (ImageView) findViewById(R.id.tab_notification);
        tab_message_list = (ImageView) findViewById(R.id.tab_message_list);
        layout_changer = (ViewFlipper) findViewById(R.id.layout_changer);

        //id for message area
        btn_push = (Button) findViewById(R.id.btn_push);
        msg_chairman = (Spinner) findViewById(R.id.msg_chairman);
        msg_priority = (Spinner) findViewById(R.id.msg_priority);
        msg_type = (Spinner) findViewById(R.id.msg_type);
        msg_name = (EditText) findViewById(R.id.msg_name);
        msg_time = (TextView) findViewById(R.id.msg_time);
        msg_date = (TextView) findViewById(R.id.msg_date);
        msg_info = (EditText) findViewById(R.id.msg_info);

        //message listview reception
        listview_messsage_list = (ListView) findViewById(R.id.listview_message_list);
        listview_message_time = (ListView) findViewById(R.id.listview_message_time);

        //reception view visitor
        msg_list_visitor_name = (TextView) findViewById(R.id.msg_list_visitor_name);
        msg_list_visitor_meeting = (TextView) findViewById(R.id.msg_list_visitor_meeting);
        msg_list_visitor_type = (TextView) findViewById(R.id.msg_list_visitor_type);
        msg_list_visitor_info = (TextView) findViewById(R.id.msg_list_visitor_info);
        msg_list_visitor_date = (TextView) findViewById(R.id.msg_list_visitor_date);
        msg_list_visitor_time = (TextView) findViewById(R.id.msg_list_visitor_time);
        msg_list_visitor_priority = (TextView) findViewById(R.id.msg_list_visitor_priority);
        msg_edit = (ImageView) findViewById(R.id.message_visitor_edit);
        text_view_ring_bell = (TextView)findViewById(R.id.text_view_ring_bell);

        immediate_btn = (Button) findViewById(R.id.immediate_btn);
        urgent_btn = (Button) findViewById(R.id.urgent_btn);
        ordinary_btn = (Button) findViewById(R.id.ordinary_btn);

        chairman_msg_btn = (Button) findViewById(R.id.chairman_msg_btn);
        chairman_msg_edit = (EditText) findViewById(R.id.chairman_msg_edit);

        dialog_accept_btn = (Button) findViewById(R.id.dialog_accept_btn);
        dialog_reject_btn = (Button) findViewById(R.id.dialog_reject_btn);
        dialog_wait_btn = (Button) findViewById(R.id.dialog_wait_btn);
        chairman_dialog_box = (RelativeLayout) findViewById(R.id.chairman_dialog_box);

        notify_reception = (ListView) findViewById(R.id.listView_reception_notification);

        logout = (ImageView)findViewById(R.id.logout_img);


        //marshmallow permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        chairman_dialog_box.setVisibility(View.INVISIBLE);


        Bundle extra = getIntent().getExtras();
        user = extra.getString("user");

        Toast.makeText(getApplicationContext(), "Welcome " + user, Toast.LENGTH_SHORT).show();

        progressDialog = new ProgressDialog(this);


        myear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mMonth + 1;

        final String today_date = "" + mday + " - " + mMonth + " - " + myear;

        user();
        ystday_data_delete();
        ystday_notify_delete();

        if (!user.equals("Reception"))
        {
            msg_edit.setVisibility(View.VISIBLE);
            text_view_ring_bell.setVisibility(View.VISIBLE);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout.setBackgroundColor(Color.parseColor("#FFCBF2CA"));
                tab_message.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message_list.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_notification.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_home.setBackgroundColor(Color.parseColor("#ffffff"));

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                    Toast.makeText(getApplicationContext(),"Click again to Logout..",Toast.LENGTH_SHORT).show();
                    logout_count++;

                if (logout_count == 2) {

                    File dir_user = new File(Environment.getExternalStorageDirectory(), "Visitor/User/");

                    if (!dir_user.exists()) {

                        dir_user.mkdirs();
                    } else {
                        File file = new File(dir_user, "user.txt");

                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write("empty".getBytes());
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write("empty".getBytes());
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Your are Logged Out..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Login_page.class));
                    finish();
                    logout_count = 0;
                }
            }
        });

        tab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message_list.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_notification.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_home.setBackgroundColor(Color.parseColor("#FFCBF2CA"));

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                logout_count = 0;
                layout_changer.setDisplayedChild(0);
            }
        });

        tab_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message.setBackgroundColor(Color.parseColor("#FFCBF2CA"));
                tab_message_list.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_notification.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_home.setBackgroundColor(Color.parseColor("#ffffff"));

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                logout_count = 0;

                if (user.equals("Reception")) {
                    layout_changer.setDisplayedChild(1);
                } else {
                    layout_changer.setDisplayedChild(7);
                }
            }
        });

        tab_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message_list.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_notification.setBackgroundColor(Color.parseColor("#FFCBF2CA"));
                tab_home.setBackgroundColor(Color.parseColor("#ffffff"));

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                notification = true;

                logout_count = 0;

                if (user.equals("Reception")) {

                    final Firebase mRef_count = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/" + today_date + "/");

                    mRef_count.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            ArrayList arrayList_count  = new ArrayList();
                            arrayList_count.add(dataSnapshot.getKey());
                            NotificationCount(arrayList_count.size());
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
                    layout_changer.setDisplayedChild(2);

                } else {
                    layout_changer.setDisplayedChild(6);
                }

            }
        });

        tab_message_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_message_list.setBackgroundColor(Color.parseColor("#FFCBF2CA"));
                tab_notification.setBackgroundColor(Color.parseColor("#ffffff"));
                tab_home.setBackgroundColor(Color.parseColor("#ffffff"));

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                logout_count = 0;

                layout_changer.setDisplayedChild(3);
            }
        });

        msg_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                myear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mday = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, myear, mMonth, mday);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        msg_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        timeSetListener, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                if (minute <= 9 && hourOfDay < 12)
                    msg_time.setText(hourOfDay + " : " + "0" + minute + "  AM");
                else if (minute > 9 && hourOfDay < 12)
                    msg_time.setText(hourOfDay + " : " + minute + "  AM");
                else if (minute <= 9 && hourOfDay >= 12)
                    msg_time.setText(hourOfDay + " : " + "0" + minute + "  PM");
                else
                    msg_time.setText(hourOfDay + " : " + minute + "  PM");
            }
        };

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                monthOfYear = monthOfYear + 1;
                msg_date.setText(dayOfMonth + " - " + monthOfYear + " - " + year);
            }
        };

        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String name_str = msg_name.getText().toString();
                String meet_person_str = msg_chairman.getSelectedItem().toString();
                String type_str = msg_type.getSelectedItem().toString();
                String info_str = msg_info.getText().toString();
                String priority_str = msg_priority.getSelectedItem().toString();
                String time_str = msg_time.getText().toString();
                String date_str = msg_date.getText().toString();

                if (!name_str.equals("")) {
                    if (!info_str.equals("")) {
                        if (!time_str.equals("")) {
                            if (!meet_person_str.equals("Choose the meeting person")) {
                                if (!type_str.equals("Choose the type of message")) {
                                    if (!priority_str.equals("Choose the priority")) {
                                        if (!date_str.equals("")) {
                                            mRef_meeting = new Firebase("https://visitor-65d3b.firebaseio.com/" + meet_person_str);
                                            mRef_priority = mRef_meeting.child(priority_str);
                                            mRef_date = mRef_priority.child(date_str);
                                            mRef_time = mRef_date.child(time_str);
                                            mRef_time.child("name").setValue(name_str);
                                            mRef_time.child("meeting").setValue(meet_person_str);
                                            mRef_time.child("date").setValue(date_str);
                                            mRef_time.child("type").setValue(type_str);
                                            mRef_time.child("info").setValue(info_str);
                                            mRef_time.child("priority").setValue(priority_str);
                                            mRef_time.child("time").setValue(time_str);


                                            Firebase mRef_reception = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str);
                                            Firebase mRef_rec_time = mRef_reception.child(time_str);
                                            mRef_rec_time.child("name").setValue(name_str);
                                            mRef_rec_time.child("meeting").setValue(meet_person_str);
                                            mRef_rec_time.child("type").setValue(type_str);
                                            mRef_rec_time.child("date").setValue(date_str);
                                            mRef_rec_time.child("info").setValue(info_str);
                                            mRef_rec_time.child("priority").setValue(priority_str);
                                            mRef_rec_time.child("time").setValue(time_str);


                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Message has been pushed....", Toast.LENGTH_SHORT).show();

                                            msg_name.setText("");
                                            msg_info.setText("");
                                            msg_date.setText("");
                                            msg_time.setText("");

                                            layout_changer.setDisplayedChild(0);

                                        } else {

                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Choose the priority ....", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {

                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Choose the priority ....", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Choose the type of message....", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Choose the meeting person....", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Enter the Time ....", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Enter the Information....", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter the Name....", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //message listview for reception
        final ArrayAdapter adapter_msg_list = new ArrayAdapter(this, R.layout.custom_list_view, R.id.list_text_view, arrayList_msg_list);
        listview_messsage_list.setAdapter(adapter_msg_list);

        Firebase mRef_recptn = new Firebase("https://visitor-65d3b.firebaseio.com/Reception");

        mRef_recptn.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                arrayList_msg_list.add(dataSnapshot.getKey());
                adapter_msg_list.notifyDataSetChanged();
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

        adapter_msg_time = new ArrayAdapter(getApplicationContext(), R.layout.custom_list_view, R.id.list_text_view, arrayList_msg_time);
        listview_message_time.setAdapter(adapter_msg_time);

        listview_messsage_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                layout_changer.setDisplayedChild(4);

                date_str_firebase = String.valueOf(arrayList_msg_list.get((int) listview_messsage_list.getItemIdAtPosition(position)));

              //  Toast.makeText(getApplicationContext(), date_str_firebase, Toast.LENGTH_SHORT).show();

                firebase_key_date = date_str_firebase;

                Firebase mRef_time = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase);
                arrayList_msg_time.clear();

                mRef_time.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        arrayList_msg_time.add(dataSnapshot.getKey());
                        adapter_msg_time.notifyDataSetChanged();
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
            }
        });


        listview_message_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                if (user.equals("Reception")) {
                    layout_changer.setDisplayedChild(5);
                    time_str_firebse = String.valueOf(arrayList_msg_time.get((int) listview_message_time.getItemIdAtPosition(position)));

                   // Toast.makeText(getApplicationContext(), time_str_firebse, Toast.LENGTH_SHORT).show();

                    firebase_key_time = time_str_firebse;

                    Firebase mRef_visitor_name = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/name");
                    Firebase mRef_visitor_meeting = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/meeting");
                    Firebase mRef_visitor_type = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/type");
                    Firebase mRef_visitor_info = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/info");
                    Firebase mRef_visitor_date = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/date");
                    Firebase mRef_visitor_time = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/time");
                    Firebase mRef_visitor_priority = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + date_str_firebase + "/" + time_str_firebse + "/priority");

                    mRef_visitor_name.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = dataSnapshot.getValue(String.class);

                            if (!name.equals(null)) {
                                msg_edit_name = dataSnapshot.getValue(String.class);
                                msg_list_visitor_name.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_meeting.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String meeting = dataSnapshot.getValue(String.class);

                            if (!meeting.equals(null)) {
                                msg_edit_meeting = dataSnapshot.getValue(String.class);
                                msg_list_visitor_meeting.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_type.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String type = dataSnapshot.getValue(String.class);
                            if (!type.equals(null)) {
                                msg_edit_type = dataSnapshot.getValue(String.class);
                                msg_list_visitor_type.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                    mRef_visitor_info.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String info = dataSnapshot.getValue(String.class);
                            if (info.equals(null)) {
                                msg_edit_info = dataSnapshot.getValue(String.class);
                                msg_list_visitor_info.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_date.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String data = dataSnapshot.getValue(String.class);

                            if (!data.equals(null)) {
                                msg_edit_date = dataSnapshot.getValue(String.class);
                                msg_list_visitor_date.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_time.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String time = dataSnapshot.getValue(String.class);

                            if (!time.equals(null)) {
                                msg_edit_time = dataSnapshot.getValue(String.class);
                                msg_list_visitor_time.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_priority.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String priority = dataSnapshot.getValue(String.class);

                            if (!priority.equals(null)) {
                                msg_edit_priority = dataSnapshot.getValue(String.class);
                                msg_list_visitor_priority.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                } else {
                    layout_changer.setDisplayedChild(5);
                    time_str_firebse = String.valueOf(arrayList_msg_time.get((int) listview_message_time.getItemIdAtPosition(position)));

                  //  Toast.makeText(getApplicationContext(), time_str_firebse, Toast.LENGTH_SHORT).show();

                    firebase_key_time = time_str_firebse;

                    myear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mday = calendar.get(Calendar.DAY_OF_MONTH);
                    mMonth = mMonth + 1;

                    String cur_date_firebase = "" + mday + " - " + mMonth + " - " + myear;
                   // Toast.makeText(getApplicationContext(), "" + cur_date_firebase, Toast.LENGTH_LONG).show();


                    Firebase mRef_visitor_name = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/name");
                    Firebase mRef_visitor_meeting = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/meeting");
                    Firebase mRef_visitor_type = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/type");
                    Firebase mRef_visitor_info = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/info");
                    Firebase mRef_visitor_date = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/date");
                    Firebase mRef_visitor_time = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/time");
                    Firebase mRef_visitor_priority = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + cur_date_firebase + "/" + time_str_firebse + "/priority");

                    mRef_visitor_name.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_name = dataSnapshot.getValue(String.class);
                            msg_list_visitor_name.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_meeting.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_meeting = dataSnapshot.getValue(String.class);
                            msg_list_visitor_meeting.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_type.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_type = dataSnapshot.getValue(String.class);
                            msg_list_visitor_type.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                    mRef_visitor_info.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_info = dataSnapshot.getValue(String.class);
                            msg_list_visitor_info.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_date.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_date = dataSnapshot.getValue(String.class);
                            msg_list_visitor_date.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_time.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_time = dataSnapshot.getValue(String.class);
                            msg_list_visitor_time.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    mRef_visitor_priority.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            msg_edit_priority = dataSnapshot.getValue(String.class);
                            msg_list_visitor_priority.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }

            }
        });

        if (!user.equals("Reception"))
        {
            msg_edit.setVisibility(View.VISIBLE);
            text_view_ring_bell.setVisibility(View.VISIBLE);
        }

        if (user.equals("Reception"))
        {
            msg_edit.setVisibility(View.INVISIBLE);
            text_view_ring_bell.setVisibility(View.INVISIBLE);
        }

        msg_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

            /*    if (user.equals("Reception")) {
                    msg_name.setText(msg_edit_name);

                    if (msg_edit_meeting.equals("Chairman")) {
                        msg_chairman.setSelection(1);
                    } else {
                        msg_chairman.setSelection(2);
                    }
                    msg_info.setText(msg_edit_info);
                    msg_time.setText(msg_edit_time);
                    msg_date.setText(msg_edit_date);

                    if (msg_edit_type.equals("Waiting to See you")) {
                        msg_type.setSelection(1);
                    } else if (msg_edit_type.equals("Like to See you")) {
                        msg_type.setSelection(2);
                    } else if (msg_edit_type.equals("Like to talk to you")) {
                        msg_type.setSelection(3);
                    } else {
                        msg_type.setSelection(4);
                    }

                    if (msg_edit_priority.equals("Ordinary")) {
                        msg_priority.setSelection(1);
                    } else if (msg_edit_priority.equals("Urgent")) {
                        msg_priority.setSelection(2);
                    } else {
                        msg_priority.setSelection(3);
                    }

                    layout_changer.setDisplayedChild(1);

                    Firebase edit_msg = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + firebase_key_date);
                    Firebase edit_message = edit_msg.child(firebase_key_time);

                    edit_message.child("name").removeValue();
                    edit_message.child("meeting").removeValue();
                    edit_message.child("type").removeValue();
                    edit_message.child("info").removeValue();
                    edit_message.child("date").removeValue();
                    edit_message.child("time").removeValue();
                    edit_message.child("priority").removeValue();


                   // Toast.makeText(getApplicationContext(), "" + edit_message, Toast.LENGTH_LONG).show();
                   }
            */
                if (user.equals("Reception"))
                {
                    msg_edit.setVisibility(View.INVISIBLE);
                    text_view_ring_bell.setVisibility(View.INVISIBLE);
                }
                else {
                    if (dialog_box_flag) {
                        chairman_dialog_box.setVisibility(View.INVISIBLE);
                        dialog_box_flag = false;
                    } else {
                        chairman_dialog_box.setVisibility(View.VISIBLE);
                        dialog_box_flag = true;
                    }
                    //dialog_box chairman

                    dialog_accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(50);

                            msg_edit.setVisibility(View.INVISIBLE);
                            text_view_ring_bell.setVisibility(View.INVISIBLE);

                            myear = calendar.get(Calendar.YEAR);
                            mMonth = calendar.get(Calendar.MONTH);
                            mday = calendar.get(Calendar.DAY_OF_MONTH);
                            mMonth = mMonth + 1;

                            String date_firebase = "" + mday + " - " + mMonth + " - " + myear;

                            Firebase firebase_accept = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+today_date+"/");
                            firebase_accept.child("" + System.currentTimeMillis()).setValue("   " + user + " ACCEPTED   \n\n   " + msg_edit_name + "\n   " + msg_edit_time + "\n   " + msg_edit_type + "\n   " + msg_edit_info);

                            Firebase delete = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + msg_edit_priority + "/" + date_firebase + "/" + msg_edit_time + "/");

                            delete.child("name").removeValue();
                            delete.child("meeting").removeValue();
                            delete.child("type").removeValue();
                            delete.child("info").removeValue();
                            delete.child("date").removeValue();
                            delete.child("time").removeValue();
                            delete.child("priority").removeValue();

                          //  Toast.makeText(getApplicationContext(), "" + msg_edit_name + "\n" + msg_edit_time + "\n" + msg_edit_date + "\n" + msg_edit_info, Toast.LENGTH_SHORT).show();
                            chairman_dialog_box.setVisibility(View.INVISIBLE);
                        }
                    });

                    dialog_reject_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(50);

                            msg_edit.setVisibility(View.INVISIBLE);
                            text_view_ring_bell.setVisibility(View.INVISIBLE);

                            Firebase firebase_reject = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+today_date+"/");
                            firebase_reject.child("" + System.currentTimeMillis()).setValue("   " + user + " REJECTED   \n\n   " + msg_edit_name + "\n   " + msg_edit_time + "\n   " + msg_edit_type + "\n   " + msg_edit_info);

                            Firebase delete = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + msg_edit_priority + "/" + msg_edit_date + "/" + msg_edit_time + "/");

                            delete.child("name").removeValue();
                            delete.child("meeting").removeValue();
                            delete.child("type").removeValue();
                            delete.child("info").removeValue();
                            delete.child("date").removeValue();
                            delete.child("time").removeValue();
                            delete.child("priority").removeValue();

                           // Toast.makeText(getApplicationContext(), "reject", Toast.LENGTH_SHORT).show();
                            chairman_dialog_box.setVisibility(View.INVISIBLE);
                        }
                    });

                    dialog_wait_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(50);

                            msg_edit.setVisibility(View.INVISIBLE);
                            text_view_ring_bell.setVisibility(View.INVISIBLE);

                            Firebase firebase_wait = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+today_date+"/");
                            firebase_wait.child("" + System.currentTimeMillis()).setValue("   " + user + " WAIT   \n\n   " + msg_edit_name + "\n   " + msg_edit_time + "\n   " + msg_edit_type + "\n   " + msg_edit_info);

                            Firebase delete = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + msg_edit_priority + "/" + msg_edit_date + "/" + msg_edit_time + "/");

                            delete.child("name").removeValue();
                            delete.child("meeting").removeValue();
                            delete.child("type").removeValue();
                            delete.child("info").removeValue();
                            delete.child("date").removeValue();
                            delete.child("time").removeValue();
                            delete.child("priority").removeValue();

                            //Toast.makeText(getApplicationContext(), "wait", Toast.LENGTH_SHORT).show();
                            chairman_dialog_box.setVisibility(View.INVISIBLE);
                        }
                    });

                }

            }
        });

        adapter_msg_time = new ArrayAdapter(getApplicationContext(), R.layout.custom_list_view, R.id.list_text_view, arrayList_msg_time);
        listview_message_time.setAdapter(adapter_msg_time);

        ordinary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                //notification

                arrayList_msg_time.clear();

                layout_changer.setDisplayedChild(4);

                myear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mday = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = mMonth + 1;

                String date_firebase = "" + mday + " - " + mMonth + " - " + myear;
                //Toast.makeText(getApplicationContext(), "" + date_firebase, Toast.LENGTH_LONG).show();

                //  https://visitor-65d3b.firebaseio.com/Chairman/Ordinary/19 - 4 - 2018/1 : 22  AM
                Firebase ordinary_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + "Ordinary/" + date_firebase + "/");

                ordinary_list.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        arrayList_msg_time.clear();
                        arrayList_msg_time.add(dataSnapshot.getKey());
                        adapter_msg_time.notifyDataSetChanged();
                        OrdinaryCount(arrayList_msg_time.size());
                       // Toast.makeText(getApplicationContext(), "" + arrayList_msg_time, Toast.LENGTH_SHORT).show();

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
            }
        });


        urgent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                arrayList_msg_time.clear();


                layout_changer.setDisplayedChild(4);

                myear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mday = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = mMonth + 1;

                String date_firebase = "" + mday + " - " + mMonth + " - " + myear;
              //  Toast.makeText(getApplicationContext(), "" + user, Toast.LENGTH_LONG).show();

                //  https://visitor-65d3b.firebaseio.com/Chairman/Ordinary/19 - 4 - 2018/1 : 22  AM
                Firebase urgent_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + "Urgent/" + date_firebase + "/");

                urgent_list.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        arrayList_msg_time.clear();
                        arrayList_msg_time.add(dataSnapshot.getKey());
                        adapter_msg_time.notifyDataSetChanged();
                        //notification
                        if (arrayList_msg_time.size() == 0)
                        UrgentCount(arrayList_msg_time.size());
                      //  Toast.makeText(getApplicationContext(), "" + arrayList_msg_time, Toast.LENGTH_SHORT).show();
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
            }
        });


        immediate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                arrayList_msg_time.clear();

                layout_changer.setDisplayedChild(4);

                myear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mday = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = mMonth + 1;

                String date_firebase = "" + mday + " - " + mMonth + " - " + myear;
                //Toast.makeText(getApplicationContext(), "" + user, Toast.LENGTH_LONG).show();

                //  https://visitor-65d3b.firebaseio.com/Chairman/Ordinary/19 - 4 - 2018/1 : 22  AM
                Firebase immediate_list = new Firebase("https://visitor-65d3b.firebaseio.com/" + user + "/" + "Immediate/" + date_firebase + "/");

                immediate_list.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        arrayList_msg_time.clear();
                        arrayList_msg_time.add(dataSnapshot.getKey());
                        adapter_msg_time.notifyDataSetChanged();
                        //notification
                        ImmediateCount(arrayList_msg_time.size());
                        //  Toast.makeText(getApplicationContext(), "" + arrayList_msg_time, Toast.LENGTH_SHORT).show();
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
            }
        });


        chairman_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                String msg_str = chairman_msg_edit.getText().toString();

                if (!msg_str.equals(null)) {
                    Firebase firebase_notify = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+today_date+"/");
                    firebase_notify.child(System.currentTimeMillis() + "").setValue("   " + user + "\n\n" + msg_str);
                }
                layout_changer.setDisplayedChild(0);
            }
        });

        final ArrayAdapter notify_adapter = new ArrayAdapter(getApplicationContext(), R.layout.custom_list_view, R.id.list_text_view, notification_data);
        notify_reception.setAdapter(notify_adapter);

        Firebase notify_ref = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+today_date);
        notify_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                notification_data.add(dataSnapshot.getValue(String.class));
                notify_adapter.notifyDataSetChanged();
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

    }


    public void NotificationCount(int count){

                int count_int = 0;
                String count_str = ""+count;

                File dir = new File(Environment.getExternalStorageDirectory(),"/Visitor/Notification");
                if (!dir.exists())
                {
                    dir.mkdirs();
                }else
                {
                    File file = new File(dir,"count.txt");

                    if (file.exists())
                    {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                        try {
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
    }

    public void ImmediateCount(int Count)
    {
                int count_int = 0;
                String count_str = ""+Count;

                File dir = new File(Environment.getExternalStorageDirectory(),"/Visitor/Notification");
                if (!dir.exists())
                {
                    dir.mkdirs();
                }else
                {
                    File file = new File(dir,"ImmediateCount.txt");

                    if (file.exists())
                    {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                        try {
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
    }

    public void UrgentCount(int Count)
    {
                int count_int = 0;
                String count_str = ""+Count;

                File dir = new File(Environment.getExternalStorageDirectory(),"/Visitor/Notification");
                if (!dir.exists())
                {
                    dir.mkdirs();
                }else
                {
                    File file = new File(dir,"UrgentCount.txt");

                    if (file.exists())
                    {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                        try {
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

    }

    public void OrdinaryCount(int Count)
    {
                int count_int = 0;
                String count_str = ""+Count;

                File dir = new File(Environment.getExternalStorageDirectory(),"/Visitor/Notification");
                if (!dir.exists())
                {
                    dir.mkdirs();
                }else
                {
                    File file = new File(dir,"OrdinaryCount.txt");

                    if (file.exists())
                    {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                        try {
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(count_str.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

    }

    public void user() {

        NotificationCount(0);
        OrdinaryCount(0);
        ImmediateCount(0);
        UrgentCount(0);

        File dir = new File(Environment.getExternalStorageDirectory(), "/Visitor/User");
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            File file = new File(dir, "user.txt");

            if (file.exists()) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(user.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(user.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ystday_data_delete()
    {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        myear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mMonth + 1;
        mday = mday - 1;

        final String ystday_date = "" + mday + " - " + mMonth + " - " + myear;
       // Toast.makeText(getApplicationContext(), "" + ystday_date, Toast.LENGTH_LONG).show();

        ystday_time_data = new ArrayList();

        final Firebase ystday_data_time = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + ystday_date + "/");

        ystday_data_time.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ystday_time_data.add(dataSnapshot.getKey());

              //  Toast.makeText(getApplicationContext(), "" + ystday_time_data, Toast.LENGTH_LONG).show();

                int i = 0;

                if (ystday_time_data.size() > 0) {
                    do {
                       // Toast.makeText(getApplicationContext(), "working   " + ystday_time_data.get(i), Toast.LENGTH_LONG).show();

                        Firebase delete_data = new Firebase("https://visitor-65d3b.firebaseio.com/Reception/" + ystday_date + "/" + ystday_time_data.get(i) + "/");

                        delete_data.child("name").removeValue();
                        delete_data.child("meeting").removeValue();
                        delete_data.child("type").removeValue();
                        delete_data.child("info").removeValue();
                        delete_data.child("date").removeValue();
                        delete_data.child("time").removeValue();
                        delete_data.child("priority").removeValue();
                        i++;
                    } while (i < ystday_time_data.size());
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

    }

    public void ystday_notify_delete()
    {

        myear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mday = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = mMonth + 1;
        mday = mday - 1;

        final String ystday_date = "" + mday + " - " + mMonth + " - " + myear;
       // Toast.makeText(getApplicationContext(), "" + ystday_date, Toast.LENGTH_LONG).show();

            final ArrayList arrayList_delete = new ArrayList();
            final Firebase mRef_notify_delete = new Firebase("https://visitor-65d3b.firebaseio.com/Notification/"+ystday_date);

            mRef_notify_delete.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    arrayList_delete.add(dataSnapshot.getKey());

                   // Toast.makeText(getApplicationContext(),""+arrayList_delete,Toast.LENGTH_LONG).show();
                    int i = 0;

                    if (arrayList_delete.size()>0)
                    {
                        do {
                            mRef_notify_delete.child(""+arrayList_delete.get(i)).removeValue();
                        }while (i < arrayList_delete.size());
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

        progressDialog.dismiss();
    }

        //marshmalloW permission

        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
        }
    }

    boolean exit = false;

    @Override
    public void onBackPressed() {

        if (exit)
        {
            finish();
        }else
        {
            exit = true;
            Toast.makeText(getApplicationContext(),"press again to exit..",Toast.LENGTH_SHORT).show();
        }

        new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                exit = false;
            }
        }.start();

    }
}
