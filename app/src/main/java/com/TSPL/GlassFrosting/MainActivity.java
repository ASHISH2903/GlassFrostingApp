package com.TSPL.GlassFrosting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.TSPL.R;

import androidRecyclerView.MessageAdapter;


public class MainActivity extends Activity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_FAIL = 6;
    public static final int CONNECTION_SUCCESS = 7;

    private Button glass_start,glass_reset,send_height_width;
    private EditText edt_txt_height,edt_txt_width,edt_txt_buzzer_time;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private EditText mOutEditText;
    private Button mSendButton,glass_clear,glass_resume,glass_stop,send_buzzer_time,status_connected;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        glass_clear = (Button) findViewById(R.id.glass_clear);

        glass_start = (Button) findViewById(R.id.glass_start);
        glass_reset = (Button) findViewById(R.id.glass_reset);
        send_height_width = (Button) findViewById(R.id.send_height_width);

        edt_txt_height = (EditText) findViewById(R.id.edt_txt_height);
        edt_txt_width = (EditText) findViewById(R.id.edt_txt_width);

        glass_resume = (Button) findViewById(R.id.glass_resume);
        glass_stop = (Button) findViewById(R.id.glass_stop);

        edt_txt_buzzer_time = (EditText) findViewById(R.id.edt_txt_buzzer_time);
        send_buzzer_time = (Button) findViewById(R.id.send_buzzer_time);

        status_connected = (Button) findViewById(R.id.status_connected);
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        send_height_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height,width;
                height = edt_txt_height.getText().toString();
                width = edt_txt_width.getText().toString();

                if(height.length()==0){
                    edt_txt_height.requestFocus();
                    edt_txt_height.setError("Please enter Height");
                }

                else if(width.length()==0)
                {
                    edt_txt_width.requestFocus();
                    edt_txt_width.setError("Please enter Width");
                }

                else
                {
                    String message = "H," + height + ":W," + width +":\r\n";
                    byte[] bytes = message.getBytes(Charset.defaultCharset());
                    mChatService.write(bytes);
                }
            }
        });

        glass_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String height,width;
                height = edt_txt_height.getText().toString();
                width = edt_txt_width.getText().toString();

                if(height.length()==0){
                    edt_txt_height.requestFocus();
                    edt_txt_height.setError("Please enter Height");
                }

                else if(width.length()==0)
                {
                    edt_txt_width.requestFocus();
                    edt_txt_width.setError("Please enter Width");
                }

                else
                {
                    String message = "START\r\n";
                    byte[] bytes = message.getBytes(Charset.defaultCharset());
                    mChatService.write(bytes);
                    glass_stop.setEnabled(true);
                    glass_stop.setTextColor(Color.YELLOW);
                    glass_stop.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));

                    send_buzzer_time.setEnabled(false);
                    send_buzzer_time.setTextColor(Color.BLACK);
                    send_buzzer_time.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                    send_height_width.setEnabled(false);
                    send_height_width.setTextColor(Color.BLACK);
                    send_height_width.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                    glass_reset.setEnabled(false);
                    glass_reset.setTextColor(Color.BLACK);
                    glass_reset.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                    glass_start.setEnabled(false);
                    glass_start.setTextColor(Color.BLACK);
                    glass_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                    glass_resume.setEnabled(false);
                    glass_resume.setTextColor(Color.BLACK);
                    glass_resume.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                    edt_txt_height.setText("");
                    edt_txt_width.setText("");
                }
            }
        });

        glass_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "RESET\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);

                send_height_width.setEnabled(true);
                send_height_width.setTextColor(Color.BLACK);
                send_height_width.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                glass_start.setEnabled(true);
                glass_start.setTextColor(Color.BLACK);
                glass_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                glass_resume.setEnabled(false);
                glass_resume.setTextColor(Color.BLACK);
                glass_resume.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
            }
        });

        glass_resume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "RESUME\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);

                glass_stop.setEnabled(true);
                glass_stop.setTextColor(Color.YELLOW);
                glass_stop.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));

                send_height_width.setEnabled(false);
                send_height_width.setTextColor(Color.BLACK);
                send_height_width.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                glass_reset.setEnabled(false);
                glass_reset.setTextColor(Color.BLACK);
                glass_reset.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                glass_start.setEnabled(false);
                glass_start.setTextColor(Color.BLACK);
                glass_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                glass_resume.setEnabled(false);
                glass_resume.setTextColor(Color.BLACK);
                glass_resume.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

            }
        });

        glass_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "STOP\r\n";
                byte[] bytes = message.getBytes(Charset.defaultCharset());
                mChatService.write(bytes);

                glass_reset.setEnabled(true);
                glass_reset.setTextColor(Color.BLACK);
                glass_reset.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                glass_resume.setEnabled(true);
                glass_resume.setTextColor(Color.BLACK);
                glass_resume.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                send_buzzer_time.setEnabled(true);
                send_buzzer_time.setTextColor(Color.BLACK);
                send_buzzer_time.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                glass_stop.setEnabled(false);
                glass_stop.setTextColor(Color.YELLOW);
                glass_stop.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape1));

                glass_start.setEnabled(false);
                glass_start.setTextColor(Color.BLACK);
                glass_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

                send_height_width.setEnabled(false);
                send_height_width.setTextColor(Color.BLACK);
                send_height_width.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));

            }
        });

        glass_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                messageList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        send_buzzer_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String buzzer_time;
                buzzer_time = edt_txt_buzzer_time.getText().toString();

                if(buzzer_time.length()==0){
                    edt_txt_buzzer_time.requestFocus();
                    edt_txt_buzzer_time.setError("Please enter valid time");
                }
                else
                {
                    int n = Integer.parseInt(buzzer_time);
                    if(n >= 0 && n <= 5)
                    {
                        edt_txt_buzzer_time.requestFocus();
                        edt_txt_buzzer_time.setError("Time must be Greater than 5");
                        edt_txt_buzzer_time.setText("");
                    }
                    else
                    {
                        String message = "B,"+buzzer_time+"\r\n";
                        byte[] bytes = message.getBytes(Charset.defaultCharset());
                        mChatService.write(bytes);
                        edt_txt_buzzer_time.setText("");
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            glass_stop.setEnabled(false);
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
                    if(readMessage.compareToIgnoreCase("FINISH\r")==0 || readMessage.compareToIgnoreCase("FINISH\r\n")==0 || readMessage.compareToIgnoreCase("FINISH")==0)
                    {
                        send_height_width.setEnabled(true);
                        glass_start.setEnabled(true);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                        messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                        break;
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
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void connect(View v) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }
}