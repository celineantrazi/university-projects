package com.example.rubysgoodies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SignupFragment extends Fragment {

    private EditText usernameSignup, passwordSignup;
    private Button btnSignup;
    private TextView tv2;
    private DBHandler dbHandler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        dbHandler = new DBHandler(requireContext());
        usernameSignup = view.findViewById(R.id.usernameSignup);
        passwordSignup = view.findViewById(R.id.passwordSignup);
        btnSignup = view.findViewById(R.id.btnSignup);
        tv2 = view.findViewById(R.id.tv2);
        btnSignup.setOnClickListener(e -> {
            String username = usernameSignup.getText().toString().trim();
            String password = passwordSignup.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHandler.checkUser(username, password)) {
                Toast.makeText(requireContext(), "Username exists. Try another.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                dbHandler.addNewUser(username, password);
                Toast.makeText(requireContext(), "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), MenuActivity.class);
                startActivity(i);
            }
        });
        tv2.setOnClickListener(e -> {
            Fragment loginFragment = new LoginFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, loginFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }
}
