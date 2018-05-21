package chierra.hof_reporter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "USER_DATA";

    private ActionBar toolbar;
    private Bundle mUserBundle, mDetectionBundle;
    private String device_id, device_key;

    Handler handler = new Handler();
    private Runnable refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        device_id = settings.getString("ID", null);
        device_key = settings.getString("KEY", null);

        mUserBundle = new Bundle();
        mUserBundle.putString("ID", device_id);
        mUserBundle.putString("KEY", device_key);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    toolbar.setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_setting:
                    fragment = new HistoryFragment();
                    fragment.setArguments(mUserBundle);
                    loadFragment(fragment);
                    toolbar.setTitle(R.string.title_history);
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    fragment.setArguments(mUserBundle);
                    loadFragment(fragment);
                    toolbar.setTitle(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    public void onBackPressed() {
        finish();
    }


}
