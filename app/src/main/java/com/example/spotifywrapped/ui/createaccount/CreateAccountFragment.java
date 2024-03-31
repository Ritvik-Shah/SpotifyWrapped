package com.example.spotifywrapped.ui.createaccount;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.databinding.FragmentCreateaccountBinding;
import com.example.spotifywrapped.databinding.FragmentDashboardBinding;
import com.example.spotifywrapped.ui.dashboard.DashboardViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class CreateAccountFragment extends Fragment{
    private FragmentCreateaccountBinding binding;
    private FirebaseFirestore db;
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonCreateAccount;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        db = FirebaseFirestore.getInstance();
        binding = FragmentCreateaccountBinding.inflate(inflater, container, false);

        editTextUsername = binding.editTextUsername;
        editTextEmail = binding.editTextEmail;
        editTextPassword = binding.editTextPassword;
        buttonCreateAccount = binding.buttonCreateAccount;

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        View root = binding.getRoot();
        return root;
    }

    private void createAccount() {
        CollectionReference dbUsers = db.collection("Users");

        // adding our data to our courses object class.
        User courses = new User(editTextEmail, editTextUsername, editTextPassword);

        // below method is use to add data to Firebase Firestore.
        dbUsers.add(courses).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(requireContext(), "Account Created!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

