package com.example.vittorusso.recognition;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;


public class FragmentDevice1 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final static String TAG = "FragmentNumber1";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView tvName;
    private TextView mConnectionState;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeServiceAccelerometer mBluetoothLeServiceAccelerometer;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ImageView ivConnect;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static final int REQUEST_ENABLE_BT = 1;

    public FragmentDevice1() {
    }

    public static FragmentDevice1 newInstance(String param1, String param2) {
        FragmentDevice1 fragment = new FragmentDevice1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("OnConnected");
            mBluetoothLeServiceAccelerometer = ((BluetoothLeServiceAccelerometer.LocalBinder) service).getService();
            if (!mBluetoothLeServiceAccelerometer.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            mBluetoothLeServiceAccelerometer.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("OnServiceDisconnected");
            mBluetoothLeServiceAccelerometer = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String value = intent.getStringExtra("WhatFragment");
            if(value.contains("Accelerometer")){
                if (BluetoothLeServiceAccelerometer.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
                    updateConnectionState(R.string.connected, R.mipmap.onblue);
                } else if (BluetoothLeServiceAccelerometer.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    updateConnectionState(R.string.disconnected, R.mipmap.offblue);
                } else if (BluetoothLeServiceAccelerometer.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeServiceAccelerometer.getSupportedGattServices());
                } else if (BluetoothLeServiceAccelerometer.ACTION_DATA_AVAILABLE.equals(action)) {
                    String data = intent.getStringExtra(BluetoothLeServiceAccelerometer.EXTRA_DATA);
                    mListener.onFragment1Interaction(data);
                }
            }
        }
    };

    public void startReading (){
        System.out.println("StartReading");
        if (mGattCharacteristics != null) {
            final BluetoothGattCharacteristic characteristic =
                    mGattCharacteristics.get(0).get(0);
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeServiceAccelerometer.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeServiceAccelerometer.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeServiceAccelerometer.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) != unknownServiceString){
                HashMap<String, String> currentServiceData = new HashMap<String, String>();
                currentServiceData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
                currentServiceData.put(LIST_UUID, uuid);
                gattServiceData.add(currentServiceData);

                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas =
                        new ArrayList<BluetoothGattCharacteristic>();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    currentCharaData.put(
                            LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                    currentCharaData.put(LIST_UUID, uuid);
                    if (SampleGattAttributes.lookup(uuid, unknownCharaString) != unknownCharaString) {
                        gattCharacteristicGroupData.add(currentCharaData);
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
        startReading();
        try {Thread.sleep(500);}
        catch (InterruptedException ex) {android.util.Log.d("Hello: ", ex.toString());}
        startReading();
        System.out.println("displayGatt");
    }




    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeServiceAccelerometer.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeServiceAccelerometer.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeServiceAccelerometer.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeServiceAccelerometer.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }



    private void updateConnectionState(final int resourceId, final int resourceId2) {
        mConnectionState.setText(resourceId);
        ivConnect.setImageResource(resourceId2);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mDeviceName = mParam1;
            mDeviceAddress = mParam2;
        }
        if (!(getActivity().getPackageManager()).hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((ControlActivity) getActivity()).setUpdateFrag1(new ControlActivity.updateFragment1() {
            @Override
            public void connectFrag1() {
                mBluetoothLeServiceAccelerometer.connect(mDeviceAddress);
            }

            @Override
            public void disconnectFrag1() {
                mBluetoothLeServiceAccelerometer.disconnect();
                updateConnectionState(R.string.disconnected, R.mipmap.offblue);
            }
        });
        return inflater.inflate(R.layout.fragment_fragment_device1, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeServiceAccelerometer.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mConnectionState = getView().findViewById(R.id.connection_state);
        ivConnect = getView().findViewById(R.id.ivFrag1);
        tvName = getView().findViewById(R.id.tvName1);
        tvName.setText(R.string.tvName1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        System.out.println("OnResumen");
        if (mBluetoothLeServiceAccelerometer != null) {
            final boolean result = mBluetoothLeServiceAccelerometer.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
        mBluetoothLeServiceAccelerometer = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public interface OnFragmentInteractionListener {
        void onFragment1Interaction(String data);
    }


    public void setmListener1 (OnFragmentInteractionListener mListener){
        this.mListener = mListener;
    }


}
