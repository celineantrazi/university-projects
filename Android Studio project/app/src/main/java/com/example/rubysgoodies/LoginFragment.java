package com.example.rubysgoodies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment {

    private EditText usernameLogin, passwordLogin;
    private Button btnLogin;
    private TextView tv1;
    private DBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        dbHandler = new DBHandler(requireContext());
        usernameLogin = view.findViewById(R.id.usernameLogin);
        passwordLogin = view.findViewById(R.id.passwordLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        tv1 = view.findViewById(R.id.tv1);
        btnLogin.setOnClickListener(v -> {
            String username = usernameLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHandler.checkUser(username, password)) {
                Toast.makeText(requireContext(), "Welcome back " + username + "!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(requireContext(), MenuActivity.class);
                startActivity(i);
            } else
                Toast.makeText(requireContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();

        });
        tv1.setOnClickListener(v -> {
            Fragment signupFragment = new SignupFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, signupFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }
}


