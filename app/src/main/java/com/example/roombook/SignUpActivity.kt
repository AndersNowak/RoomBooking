package com.example.roombook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {


    private var mAuth: FirebaseAuth? = null

    private lateinit var signupActivityBtn: Button
    private var emailSignup: EditText? = null
    private var passwordSignup: EditText? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        emailSignup = findViewById(R.id.emailSignupText)
        passwordSignup = findViewById(R.id.passwordSignupText)
        signupActivityBtn = findViewById(R.id.signupActivityBtn)

        signupActivityBtn.setOnClickListener {
            val email = emailSignup!!.text.toString()
            val password = passwordSignup!!.text.toString()

            if (email.isEmpty()) {
                emailSignup!!.error = "Please enter email"
                emailSignup!!.requestFocus()
            } else if (password.isEmpty()) {
                passwordSignup!!.error = "Please enter password"
            } else if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Fields are empty", Toast.LENGTH_SHORT).show()

            } else if (!(email.isEmpty() && !password.isEmpty())) {
                mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@SignUpActivity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "Signup unsuccesful", Toast.LENGTH_SHORT).show()

                    } else {
                        startActivity(Intent(this@SignUpActivity, UserActivity::class.java))

                    }
                }

            }
        }
    }
}





