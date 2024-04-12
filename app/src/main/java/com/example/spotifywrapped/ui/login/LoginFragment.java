package com.example.spotifywrapped.ui.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.content.SharedPreferences;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentLoginBinding;
import com.example.spotifywrapped.databinding.FragmentUpdateLoginBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.auth.LoginActivity;

public class LoginFragment extends Fragment {
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button loginButton, createLinkButton;

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Context context = getActivity();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        editTextUsername = binding.username;
        editTextEmail = binding.loginEmail;
        editTextPassword = binding.loginPassword;
        loginButton = binding.loginButton;
        createLinkButton = binding.createLinkButton;
        mAuth = FirebaseAuth.getInstance();

        createLinkButton.setOnClickListener((v) -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_createaccount);
        });

        loginButton.setOnClickListener((v) -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("is_logged_in", 1);
                            editor.apply();
                            Toast.makeText(getActivity(), "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            // Proceed to main activity or desired destination
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                            Activity activity = getActivity();
                            activity.finish();
                            activity.startActivity(activity.getIntent());

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });



        });

        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }






}
