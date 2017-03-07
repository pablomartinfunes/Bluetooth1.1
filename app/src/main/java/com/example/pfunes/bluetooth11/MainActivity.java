package com.example.pfunes.bluetooth11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btnActivarDesactivar;
    private BluetoothAdapter blueAdapter;
    private ArrayList<BluetoothDevice> blueDevs;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, blueAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        mostrarMensaje("Estado BT ON");
                        btnActivarDesactivar.setText("Desactivar Bluetooth");
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        mostrarMensaje("Estado BT turning ON");
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        mostrarMensaje("Estado BT OFF");
                        btnActivarDesactivar.setText("Activar Bluetooth");
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mostrarMensaje("Estado BT Turnning OFF");
                        break;
                }

            }

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                blueDevs.add(dev);
                return;
            }

            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                mostrarMensaje("Comenzo el escaneo de BT Devs");
                return;
            }

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, blueAdapter.ERROR);
                if(state == BluetoothDevice.BOND_BONDED){
                    mostrarMensaje("BOND_BONDED");
                }else if(state == BluetoothDevice.BOND_BONDING){
                    mostrarMensaje("BOND_BONDING");
                }else{
                    mostrarMensaje("BOND_NONE");
                }

            }

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mostrarMensaje("Finalizo el escaneo de dispositivos BT.");
                Intent i = new Intent(getApplicationContext(), ListBluetoothDevices.class);
                i.putParcelableArrayListExtra("devices.list", blueDevs);
                startActivity(i);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        btnActivarDesactivar = (Button) findViewById(R.id.btnBluetooth);
        blueDevs = new ArrayList<>();

        IntentFilter i = new IntentFilter();
        i.addAction(BluetoothDevice.ACTION_FOUND);
        i.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        i.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        i.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver,i);
    }

    public void activarDesactivarBT(View v){

        if(blueAdapter == null){
            mostrarMensaje("Su equipo no posee Bluetooth compatible.");

        }else if(!blueAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

        }else{
            blueAdapter.disable();
        }

    }

    public void buscarBluetoothDevs(View v){
        blueAdapter.startDiscovery();
    }


    private void mostrarMensaje(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
