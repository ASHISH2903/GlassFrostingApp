package com.TSPL.timerrelay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.TSPL.R;

import androidRecyclerView.MessageAdapter;


public class MainActivity extends Activity {

    // Message types sent from the BluetoothChatService Handler

    String[] listOfRelay = {"Select Relay","0", "1", "2", "3"};
    Button btnEnableDisable_Discoverable;
    private RadioGroup rg;
    private RadioButton rb_hold,rb_reset;
    private Button status1,status2,status3,status4;
    private Button btn_start,btn_hold,btn_reset,radio_send;
    private Button setupBtn,configBtn;
    private Button btn_load,btn_save;
    private Spinner list_of_relay;
    private EditText edt_txt_on,edt_txt_off,select_time;
    private Button relay_on,active_relay,total_time_cycle;
    private String selected_relay = null;
    private CheckBox ch1,ch2,ch3,ch4,auto_start_check;
    private RadioButton hr,min,sec;
    private RadioGroup time_radio_group;
    LinearLayout setupLine,configLine;


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_FAIL = 6;
    public static final int CONNECTION_SUCCESS = 7;



    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private EditText mOutEditText,edt_txt_password,edt_txt_new_password,select_on_time,select_span_time,select_cycle_time,select_sequence;
    private Button mSendButton,status_connected,logout_btn,dcs_settings,relay_status,login_to_device,config_btn,setup_btn;
    private Button reset_password_btn,reset_password,scan,disc,clear_eeprom_btn,btn_dcs_send;
    private LinearLayout login_linear_page,relay_status_linear_view,reset_password_linear_page,setup_linear_page,config_linear_page,dcs_linear_page;

    private int mHour, mMinute;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MessageAdapter mAdapter;

    public int counter = 0;

    private List<androidRecyclerView.Message> messageList = new ArrayList<androidRecyclerView.Message>();

    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    String amPm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /**NORMAL**/
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /**SORT**/
        /*mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();*/

        //login_btn = (Button) findViewById(R.id.login_btn);
        dcs_settings = (Button) findViewById(R.id.dcs_settings);
        relay_status = (Button) findViewById(R.id.relay_status);
        login_to_device = (Button) findViewById(R.id.login_to_device);
        status_connected = (Button) findViewById(R.id.status_connected);
        config_btn = (Button) findViewById(R.id.config_btn);
        setup_btn = (Button) findViewById(R.id.setup_btn);
        reset_password = (Button) findViewById(R.id.reset_password);
        reset_password_btn = (Button) findViewById(R.id.reset_password_btn);
        select_time = (EditText) findViewById(R.id.select_time);

        edt_txt_password = (EditText) findViewById(R.id.edt_txt_password);
        edt_txt_password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });


        edt_txt_new_password = (EditText) findViewById(R.id.edt_txt_new_password);
        edt_txt_new_password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });


        login_linear_page = (LinearLayout) findViewById(R.id.login_linear_page);
        relay_status_linear_view = (LinearLayout) findViewById(R.id.relay_status_linear_view);
        reset_password_linear_page = (LinearLayout) findViewById(R.id.reset_password_linear_page);
        setup_linear_page = (LinearLayout) findViewById(R.id.setup_linear_page);
        config_linear_page = (LinearLayout) findViewById(R.id.config_linear_page);


        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rb_hold = (RadioButton) findViewById(R.id.Radio_hold);
        rb_reset = (RadioButton) findViewById(R.id.Radio_reset);

        status1 = (Button) findViewById(R.id.Relay_Status_1);
        status2 = (Button) findViewById(R.id.Relay_Status_2);
        status3 = (Button) findViewById(R.id.Relay_Status_3);
        status4 = (Button) findViewById(R.id.Relay_Status_4);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_hold = (Button) findViewById(R.id.btn_hold);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        //radio_send = (Button)  findViewById(R.id.send_radio);

        btn_load = (Button) findViewById(R.id.btn_load);
        btn_save = (Button) findViewById(R.id.btn_save);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        scan = (Button) findViewById(R.id.scan);
        disc = (Button) findViewById(R.id.discoverable);

        clear_eeprom_btn = (Button) findViewById(R.id.clear_eeprom_btn);

        list_of_relay = (Spinner) findViewById(R.id.list_of_relay);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listOfRelay);
        list_of_relay.setAdapter(adapter);

        edt_txt_on = (EditText) findViewById(R.id.edt_txt_on);

        relay_on = (Button) findViewById(R.id.relay_on);
        active_relay = (Button) findViewById(R.id.active_relay);
        total_time_cycle = (Button) findViewById(R.id.total_time_cycle);

        edt_txt_on.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });
        //  edt_txt_off.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });


        ch1 = (CheckBox) findViewById(R.id.checked1);
        ch2 = (CheckBox) findViewById(R.id.checked2);
        ch3 = (CheckBox) findViewById(R.id.checked3);
        ch4 = (CheckBox) findViewById(R.id.checked4);
        auto_start_check = (CheckBox) findViewById(R.id.autostart_check);

        dcs_linear_page = (LinearLayout)  findViewById(R.id.dcs_linear_page);
        select_on_time = (EditText) findViewById(R.id.select_on_time);
        select_span_time = (EditText) findViewById(R.id.select_span_time);
        select_cycle_time = (EditText) findViewById(R.id.select_cycle_time);
        select_sequence = (EditText) findViewById(R.id.select_sequence);
        select_sequence.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });

        btn_dcs_send = (Button) findViewById(R.id.btn_dcs_send);

        status_connected = (Button) findViewById(R.id.status_connected);
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /******************* CLEAR EEPROM BUTTON CLICK EVENT *******************/
        clear_eeprom_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "CLEAR\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* LOGIN BUTTON CLICK EVENT *******************/
        logout_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scan.setVisibility(View.VISIBLE);
                disc.setVisibility(View.VISIBLE);
                clear_eeprom_btn.setVisibility(View.GONE);
                reset_password_linear_page.setVisibility(View.GONE);
                config_linear_page.setVisibility(View.GONE);
                setup_linear_page.setVisibility(View.GONE);
                relay_status_linear_view.setVisibility(View.GONE);
                login_linear_page.setVisibility(View.GONE);
                config_btn.setVisibility(View.GONE);
                setup_btn.setVisibility(View.GONE);
                dcs_settings.setVisibility(View.GONE);
                relay_status.setVisibility(View.GONE);
                reset_password_btn.setVisibility(View.GONE);
                logout_btn.setVisibility(View.GONE);
                dcs_linear_page.setVisibility(View.GONE);
            }
        });

        /******************* LOGIN BUTTON CLICK EVENT *******************/

        login_to_device.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String get_pwd = edt_txt_password.getText().toString();

                if(get_pwd.length()==0)
                {
                    edt_txt_password.requestFocus();
                    edt_txt_password.setError("Please enter password");
                }

                String message = "PWD:"+get_pwd+"\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);

            }
        });

        /******************* RESET PASSWORD CLICK EVENT *******************/

        reset_password_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_password_linear_page.setVisibility(View.VISIBLE);
                config_linear_page.setVisibility(View.GONE);
                setup_linear_page.setVisibility(View.GONE);
                relay_status_linear_view.setVisibility(View.GONE);
                dcs_linear_page.setVisibility(View.GONE);
            }
        });

        /******************* RESET PASSWORD CLICK EVENT *******************/

        reset_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String New_Password = edt_txt_new_password.getText().toString();
                String message = "NPWD:"+New_Password+"\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
                Toast.makeText(MainActivity.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                reset_password_linear_page.setVisibility(View.GONE);
                edt_txt_new_password.setText("");
                dcs_linear_page.setVisibility(View.GONE);
            }
        });

        /******************* RELAY STATUS CLICK EVENT *******************/

        relay_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relay_status_linear_view.setVisibility(View.VISIBLE);
                String message = "READ\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                config_linear_page.setVisibility(View.GONE);
                setup_linear_page.setVisibility(View.GONE);
                reset_password_linear_page.setVisibility(View.GONE);
                dcs_linear_page.setVisibility(View.GONE);
                mChatService.write(bytes);
                messageList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        /******************* CONFIG BUTTON CLICK EVENT *******************/

        config_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                config_linear_page.setVisibility(View.VISIBLE);
                setup_linear_page.setVisibility(View.GONE);
                reset_password_linear_page.setVisibility(View.GONE);
                relay_status_linear_view.setVisibility(View.GONE);
                dcs_linear_page.setVisibility(View.GONE);

            }
        });

        /******************* SETUP BUTTON CLICK EVENT *******************/
        setup_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setup_linear_page.setVisibility(View.VISIBLE);
                config_linear_page.setVisibility(View.GONE);
                reset_password_linear_page.setVisibility(View.GONE);
                relay_status_linear_view.setVisibility(View.GONE);
                dcs_linear_page.setVisibility(View.GONE);
            }
        });

        /******************* DCS SETTING BUTTON CLICK EVENT *******************/

        dcs_settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dcs_linear_page.setVisibility(View.VISIBLE);
                setup_linear_page.setVisibility(View.GONE);
                config_linear_page.setVisibility(View.GONE);
                reset_password_linear_page.setVisibility(View.GONE);
                relay_status_linear_view.setVisibility(View.GONE);
            }
        });

        /******************* DCS SETTING BUTTON CLICK EVENT *******************/
        select_on_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
                final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);
                numberPickerHour.setMaxValue(23);
                numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
                final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                numberPickerMinutes.setMaxValue(59);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
                numberPickerSeconds.setMaxValue(59);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //select_time.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                        select_on_time.setText(String.format("%02d:%02d:%02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Hours", numberPickerHour.getValue());
                        editor.putInt("Minutes", numberPickerMinutes.getValue());
                        editor.putInt("Seconds", numberPickerSeconds.getValue());
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        /******************* DCS SETTING BUTTON CLICK EVENT *******************/
        select_span_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
                final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);
                numberPickerHour.setMaxValue(23);
                numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
                final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                numberPickerMinutes.setMaxValue(59);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
                numberPickerSeconds.setMaxValue(59);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //select_time.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                        select_span_time.setText(String.format("%02d:%02d:%02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Hours", numberPickerHour.getValue());
                        editor.putInt("Minutes", numberPickerMinutes.getValue());
                        editor.putInt("Seconds", numberPickerSeconds.getValue());
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        /******************* DCS SETTING BUTTON CLICK EVENT *******************/
        select_cycle_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
                final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);
                numberPickerHour.setMaxValue(23);
                numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
                final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                numberPickerMinutes.setMaxValue(59);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
                numberPickerSeconds.setMaxValue(59);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //select_time.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                        select_cycle_time.setText(String.format("%02d:%02d:%02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Hours", numberPickerHour.getValue());
                        editor.putInt("Minutes", numberPickerMinutes.getValue());
                        editor.putInt("Seconds", numberPickerSeconds.getValue());
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        btn_dcs_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String onTime, spanTime, cycleTime, sequence;

                /**** ON TIME ****/
                onTime = select_on_time.getText().toString();
                String on_Time [] = onTime.split(":");
                int h = Integer.parseInt(on_Time[0]);
                h = h * 3600;
                int m = Integer.parseInt(on_Time[1]);
                m = m * 60;
                int s = Integer.parseInt(on_Time[2]);
                int Full_On_Time = h + m + s;
                String dcs_on_time = String.valueOf(Full_On_Time);

                /**** SPAN TIME ****/
                spanTime = select_span_time.getText().toString();
                String span_Time [] = spanTime.split(":");
                h = Integer.parseInt(span_Time[0]);
                h = h * 3600;
                m = Integer.parseInt(span_Time[1]);
                m = m * 60;
                s = Integer.parseInt(span_Time[2]);
                int Full_Span_Time = h + m + s;
                String dcs_span_time = String.valueOf(Full_Span_Time);

                /**** CYCLE TIME ****/
                cycleTime = select_cycle_time.getText().toString();
                String cycle_Time [] = cycleTime.split(":");
                h = Integer.parseInt(cycle_Time[0]);
                h = h * 3600;
                m = Integer.parseInt(cycle_Time[1]);
                m = m * 60;
                s = Integer.parseInt(cycle_Time[2]);
                int Full_Cycle_Time = h + m + s;
                String dcs_cycle_time = String.valueOf(Full_Cycle_Time);

                /**** SEQUENCE TIME ****/
                sequence = select_sequence.getText().toString();

                String message = "DCS:"+dcs_on_time+","+dcs_span_time+","+dcs_cycle_time+","+sequence+"\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
                select_on_time.setText("");
                select_span_time.setText("");
                select_cycle_time.setText("");
                select_sequence.setText("");
            }
        });

        /******************* SETUP HOLD RESET BUTTON CLICK EVENT *******************/
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rg.findViewById(checkedId);
                int index = rg.indexOfChild(radioButton);
                switch (index)
                {
                    case 0:
                        String selectedRadioButtonText1 = "CONFIG:HOLD\r\n";
                        byte[] bytes1 = selectedRadioButtonText1.getBytes(Charset.defaultCharset());
                        mChatService.write(bytes1);
                        break;
                    case 1:
                        String selectedRadioButtonText2 = "CONFIG:RESET\r\n";
                        byte[] bytes2 = selectedRadioButtonText2.getBytes(Charset.defaultCharset());
                        mChatService.write(bytes2);
                        break;
                }
            }
        });

        /******************* SETUP START BUTTON CLICK EVENT *******************/
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "START\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* SETUP HOLD BUTTON CLICK EVENT *******************/
        btn_hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "HOLD\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* SETUP RESET BUTTON CLICK EVENT *******************/
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "RESET\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* SETUP AUTO START CLICK EVENT *******************/
        auto_start_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auto_start_check.isChecked())
                {
                    String message = "CONFIG>AUTOSTART:1\r\n";
                    byte[] bytes = message.getBytes(Charset.defaultCharset());
                    mChatService.write(bytes);
                }
                else
                {
                    String message = "CONFIG>AUTOSTART:0\r\n";
                    byte[] bytes = message.getBytes(Charset.defaultCharset());
                    mChatService.write(bytes);
                }
            }
        });


        /******************* CONFIG SAVE BUTTON CLICK EVENT *******************/
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "SAVE\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* CONFIG LOAD BUTTON CLICK EVENT *******************/
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "LOAD\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* CONFIG TRANSITION TIME CLICK EVENT *******************/
        relay_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_relay = list_of_relay.getSelectedItem().toString();
                String[] strings = edt_txt_on.getText().toString().split(",");
                Arrays.sort(strings);
                String output = TextUtils.join(",",strings);
                String message = "CONFIG>T:"+selected_relay+":"+output+"\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
                edt_txt_on.setText("");
            }
        });

        /******************* CONFIG ACTIVE RELAY CLICK EVENT *******************/
        active_relay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="";
                if(ch1.isChecked())
                {
                    msg = msg + "1,";
                }
                else
                {
                    msg = msg + "0,";
                }
                if(ch2.isChecked())
                {
                    msg = msg + "1,";
                }
                else
                {
                    msg = msg + "0,";
                }
                if(ch3.isChecked())
                {
                    msg = msg + "1,";
                }
                else
                {
                    msg = msg + "0,";
                }
                if(ch4.isChecked())
                {
                    msg = msg + "1";
                }
                else
                {
                    msg = msg + "0";
                }
                String message = "CONFIG>ACTIVE:"+msg+"\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);
            }
        });

        /******************* CONFIG TOTAL TIME CLICK EVENT *******************/
       select_time.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
               final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);
               numberPickerHour.setMaxValue(23);
               numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
               final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
               numberPickerMinutes.setMaxValue(59);
               numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
               final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
               numberPickerSeconds.setMaxValue(59);
               numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));
               Button cancel = view.findViewById(R.id.cancel);
               Button ok = view.findViewById(R.id.ok);
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setView(view);
               final AlertDialog alertDialog = builder.create();
               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       alertDialog.dismiss();
                   }
               });
               ok.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //select_time.setText(numberPickerHour.getValue() + ":" + numberPickerMinutes.getValue() + ":" + numberPickerSeconds.getValue());
                       select_time.setText(String.format("%02d:%02d:%02d", numberPickerHour.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       editor.putInt("Hours", numberPickerHour.getValue());
                       editor.putInt("Minutes", numberPickerMinutes.getValue());
                       editor.putInt("Seconds", numberPickerSeconds.getValue());
                       editor.apply();
                       alertDialog.dismiss();
                   }
               });
               alertDialog.show();
           }
       });

        /******************* CONFIG TOTAL TIME CLICK EVENT *******************/
       total_time_cycle.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               String message = select_time.getText().toString();
               String total_time [] = message.split(":");
               int h = Integer.parseInt(total_time[0]);
               h = h * 3600;
               int m = Integer.parseInt(total_time[1]);
               m = m * 60;
               int s = Integer.parseInt(total_time[2]);
               int Full_Total_Time = h + m + s;
               String send_time = String.valueOf(Full_Total_Time);
               message = "CONFIG>CYCLE:"+send_time+"\r\n";
               byte[] bytes = message.getBytes(Charset.defaultCharset());
               mChatService.write(bytes);
               select_time.setText("");
               Toast.makeText(MainActivity.this, "Total Time : "+Full_Total_Time, Toast.LENGTH_SHORT).show();

           }
       });

        /*total_time_cycle.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int selectedRadioButtonID = time_radio_group.getCheckedRadioButtonId();
                if (selectedRadioButtonID != -1) {
                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                    if(selectedRadioButton.getText().toString().equals("H"))
                    {
                        String time = select_time.getText().toString();
                        int h = Integer.parseInt(time);
                        h = h * 3600;
                        time = String.valueOf(h);
                        String message = "CONFIG:CYCLE:"+time+"\r\n";
                        byte[] bytes = message.getBytes(Charset.defaultCharset());
                        mChatService.write(bytes);
                        select_time.setText("");
                    }
                    if(selectedRadioButton.getText().toString().equals("M"))
                    {
                        String time = select_time.getText().toString();
                        int m = Integer.parseInt(time);
                        m = m * 60;
                        time = String.valueOf(m);
                        String message = "CONFIG:CYCLE:"+time+"\r\n";
                        byte[] bytes = message.getBytes(Charset.defaultCharset());
                        mChatService.write(bytes);
                        select_time.setText("");
                    }
                    if(selectedRadioButton.getText().toString().equals("S"))
                    {
                        String time = select_time.getText().toString();
                         message = "CONFIG:CYCLE:"+time+"\r\n";
                        byte[] bytes = message.getBytes(Charset.defaultCharset());
                        mChatService.wrStringite(bytes);
                        select_time.setText("");
                    }
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    // not required
    private void setupChat() {
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    return true;
                }
            };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mAdapter.notifyDataSetChanged();
                    messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    readMessage+="\n";
                    if(readMessage.compareToIgnoreCase("correct\r\n")==0)
                    {
                        login_linear_page.setVisibility(View.GONE);
                        scan.setVisibility(View.GONE);
                        disc.setVisibility(View.GONE);
                        dcs_linear_page.setVisibility(View.GONE);
                        config_btn.setVisibility(View.VISIBLE);
                        setup_btn.setVisibility(View.VISIBLE);
                        dcs_settings.setVisibility(View.VISIBLE);
                        relay_status.setVisibility(View.VISIBLE);
                        reset_password_btn.setVisibility(View.VISIBLE);
                        logout_btn.setVisibility(View.VISIBLE);
                        clear_eeprom_btn.setVisibility(View.VISIBLE);
                        edt_txt_password.setText("");
                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else if(readMessage.compareToIgnoreCase("wrong\r\n")==0)
                    {
                        edt_txt_password.requestFocus();
                        edt_txt_password.setText("");
                        edt_txt_password.setError("Incorrect Password");
                    }
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    status_connected.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_on));
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    login_linear_page.setVisibility(View.VISIBLE);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_FAIL:
                    status_connected.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_off));
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_SUCCESS:
                    status_connected.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_on));
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void connect(View v) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        //login_linear_page.setVisibility(View.VISIBLE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }

}