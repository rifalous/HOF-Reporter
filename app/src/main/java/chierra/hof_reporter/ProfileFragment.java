package chierra.hof_reporter;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ID";
    private static final String ARG_PARAM2 = "KEY";

    private Button mProfileSetting;
    private TextView mUserIdTv, mUserNameTv, mUserAddressTv, mUserPhoneTv;
    private String mJsonData = "";
    private String device_id, device_key;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            device_id = getArguments().getString(ARG_PARAM1);
            device_key = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserIdTv = view.findViewById(R.id.user_id);
        mUserNameTv = view.findViewById(R.id.user_name);
        mUserAddressTv = view.findViewById(R.id.user_address);
        mUserPhoneTv = view.findViewById(R.id.user_phone);

        mProfileSetting = view.findViewById(R.id.edit_profile);
        mProfileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),DataFormActivity.class);
                startActivity(intent);
            }
        });

        startAsync();

        return view;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void startAsync(){
        new UserDataAsyncTask().execute();
    }


    class UserDataAsyncTask extends AsyncTask<URL,Void,String> {
        String BASE_URL="http://cerebrum.id/project/get_user_data.php";
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
                Log.d("haha2", jsonString);
                mJsonData = jsonString;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String strings) {
            ArrayList<UserProfile> User = getJsonData();
            int size = 0;
            mUserIdTv.setText(User.get(size).getmDeviceId());
            mUserNameTv.setText(User.get(size).getmName());
            mUserAddressTv.setText(User.get(size).getmAddress());
            mUserPhoneTv.setText(User.get(size).getmPhone());
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

                String data = URLEncoder.encode("device_key", "UTF-8")
                        + "=" + URLEncoder.encode(device_key, "UTF-8");


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

    private ArrayList<UserProfile> getJsonData(){
        ArrayList<UserProfile> news = new ArrayList<>();

        try {
            JSONArray root = new JSONArray(mJsonData);
            //JSONArray features = root.getJSONArray("daftar");
            //JSONObject head = root.getJSONObject("");
            //JSONObject object = head.getJSONObject("data");
            for(int i=0 ; i<root.length() ; i++){
                //JSONObject properties = hutan.getJSONObject(i).getJSONObject("properties");
                JSONObject properties = root.getJSONObject(i);
                news.add(new UserProfile(properties.getString("device_id"),
                        properties.getString("name"),
                        properties.getString("address"),
                        properties.getString("phone")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return news;
    }


}
