package chierra.hof_reporter;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    private RecyclerView mRecylerViewHistory;
    private String mJsonData;
    private HistoryAdapter mAdapter;
    private TextView mEmptyView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ID";
    private static final String ARG_PARAM2 = "KEY";
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mRecylerViewHistory = view.findViewById(R.id.rv_history);
        mEmptyView = view.findViewById(R.id.empty_view);
        mRecylerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        startAsync();

        return view;
    }


    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        String BASE_URL="https://www.cerebrum.id/project/get_wildfire_record.php";
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
            List<DetectData> detectData = getJsonData(strings);
            mAdapter = new HistoryAdapter(detectData, getActivity(), new OnClickListener() {
                @Override
                public void onItemClick(DetectData item) {

                }
            });

            if(mAdapter.getItemCount()==0){
                mEmptyView.setVisibility(View.VISIBLE);
                mRecylerViewHistory.setVisibility(View.GONE);
            }else{
                mRecylerViewHistory.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mRecylerViewHistory.setAdapter(mAdapter);
            }

        }

        private String makeHttpRequest (URL url) throws IOException{
            String jsonResponse = "";
            try{
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("device_id", "UTF-8")
                        + "=" + URLEncoder.encode(device_id, "UTF-8");


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
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

    private List<DetectData> getJsonData(String src){
        List<DetectData> data = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(src);
            //JSONArray features = root.getJSONArray("daftar");
            //JSONObject head = root.getJSONObject("");
            JSONArray object = root.getJSONArray("data");
            for(int i=0 ; i<object.length() ; i++){
                //JSONObject properties = hutan.getJSONObject(i).getJSONObject("properties");
                JSONObject properties = object.getJSONObject(i);
                data.add(new DetectData(properties.getString("value"),properties.getString("image"),properties.getString("time")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}
