package com.example.rubysgoodies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private Button signup;
    private Button insta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.btnSignup);
        insta = findViewById(R.id.btnInsta);
        login.setOnClickListener(e ->
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();

        });
        signup.setOnClickListener(e ->
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SignupFragment())
                    .commit();

        });
        insta.setOnClickListener(e ->
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.instagram.com/rubys.goodies/"));
            startActivity(i);
        });
    }
}