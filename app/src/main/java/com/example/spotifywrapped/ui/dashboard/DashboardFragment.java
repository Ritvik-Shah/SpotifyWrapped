package com.example.spotifywrapped.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Button;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import com.example.spotifywrapped.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Find the deleteButton in the layout
        int deleteButtonId = getResources().getIdentifier("deleteButton", "id", getActivity().getPackageName());
        Button deleteButton = root.findViewById(deleteButtonId);

        // Set OnClickListener for the deleteButton
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current user's ID
                String userId = mAuth.getCurrentUser().getUid();

                // Get reference to the user's account document
                DocumentReference accountRef = db.collection("users").document(userId).collection("Account").document("userInfo");

                // Delete fields within the user's account document
                accountRef.update(
                                "api_key", null,
                                "email", null,
                                "id", null,
                                "password", null
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // User data deleted successfully
                                Toast.makeText(getContext(), "User data deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete user data
                                Toast.makeText(getContext(), "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
