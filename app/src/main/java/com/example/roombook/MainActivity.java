package com.example.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "GESTURES";

    private GestureDetector gestureDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this, this);

    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {

        return gestureDetector.onTouchEvent(event);

    }




    @Override

    public boolean onDown(MotionEvent e) {

        Log.d(TAG, "onDown");

        return true;

    }



    @Override

    public void onShowPress(MotionEvent e) {

        Log.d(TAG, "onShowPress");

    }



    @Override

    public boolean onSingleTapUp(MotionEvent e) {

        Log.d(TAG, "onSingleTapUp");

        return true; // done

    }



    @Override

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d(TAG, "onScroll");

        return true;

    }



    @Override

    public void onLongPress(MotionEvent e) {

        Log.d(TAG, "onLongPress");

    }



    @Override

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        Log.d(TAG, "onFling " + e1.toString() + "::::" + e2.toString());

        boolean leftSwipe = e1.getX() > e2.getX();
        boolean rightSwipe = e1.getX() < e2.getX();

        Log.d(TAG, "onFling left: " + leftSwipe);

        if (leftSwipe || rightSwipe) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }


        return true; // done

    }


    public void buttonClicked(View view) {

        Log.d(TAG, "button clicked");

    }
}
