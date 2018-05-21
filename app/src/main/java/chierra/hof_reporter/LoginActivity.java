package chierra.hof_reporter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "USER_DATA";

    private EditText mDeviceIdEt, mDeviceKeyEt;
    private Button mCheckButton;
    private String mDeviceID, mDeviceKey;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mDeviceIdEt.setText(settings.getString("ID", null));
        mDeviceKeyEt.setText(settings.getString("KEY", null));

    }

    private void initialize(){
        mDeviceIdEt = findViewById(R.id.device_id);
        mDeviceKeyEt = findViewById(R.id.device_key);
        mCheckButton = findViewById(R.id.check);

        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeviceID = mDeviceIdEt.getText().toString();
                mDeviceKey = mDeviceKeyEt.getText().toString();
                new UserDataAsyncTask().execute();
            }
        });
    }

    private void savePreferences(String deviceID, String deviceKey){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ID", deviceID).apply();
        editor.putString("KEY", deviceKey).apply();

        // Commit the edits!
        editor.commit();
    }

    class UserDataAsyncTask extends AsyncTask<URL,Void,String> {
        String BASE_URL="http://cerebrum.id/project/login_android.php";
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sedang Mencoba Masuk.");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            String serverResponse = "";
            URL url = null;
            try{
                url = createUrl(BASE_URL);
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            try{
                serverResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String strings) {
            Log.d("webrespondd", strings);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(strings.toLowerCase().equals("sukses")){
                savePreferences(mDeviceID,mDeviceKey);
                Intent tempIntent = new Intent(LoginActivity.this, MainActivity.class);
                tempIntent.putExtra("ID", mDeviceID);
                tempIntent.putExtra("KEY", mDeviceKey);
                startActivity(tempIntent);
            }else{
                Toast.makeText(getApplicationContext(),strings, Toast.LENGTH_SHORT).show();
            }

        }

        private String makeHttpRequest (URL add_url) throws IOException{
            String jsonResponse = "";
            try{
                URL url = add_url;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("device_id", "UTF-8")
                        + "=" + URLEncoder.encode(mDeviceID, "UTF-8");

                data += "&" + URLEncoder.encode("device_key", "UTF-8") + "="
                        + URLEncoder.encode(mDeviceKey, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.d("resp", jsonResponse);
                inputStream.close();
                httpURLConnection.disconnect();
                return jsonResponse;


            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        private URL createUrl(String url) throws MalformedURLException {
            return new URL(url);
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while(line != null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return  output.toString();
        }

    }

}
