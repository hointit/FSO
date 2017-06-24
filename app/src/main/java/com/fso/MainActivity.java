package com.fso;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    //data Firebase
    private FirebaseAuth mAuth;



    Button btnLogin;
    EditText email;
    EditText pass;
    TextView navHeaderName;
    TextView navHeaderEmail;
    FirebaseUser currentUser;
    private DatabaseReference mData;
    ListView lvLocation;
    public ArrayList<Location> dataList;
    Geocoder geoCoder;
    String API_KEY = "AIzaSyCGFI199a3yWLyfv2ELzRkt4gpQp-ZE6Ms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            setMainActivity();
        }
        else {
            setLoginActivity();
        }

        geoCoder = new Geocoder(this, Locale.getDefault());
    }


    // Hàm set lại về giao diện chính
    public void setMainActivity( ){
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        navHeaderName = (TextView)header.findViewById(R.id.text_name);
        navHeaderEmail = (TextView)header.findViewById(R.id.text_email);

        navHeaderName.setText(currentUser.getDisplayName());
        navHeaderEmail.setText(currentUser.getEmail());
        lvLocation = (ListView)findViewById(R.id.list_locationData);
        // CHOICE_MODE_NONE: Không cho phép lựa chọn (Mặc định).
        // ( listView.setItemChecked(..) không làm việc với CHOICE_MODE_NONE).
        // CHOICE_MODE_SINGLE: Cho phép một lựa chọn.
        // CHOICE_MODE_MULTIPLE: Cho phép nhiều lựa chọn.
        // CHOICE_MODE_MULTIPLE_MODAL: Cho phép nhiều lựa chọn trên Modal Selection Mode.
        lvLocation.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();


            }
        });
        SimpleDateFormat fDate = new SimpleDateFormat("yyyyMMdd");
        String sDate = fDate.format(new Date());
        getDataLocation(sDate);

    }


    // Nếu chưa login hoặc mới logout
    private void setLoginActivity(){
        setContentView(R.layout.activity_login);
        btnLogin = (Button)findViewById(R.id.btn_login);
        email = (EditText)findViewById(R.id.input_email);
        pass = (EditText)findViewById(R.id.input_password);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),pass.getText().toString());
            }
        });
    }


    // Lấy dũ liệu từ firebase
    public void getDataLocation(String sDate){
        dataList = new ArrayList<Location>();
        final CustomListAdapter cLv = new CustomListAdapter(this, dataList);
        lvLocation.setAdapter(cLv);

        getSupportActionBar().setTitle("Dữ liệu ngày: " + sDate);
        //Date a = new Date(sDate);
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("teouit").child("LocationData").child(sDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LocationData ld = dataSnapshot.getValue(LocationData.class);

                //Chuyển đổi địa chỉ
                List<Address> addresses = null;
                String addressText = "";
                try {
                    while (addresses==null){
                        addresses = geoCoder.getFromLocation(Double.parseDouble(ld.Latitude),Double.parseDouble(ld.Longitude), 1);
                    }
                    String mAddress = "";
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        for (int i = 0; i < 4; i ++) {
                            mAddress += address.getAddressLine(i) + " ";

                        }
                        Location mLoc = new Location(mAddress, ld.Time, "", ld.Latitude, ld.Longitude);
                        dataList.add(mLoc);
                        cLv.updateResults(dataList);
                        // Lấy hình ảnh
                        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + ld.Latitude + "," + ld.Longitude + "&radius=400&type=restaurant&keyword=cruise&key=" + API_KEY;
                        //new GetClass(getApplicationContext(), url, cLv, mAddress, ld.Time).execute();
                    }
                    else {
                        Location mLoc = new Location("Tọa độ:  " + ld.Latitude + ", " + ld.Longitude, ld.Time, "", ld.Latitude, ld.Longitude);
                        dataList.add(mLoc);
                        cLv.updateResults(dataList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    //Hoi Xử lý get link hình ảnh
    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        private String url;
        private final CustomListAdapter cLv;
        private String  mAddress;
        private String mTime;

        // Hàm khởi tạo
        public GetClass(Context context, String url, CustomListAdapter cLv, String mAddress, String Time) {
            this.context = context;
            this.url = url;
            this.cLv = cLv;
            this.mAddress = mAddress;
            this.mTime = Time;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                //final TextView outputView = (TextView) findViewById(R.id.showOutput);
                URL url = new URL(this.url);
                final String timeUpdate = this.mTime;
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                final StringBuilder output = new StringBuilder();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(responseOutput.toString());

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(output.toString());
                            Log.d("status: ", obj.getString("status"));
                            if(!obj.getString("status").equals("OK"))
                            {
                                Location mLoc = new Location(mAddress, timeUpdate, "");
                                dataList.add(mLoc);
                                cLv.updateResults(dataList);
                                return;
                            }
                            String photoReference = obj.getJSONArray("results").getJSONObject(0).getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                            String urlImage = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=" + photoReference + "&key=" + API_KEY;

                            Location mLoc = new Location(mAddress, timeUpdate, urlImage);
                            dataList.add(mLoc);
                            cLv.updateResults(dataList);
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + output + "\"");
                        }
                    }
                });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Đăng nhập firebase
    private void signIn(String email, String password) {
        if(email.equals("") || password.equals("")) {
            Toast.makeText(MainActivity.this, "Email mà mật khẩu không được để trống!!!",Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("Đang đăng nhập........");
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    currentUser = mAuth.getCurrentUser();
                    setMainActivity();
                } else {
                    Toast.makeText(MainActivity.this, "Đăng nhập thất bại, kiểm tra email và mật khẩu.",Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
        // [END sign_in_with_email]
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // Xử lý click actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_datepicker) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // xử lý navigation bar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_open_map) {
            if(dataList.size() == 0){
                Toast.makeText(this, "Không có dữ liệu.", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent googleMap = new Intent(getApplicationContext(), GoogleMaps.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data", dataList);
            googleMap.putExtras(bundle);


            startActivity(googleMap);

        } else if (id == R.id.nav_open_map_last) {
            Intent googleMapLastKnow = new Intent(getApplicationContext(), GoogleMaps.class);
            ArrayList<Location> lastKnow = new ArrayList<Location>();
            if(dataList.size() > 0)
                lastKnow.add(dataList.get(dataList.size()- 1));
            else {
                Toast.makeText(this, "Không có dữ liệu.", Toast.LENGTH_SHORT).show();
                return false;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data", lastKnow);
            googleMapLastKnow.putExtras(bundle);
            startActivity(googleMapLastKnow);

        } else if (id == R.id.nav_open_map_checked) {
            ArrayList<Location> tempData = new ArrayList<Location>();
            Intent googleMapLastKnow = new Intent(getApplicationContext(), GoogleMaps.class);
            for (int i = 0; i < lvLocation.getChildCount(); i++){
                View v = lvLocation.getChildAt(i);
                CheckBox chk=(CheckBox) v.findViewById(R.id.chkitem);
                if(chk.isChecked())
                {
                    tempData.add(dataList.get(i));
                }
            }
            if(tempData.size() == 0){
                Toast.makeText(this, "Không có dữ liệu.", Toast.LENGTH_SHORT).show();
                return false;
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data", tempData);
            googleMapLastKnow.putExtras(bundle);
            startActivity(googleMapLastKnow);
        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            setLoginActivity();
        }else if (id == R.id.nav_signoutAndExit) {
            FirebaseAuth.getInstance().signOut();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        else if (id == R.id.nav_exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        return false;
    }
}
