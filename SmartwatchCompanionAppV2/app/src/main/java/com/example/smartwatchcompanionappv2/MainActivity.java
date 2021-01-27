package com.example.smartwatchcompanionappv2;

/* Referenced Material

I am not an android developer and as a result have limited knowledge in working with android. Here are some of the references I
Used that helped me greatly in creating this application.
NordicSemiconductor/Android-Scanner-Compat-Library: https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library
Background operation of BLE Library with Android 8 - Request for Example: https://devzone.nordicsemi.com/f/nordic-q-a/50642/background-operation-of-ble-library-with-android-8---request-for-example

 */

import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static MainActivity reference;
    public static String serviceUUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    public static String charUUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";

    public static String notificationData = "";

    private static String TAG = "Main";
    public static BLEGATT blegatt;
    public static String[] tabText = {"First Tab", "Second Tab"};
    public static TextView txtView;

    private NotificationReceiver nReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = (TextView) findViewById(R.id.textView);
        txtView.setText("Test");
        reference = this;

        //init notification receiver
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NLService.NOTIFICATION_ACTION);
        registerReceiver(nReceiver, filter);

        //get the current notifications by broadcasting an intent
        Intent i = new Intent(NLService.GET_NOTIFICATION_INTENT);
        i.putExtra("command", "list");
        sendBroadcast(i);



        BLEScanner.startScan(this.getApplicationContext());
        blegatt = new BLEGATT(this.getApplicationContext(), (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE));
    }

    public static void updateStatusText() {
        reference.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String statusText = BLEGATT.getStatusText()
                        +"\nNotification Data: \n" + notificationData;
                reference.txtView.setText(statusText);
            }
        });

    }

    public void sdt(View view) {
        sendDateAndTime();
    }

    void sendDateAndTime() {
        Log.d(TAG, "SENDING DATE AND TIME");
        blegatt.write(BLEGATT.getDateAndTime());
    }

    //receives the data from the NLService and updates fields in this class.
    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onRecieve method callback received " + intent.getStringExtra("notification_event"));
            String temp = intent.getStringExtra("notification_event");
            if (!notificationData.contains(temp)) {
                temp = intent.getStringExtra("notification_event") + "\n" + notificationData;
                notificationData = temp.replace("\n\n", "\n");
            }
updateStatusText();
        }
    }



}