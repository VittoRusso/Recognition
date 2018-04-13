package com.example.vittorusso.recognition;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vittorusso.recognition.Others.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;


public class FragmentDevice2 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private final static String TAG = "FragmentNumber2";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView tvName;
    private TextView mConnectionState2;
    private String mDeviceName2;
    private String mDeviceAddress2;
    private BluetoothAdapter mBluetoothAdapter2;
    private BluetoothLeServiceHeart mBluetoothLeService2;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics2 = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected2 = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic2;
    private ImageView ivConnect;


    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static final int REQUEST_ENABLE_BT = 1;

    private OnFragmentInteractionListener mListener2;

    public FragmentDevice2() {
    }


    public static FragmentDevice2 newInstance(String param1, String param2) {
        FragmentDevice2 fragment = new FragmentDevice2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private final ServiceConnection mServiceConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("OnConnected2");
            mBluetoothLeService2 = ((BluetoothLeServiceHeart.LocalBinder) service).getService();
            if (!mBluetoothLeService2.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            mBluetoothLeService2.connect(mDeviceAddress2);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("OnServiceDisconnected2");
            mBluetoothLeService2 = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String value = intent.getStringExtra("WhatFragment");
            if(value.contains("HeartRate")){
                if (BluetoothLeServiceHeart.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected2 = true;
                    updateConnectionState(R.string.connected, R.mipmap.onblue);
                } else if (BluetoothLeServiceHeart.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected2 = false;
                    updateConnectionState(R.string.disconnected, R.mipmap.offblue);
                } else if (BluetoothLeServiceHeart.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    displayGattServices2(mBluetoothLeService2.getSupportedGattServices());
                } else if (BluetoothLeServiceHeart.ACTION_DATA_AVAILABLE.equals(action)) {
                    String data = intent.getStringExtra(BluetoothLeServiceHeart.EXTRA_DATA);
                    mListener2.onFragment2Interaction(data);
                }
            }
        }
    };

    public void startReading2 (){
        System.out.println("StartReading2");
        if (mGattCharacteristics2 != null) {
            final BluetoothGattCharacteristic characteristic =
                    mGattCharacteristics2.get(0).get(0);
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (mNotifyCharacteristic2 != null) {
                    mBluetoothLeService2.setCharacteristicNotification(
                            mNotifyCharacteristic2, false);
                    mNotifyCharacteristic2 = null;
                }
                mBluetoothLeService2.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic2 = characteristic;
                mBluetoothLeService2.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }


    private void displayGattServices2(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics2 = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

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
                mGattCharacteristics2.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
        startReading2();
        try {Thread.sleep(500);}
        catch (InterruptedException ex) {android.util.Log.d("Hello: ", ex.toString());}
        startReading2();
        System.out.println("displayGatt2");
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeServiceHeart.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeServiceHeart.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeServiceHeart.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeServiceHeart.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void updateConnectionState(final int resourceId, final int resourceId2) {
        mConnectionState2.setText(resourceId);
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
            mDeviceName2 = mParam1;
            mDeviceAddress2 = mParam2;
        }
        if (!(getActivity().getPackageManager()).hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter2 = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter2.isEnabled()) {
            if (!mBluetoothAdapter2.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((ControlActivity) getActivity()).setUpdateFrag2(new ControlActivity.updateFragment2() {
            @Override
            public void connectFrag2() {
             mBluetoothLeService2.connect(mDeviceAddress2);
            }

            @Override
            public void disconnectFrag2() {
                mBluetoothLeService2.disconnect();
                updateConnectionState(R.string.disconnected, R.mipmap.offblue);
            }
        });

        return inflater.inflate(R.layout.fragment_fragment_device2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent gattServiceIntent2 = new Intent(getActivity(), BluetoothLeServiceHeart.class);
        getActivity().bindService(gattServiceIntent2, mServiceConnection2, BIND_AUTO_CREATE);

        mConnectionState2 = getView().findViewById(R.id.connection_state2);
        ivConnect = getView().findViewById(R.id.ivFrag2);
        tvName = getView().findViewById(R.id.tvName2);
        tvName.setText(R.string.tvName2);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter());
        System.out.println("OnResumen2");
        if (mBluetoothLeService2 != null) {
            final boolean result = mBluetoothLeService2.connect(mDeviceAddress2);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection2);
        mBluetoothLeService2 = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener2 = null;

    }

    public interface OnFragmentInteractionListener {
        void onFragment2Interaction(String data);
    }

    public void setmListener2 (OnFragmentInteractionListener mListener2){
        this.mListener2 = mListener2;
    }
}
