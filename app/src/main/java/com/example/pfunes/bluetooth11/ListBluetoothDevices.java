package com.example.pfunes.bluetooth11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * Created by pfunes on 02/03/17.
 */

public class ListBluetoothDevices extends AppCompatActivity {

    private ListView lvDevs;
    private ArrayList nombresDevsBT = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_bluetooth_devices);

        Bundle extras = getIntent().getExtras();
        final ArrayList<BluetoothDevice> listaDevs = extras.getParcelableArrayList("devices.list");

        for (BluetoothDevice dev : listaDevs)
            nombresDevsBT.add(dev.getName());

        lvDevs = (ListView) findViewById(R.id.lvDevsToConnet);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nombresDevsBT);
        lvDevs.setAdapter(adapter);

        lvDevs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListBluetoothDevices.this, "Name: " + listaDevs.get(position).getName() , Toast.LENGTH_SHORT).show();
                //Toast.makeText(ListBluetoothDevices.this, "Address: " + listaDevs.get(position).getAddress() , Toast.LENGTH_SHORT).show();
                //Toast.makeText(ListBluetoothDevices.this, "UUIDs: " + listaDevs.get(position).getUuids() , Toast.LENGTH_SHORT).show();

                // pairDevice(listaDevs.get(position));
                new ConnectThread(listaDevs.get(position));

            }
        });

    }

    private void pairDevice(BluetoothDevice device){

        try{
            Method method = device.getClass().getMethod("createBond", (Class[])null);
            method.invoke(device, (Object[]) null);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class ConnectThread extends Thread{

        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;
        private final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice dev){

            BluetoothSocket socket = null;
            mmDevice = dev;

            //           BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
//            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            try{
                socket = dev.createInsecureRfcommSocketToServiceRecord(UUID);
            }catch (IOException e){
                Log.e("bluekey", "Error de creacion de socket");

            }
            Log.i("bluekey", "Se creo el socket");
            mmSocket = socket;
            this.start();
        }

        @Override
        public void run() {
            super.run();

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try{
                Log.i("bluekey", "Antes de connect");
                mmSocket.connect();
                Log.i("bluekey", "Despues de connect");
            }catch (IOException e){
                try {
                    Log.e("bluekey", "Error de connexion de socket");
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

}