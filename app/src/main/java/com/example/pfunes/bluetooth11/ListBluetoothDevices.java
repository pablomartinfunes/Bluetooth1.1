package com.example.pfunes.bluetooth11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
                Toast.makeText(ListBluetoothDevices.this, listaDevs.get(position).getAddress() , Toast.LENGTH_SHORT).show();
                ConnectThread threadBT = new ConnectThread(listaDevs.get(position));
                threadBT.start();
            }
        });

    }


    private class ConnectThread extends Thread{

        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;
        private final java.util.UUID UUID = java.util.UUID.fromString("522b1afc-7ec2-4b5d-9cf8-4c198513fe0d");

        public ConnectThread(BluetoothDevice dev){

            BluetoothSocket socket = null;
            mmDevice = dev;

            try{
                socket = dev.createRfcommSocketToServiceRecord(UUID);
            }catch (IOException e){}

            mmSocket = socket;
        }

        @Override
        public void run() {
            super.run();

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try{
                mmSocket.connect();
            }catch (IOException e){
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

}


