package com.example.roombook;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

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

public class RoomActivity extends Activity {

    ArrayList<Room> rooms = new ArrayList<>();
    ArrayAdapter<Room> roomArrayAdapter;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setActionBar(toolbar);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Log.d("TAG", "onCreate roomActivity");
        roomArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rooms);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent itemIntent = new Intent(getBaseContext(), roomInfoActivity.class);
                Room room = (Room) parent.getItemAtPosition(position);
                itemIntent.putExtra("ROOM", room);
                startActivity(itemIntent);
            }
        };

        ListView listView = findViewById(R.id.roomListView);

        listView.setAdapter(roomArrayAdapter);

        listView.setOnItemClickListener(itemClickListener);

        getRooms();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "logged off", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(RoomActivity.this, MainActivity.class));
                break;
            case R.id.roomactivity:
                Log.d("TAG", "switching to room activity");
                startActivity(new Intent(RoomActivity.this, RoomActivity.class));
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

    private void getRooms() {
        Log.d("TAG", "Getting room from rest");
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Rooms").build();
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
                    Room[] Rooms = gson.fromJson((response.body()).string(), Room[].class);
                    Log.d("TAG", Rooms.toString());
                    rooms.addAll(Arrays.asList(Rooms));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomArrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }}













