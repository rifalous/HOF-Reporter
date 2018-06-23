package chierra.hof_reporter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class DataFormActivity extends AppCompatActivity {
    private int PLACE_PICKER_REQUEST = 1;

    private EditText mEditTextName;
    private TextView mEditTextAddress;
    private EditText mEditTextPhone;
    private ImageView mPlacePicker;
    private GoogleApiClient mGoogleApiClient;
    private String mLongitude;
    private String mLatitude;
    private LinearLayout mAddressLayout;
    SharedPreferences settings;


    private String id, key, name, phone, address, nama, telp, alamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_form);

        nama = "";
        telp = "";
        alamat = "";
        mLongitude = "";
        mLatitude = "";

        nama = getIntent().getStringExtra("nama");
        telp = getIntent().getStringExtra("telp");
        alamat = getIntent().getStringExtra("alamat");


        Button button = findViewById(R.id.save);
        settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        mEditTextName = findViewById(R.id.edit_name);
        mEditTextName.setText(nama);
        mEditTextPhone = findViewById(R.id.edit_phone);
        mEditTextPhone.setText(telp);
        mEditTextAddress = findViewById(R.id.preview_address);
        mAddressLayout = findViewById(R.id.address_layout);
        try{
            if(alamat.equals(null) || alamat.equals("")){

            }else{
                mEditTextAddress.setText(alamat);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        mPlacePicker = findViewById(R.id.pick_location);

        mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(DataFormActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        getPreferences();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void saveData(){
        name = mEditTextName.getText().toString();
        address = mEditTextAddress.getText().toString();
        phone = mEditTextPhone.getText().toString();

            if(mEditTextName.getText().toString().matches("") || mEditTextAddress.getText().toString().matches("") || mEditTextPhone.getText().toString().matches("")){
                Toast.makeText(this, "Periksa kembali data diri anda", Toast.LENGTH_SHORT).show();
            }else{
                if(mLongitude.matches("")||mLatitude.matches("")){
                    Toast.makeText(this, "Mohon tandai ulang alamat anda", Toast.LENGTH_SHORT).show();
                }else{
                    BackgroundTask sendData = new BackgroundTask();
                    sendData.execute(id, key, name, address, phone);
                    Intent tempIntent = new Intent(DataFormActivity.this, MainActivity.class);
                    tempIntent.putExtra("TAG", "home");
                    startActivity(tempIntent);
                }

            }

    }

    public void getPreferences(){
        id = settings.getString("ID", null);
        key = settings.getString("KEY", null);
    }


    class BackgroundTask extends AsyncTask<String, Void, String> {

        String add_url;

        @Override
        protected void onPreExecute() {

            add_url = "http://cerebrum.id/project/edit_user.php";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... args) {
            String device_id, device_key, name, address, phone;
            device_id = args[0];
            device_key = args[1];
            name = args[2];
            address = args[3];
            phone = args[4];



            try{
                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("device_id", "UTF-8")
                        + "=" + URLEncoder.encode(device_id, "UTF-8")

                        + "&" + URLEncoder.encode("device_key", "UTF-8") + "="
                        + URLEncoder.encode(device_key, "UTF-8")

                        + "&" + URLEncoder.encode("name", "UTF-8") + "="
                        + URLEncoder.encode(name, "UTF-8")

                        + "&" + URLEncoder.encode("address", "UTF-8") + "="
                        + URLEncoder.encode(address, "UTF-8")

                        + "&" + URLEncoder.encode("lng", "UTF-8") + "="
                        + URLEncoder.encode(mLongitude, "UTF-8")

                        + "&" + URLEncoder.encode("lat", "UTF-8") + "="
                        + URLEncoder.encode(mLatitude, "UTF-8")

                        + "&" + URLEncoder.encode("phone", "UTF-8")
                        + "=" + URLEncoder.encode(phone, "UTF-8");


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Sukses Mengubah Profil";


            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                mLongitude = longitude;
                mLatitude = latitude;
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Longitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                mLongitude = longitude;
                mLatitude = latitude;
                mEditTextAddress.setText(address);
            }
        }
    }
}
