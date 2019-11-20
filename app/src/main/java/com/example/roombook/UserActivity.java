package com.example.roombook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.roombook.classes.Reservation;
import com.example.roombook.classes.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UserActivity extends Activity {




    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ArrayList<Reservation> myReservations = new ArrayList<>();
    Room room;
    ListView myResListView;
    ArrayAdapter<Reservation> arrayAdapter;
    SwipeRefreshLayout sr;
    TextView userInfoView;
    int selectedItem = -1;
    View selectedView;
    Button delBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_user);

        myResListView = findViewById(R.id.myreservationListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myReservations);
        myResListView.setAdapter(arrayAdapter);
        userInfoView = findViewById(R.id.userInformationView);

        delBtn = findViewById(R.id.deleteBtn);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReservation();
            }
        });

        Toolbar toolbar = findViewById(R.id.app_bar);
        setActionBar(toolbar);

        Log.d("TAG", "user on create");

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == selectedItem) {
                    view.setBackgroundColor(Color.WHITE);
                    selectedItem = -1;
                    selectedView = null;
                }
                else {
                    if(selectedView != null)
                        selectedView.setBackgroundColor(Color.WHITE);

                    view.setBackgroundColor(Color.GRAY);
                    selectedItem = position;
                    selectedView = view;
                }
            }
        };

        myResListView.setOnItemClickListener(clickListener);

        showMyReservations(FirebaseAuth.getInstance().getCurrentUser().getUid());

        sr = findViewById(R.id.refreshSwipe);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showMyReservations(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "logged off", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(UserActivity.this, MainActivity.class));
            break;
            case R.id.roomactivity:
                Log.d("TAG", "switching to room activity");
                startActivity(new Intent(UserActivity.this, RoomActivity.class));
            break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showMyReservations(String userId) {
        OkHttpClient client = new OkHttpClient();
        Log.d("TAG", userId);
        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/user/" + userId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code" + response);
                else {

                    Gson gson = new Gson();
                    Reservation[] Reserve = gson.fromJson(response.body().string(), Reservation[].class);
                    Log.d("TAG", "Number of reservations received: " + Reserve.length);
                    myReservations.clear();
                    myReservations.addAll(Arrays.asList(Reserve));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG", "run bitch");
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
                sr.setRefreshing(false);
            }
        });
    }

    private void deleteReservation(){

        if (selectedItem < 0)
            return;
        OkHttpClient client = new OkHttpClient();
        Reservation res = myReservations.get(selectedItem);

        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/" + res.getId()).delete().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserActivity.this, "Reservation could not be deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myReservations.remove(selectedItem);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


}
