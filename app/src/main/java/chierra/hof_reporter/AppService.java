package chierra.hof_reporter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Unknown on 5/4/2018.
 */

public class AppService extends Service {

    private String mJsonData = "";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();

        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startAsync();
                refreshHandler.postDelayed(this, 2000);
                Log.d("ulang", "ulangulang");
            }
        };
        refreshHandler.postDelayed(runnable,1000);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void startAsync(){
        new DetectAsyncTask().execute();
    }

    class DetectAsyncTask extends AsyncTask<URL,Void,String> {
        String BASE_URL="http://cerebrum.id/project/last_detection.php";
        @Override
        protected String doInBackground(URL... urls) {
            String jsonString = "";
            URL url = null;
            try{
                url = createUrl(BASE_URL);
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            try{
                jsonString = makeHttpRequest(url);
                mJsonData = jsonString;
                Log.d("haha", jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String strings) {
            DetectData detectData = getJsonData();
            if(detectData!=null){
                if(detectData.getmStatus().toLowerCase().equals("ada api")){
                    fireNotification(detectData.getmTime());
                }else{
                }
            }
        }

        private String makeHttpRequest (URL url) throws IOException{
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try{
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }catch (IOException e){

            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if(inputStream != null){
                    inputStream.close();
                }
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

    private DetectData getJsonData(){
        DetectData data = null;

        try {
            JSONObject root = new JSONObject(mJsonData);
            Log.d("haha2323",mJsonData);
            //JSONArray features = root.getJSONArray("daftar");
            //JSONObject head = root.getJSONObject("");
            JSONArray object = root.getJSONArray("data");
            for(int i=0 ; i<object.length() ; i++){
                //JSONObject properties = hutan.getJSONObject(i).getJSONObject("properties");
                JSONObject properties = object.getJSONObject(i);
                data= new DetectData(properties.getString("value"),properties.getString("image"),properties.getString("time"));
                Log.d("values",properties.getString("value"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void fireNotification(String time){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.fire)
                        .setContentTitle("TERDETEKSI API")
                        .setContentText(time);
        builder.setVibrate(new long []{500,500});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
