package chierra.hof_reporter;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mLastUpdated;
    private ImageView mFireIcon;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mJsonData = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mFireIcon = view.findViewById(R.id.fire_symbol);
        mLastUpdated = view.findViewById(R.id.last_update_time);

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

        return view;
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
                mLastUpdated.setText(detectData.getmTime());
                if(detectData.getmStatus().toLowerCase().equals("ada api")){
                    mFireIcon.setImageResource(R.drawable.fire);
                    fireNotification();
                }else{
                    mFireIcon.setImageResource(R.drawable.no_fire);
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

    private void fireNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.fire)
                        .setContentTitle("DIRUMAH ANDA TERDETEKSI API");
        builder.setVibrate(new long []{500,500});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
