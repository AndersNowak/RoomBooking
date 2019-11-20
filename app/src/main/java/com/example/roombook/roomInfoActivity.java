package com.example.roombook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class roomInfoActivity extends Activity {

    ArrayList<Reservation> reservations = new ArrayList<>();
    Room room;
    Button makeResBtn;
    ListView infoList;
    SwipeRefreshLayout sr;


    ArrayAdapter<Reservation> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_room_info);


        Toolbar toolbar = findViewById(R.id.app_bar);
        setActionBar(toolbar);

        room = (Room) getIntent().getSerializableExtra("ROOM");
        makeResBtn = findViewById(R.id.makeReservationBtn);
        infoList = findViewById(R.id.infoListView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservations);

        infoList.setAdapter(arrayAdapter);

        makeResBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MakeReservationActivity.class);
                intent.putExtra("RoomId", room.getId());
                startActivity(intent);
            }
        });

        getReservations(room.getId());

        sr = findViewById(R.id.refreshSwipe);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReservations(room.getId());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "logged off", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(roomInfoActivity.this, MainActivity.class));
                break;
            case R.id.roomactivity:
                Log.d("TAG", "switching to room activity");
                startActivity(new Intent(roomInfoActivity.this, RoomActivity.class));
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





    private void getReservations(int roomId) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/"+ roomId).build();
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
                    Reservation[] Reserve = gson.fromJson((response.body()).string(), Reservation[].class);
                    reservations.clear();
                    reservations.addAll(Arrays.asList(Reserve));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
                sr.setRefreshing(false);
            }
        });
    }
}

