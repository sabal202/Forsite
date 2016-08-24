package ru.main.sabal.materialapp;

        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.app.Activity;
        import android.content.pm.ActivityInfo;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;
        import android.content.Intent;
        import java.util.Set;


public class SetDevice extends Activity {

    public ListView DeviceListView;
    public String SelectedDevice;
    public String Names[] = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_set_device);
        DeviceListView = (ListView) findViewById(R.id.Choose);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            ToScreen("\nSwitch on BlueTooth, please!\n");
            SetDevice.this.finish();
        }
        final Set<BluetoothDevice> DeviceList = bluetoothAdapter.getBondedDevices();
        Names = new String [DeviceList.size()];
        int j = 0;
        for(BluetoothDevice i : DeviceList) {
            Names[j ++] = i.getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_1, Names);

        DeviceListView.setAdapter(adapter);

        DeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedDevice = Names[position];
                Intent intent = new Intent(SetDevice.this, Analizator.class);
                intent.putExtra("Device Name", SelectedDevice);
                startActivity(intent);

            }
        });
    }

    public void ToScreen(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
