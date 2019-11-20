package com.example.roombook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.example.roombook.classes.Reservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeReservationActivity extends Activity {

    TextView fromdateText;
    TextView fromtimeText;
    TextView totimeText;
    TextView todateText;
    EditText purposeText;
    LocalDate fromDate;
    LocalDate toDate;
    LocalTime fromtime;
    LocalTime totime;
    Button createResBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);
        purposeText = findViewById(R.id.purposeEditText);
        fromdateText = findViewById(R.id.fromDateTextView);
        fromtimeText = findViewById(R.id.fromTimeTextView);
        totimeText = findViewById(R.id.toTimeTextView);
        todateText = findViewById(R.id.toDateTextView);
        createResBtn = findViewById(R.id.createReservationBtn);


        Toolbar toolbar = findViewById(R.id.app_bar_make);
        setActionBar(toolbar);


        fromdateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fromDate = LocalDate.of(year, month + 1, dayOfMonth);
                        fromdateText.setText(fromDate.format((DateTimeFormatter.ISO_DATE)));
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(MakeReservationActivity.this, dateSetListener, currentYear, currentMonth, currentDayOfMonth);
                dialog.show();

            }
        });

        todateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        toDate = LocalDate.of(year, month + 1, dayOfMonth);
                        todateText.setText(toDate.format(DateTimeFormatter.ISO_DATE));
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(MakeReservationActivity.this, dateSetListener, currentYear, currentMonth, currentDayOfMonth);
                dialog.show();

            }
        });

        fromtimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        fromtime = LocalTime.of(hourOfDay, minute);
                        fromtimeText.setText(fromtime.getHour() + ":" + fromtime.getMinute());
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(MakeReservationActivity.this, timeSetListener, currentHourOfDay, currentMinute, true);
                dialog.show();
            }

        });

        totimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        totime = LocalTime.of(hourOfDay, minute);
                        totimeText.setText(totime.getHour() + ":" + totime.getMinute());
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(MakeReservationActivity.this, timeSetListener, currentHourOfDay, currentMinute, true);
                dialog.show();
            }

        });

        createResBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReservation();
            }
        });

    }





    private void postReservation(){

        Log.d("TAG", "Started creating post reservation");

        // toEpochsecon er en localtime metode der converter localtime til et nummer af sekunder siden 1970 bla bla
        long fromMilli = LocalDateTime.of(fromDate, fromtime).toEpochSecond(OffsetDateTime.now().getOffset());

        long toMilli = LocalDateTime.of(toDate, totime).toEpochSecond(OffsetDateTime.now().getOffset());

        int roomId = getIntent().getIntExtra("RoomId", -1);
        String purpose = purposeText.getText().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Reservation r = new Reservation(fromMilli, toMilli, userId, purpose, roomId);

        String jsonReservation = new Gson().toJson(r);
        Log.d("TAG", jsonReservation);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(jsonReservation, JSON);

        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations").post(requestBody).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (!response.isSuccessful())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), response.message(),Toast.LENGTH_SHORT).show();
                        }
                    });
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);



    }
}


















