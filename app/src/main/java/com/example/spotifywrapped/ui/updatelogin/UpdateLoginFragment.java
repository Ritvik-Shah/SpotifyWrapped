package com.example.spotifywrapped.ui.updatelogin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentLoginBinding;
import com.example.spotifywrapped.databinding.FragmentUpdateLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateLoginFragment extends Fragment {
    private FragmentUpdateLoginBinding binding;
    private EditText editTextNewEmail, editTextCurrentPassword, editTextNewPassword;
    private Button updateLoginButton;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Context context = getActivity();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
        binding = FragmentUpdateLoginBinding.inflate(inflater, container, false);
        editTextNewEmail = binding.newEmail;
        editTextNewPassword = binding.newPassword;
        editTextCurrentPassword = binding.currentPassword;
        updateLoginButton = binding.updateLoginButton;

        updateLoginButton.setOnClickListener((v) -> {
            String email = editTextNewEmail.getText().toString();
            String password = editTextNewPassword.getText().toString();
            String currentPassword = editTextCurrentPassword.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential) // Add reauthentication if necessary
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> reauthTask) {
                                if (reauthTask.isSuccessful()) {
                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "User Email updated.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Email update failed: " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getActivity(), "Reauthentication failed: " + reauthTask.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "User not logged in",
                        Toast.LENGTH_SHORT).show();
            }

            if (user != null) {
                // Reauthenticate user with current credentials
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> reauthTask) {
                                if (reauthTask.isSuccessful()) {
                                    // Reauthentication successful, proceed with password update
                                    user.updatePassword(password)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "Password updated successfully.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Password update failed: " + updateTask.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Reauthentication failed, handle the error
                                    Toast.makeText(getActivity(), "Reauthentication failed: " + reauthTask.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "Password update failed ",
                        Toast.LENGTH_SHORT).show();
            }
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
