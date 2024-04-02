package com.example.spotifywrapped.ui.createaccount;
import android.content.Intent;
import android.net.Uri;
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
import com.example.spotifywrapped.User;
import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.databinding.FragmentCreateaccountBinding;
import com.example.spotifywrapped.databinding.FragmentDashboardBinding;
import com.example.spotifywrapped.ui.dashboard.DashboardViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.OkHttpClient;
import okhttp3.Call;

public class CreateAccountFragment extends Fragment{
    private FragmentCreateaccountBinding binding;
    private FirebaseFirestore db;
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private String authCode;
    private Button buttonCreateAccount, buttonLinkSpotify;
    private boolean isSpotifyLinked = false;
    public static final String REDIRECT_URI = "SPOTIFY-SDK://auth";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    public static final String CLIENT_ID = "7e2ace9bc6e942d394cc8c9c71d0acd9";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String apiKey;
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

        buttonLinkSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthCode();
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpotifyLinked) {
                    createAccount();
                } else {
                    Toast.makeText(requireContext(), "Please link your Spotify account first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View root = binding.getRoot();
        return root;
    }

    private void createAccount() {
        CollectionReference dbUsers = db.collection("Users");

        // adding our data to our courses object class.
        User courses = new User(authCode, editTextEmail.toString(), editTextUsername.toString(), editTextPassword.toString());

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

    public void getAuthCode() {

        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_CODE_REQUEST_CODE, request);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            authCode = mAccessCode;
        }
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
    }

