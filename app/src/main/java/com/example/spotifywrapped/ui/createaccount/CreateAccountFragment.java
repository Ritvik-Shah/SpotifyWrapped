package com.example.spotifywrapped.ui.createaccount;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.User;
import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.databinding.FragmentCreateaccountBinding;
import com.example.spotifywrapped.databinding.FragmentDashboardBinding;
import com.example.spotifywrapped.ui.dashboard.DashboardViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class CreateAccountFragment extends Fragment{
    private FragmentCreateaccountBinding binding;
    private FirebaseFirestore db;
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private String authCode;
    private Button buttonCreateAccount, buttonLinkSpotify;
    private boolean isSpotifyLinked = false;
    public static final String REDIRECT_URI = "spotify-sdk://auth";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    public static final String CLIENT_ID = "7e2ace9bc6e942d394cc8c9c71d0acd9";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    boolean authSuccess = false;
    private String apiKey;
    private int counter = 0;
    private FirebaseAuth mAuth;
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
        buttonLinkSpotify = binding.buttonLinkSpotify;
        counter = 0;
        mAuth = FirebaseAuth.getInstance();

        /*Button tokenBtn = (Button) binding.tokenBtn;
        Button codeBtn = (Button) binding.codeBtn;
        Button profileBtn = (Button) binding.profileBtn;*/

        // Set the click listeners for the buttons

        /*tokenBtn.setOnClickListener((v) -> {
            getToken();
        });

        codeBtn.setOnClickListener((v) -> {
            getCode();
        });

        profileBtn.setOnClickListener((v) -> {
            mAccessToken = ((MainActivity) requireActivity()).getmAccessToken();
            mAccessCode = ((MainActivity) requireActivity()).getmAccessCode();
            onGetUserProfileClicked();
        });


        });*/
        buttonLinkSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (counter == 0) {
                    getToken();
                    counter++;
                } else if (counter == 1) {
                    getAuthCode();
                    counter++;
                } else {
                    onGetUserProfileClicked();
                    counter = 0;
                }*/

                getToken();
                /*getCode();
                onGetUserProfileClicked();*/
                //SystemClock.sleep(1000);
                mAccessToken = ((MainActivity) requireActivity()).getmAccessToken();
                //mAccessCode = ((MainActivity) requireActivity()).getmAccessCode();

                //Toast.makeText(requireContext(), mAccessToken, Toast.LENGTH_SHORT).show();

                isSpotifyLinked = true;
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpotifyLinked) {
                    mAccessToken = ((MainActivity) requireActivity()).getmAccessToken();
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

        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        // adding our data to our user object class.
        User user = new User(mAccessToken, email, username, password);

        // below method is use to add data to Firebase Firestore.

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = task.getResult().getUser();
                            String userID = currentUser.getUid();
                            // Now you have the userID, you can use it as needed
                            Log.d("UserID", "User ID: " + userID);
                            CollectionReference dbUsers = db.collection("Users");
                            dbUsers.document(userID).set(user);
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                            navController.navigate(R.id.navigation_login);
                            authSuccess = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            /*Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });

        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();
            // Now you have the userID, you can use it as needed
        Log.d("UserID", "User ID: " + userID);
        CollectionReference dbUsers = db.collection("Users");
        dbUsers.document(userID).set(user);
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_login);*/

        /* dbUsers.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.

                if (authSuccess == true){
                    Toast.makeText(requireContext(), "Account Created!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.navigation_login);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        }); */
    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_TOKEN_REQUEST_CODE, request);
        if (getActivity() == null) {
            Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            Toast.makeText(requireContext(), mAccessToken, Toast.LENGTH_LONG);
            //setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            Toast.makeText(requireContext(), mAccessCode, Toast.LENGTH_LONG);
            //setTextAsync(mAccessCode, codeTextView);
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(requireContext(), "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(requireContext(), "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    //setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(requireContext(), "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(() -> textView.setText(text));
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
                .setScopes(new String[] { "user-read-email", "user-top-read "}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

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

    @Override
    public void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}
