package com.example.spotifywrapped.ui.home;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.User;
import com.example.spotifywrapped.databinding.FragmentHomeBinding;
import com.example.spotifywrapped.ui.CardAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private CardAdapter adapter;

    private FragmentHomeBinding binding;
    private String authCode;
    public static final String REDIRECT_URI = "spotify-sdk://auth";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private final OkHttpClient mOkHttpClient2 = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall, mCall2;
    public static final String CLIENT_ID = "7e2ace9bc6e942d394cc8c9c71d0acd9";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    //private String apiKey;
    //private int counter = 0;
    //private FirebaseAuth mAuth;
    private List<String> myList = new ArrayList<>();
    private TextView profileTextView, secondTextView;
    private FirebaseFirestore db;
    private User user;
    private String artistId = new String();
    private List<JSONObject> artists = new ArrayList<>();

    private List<String> cards = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        profileTextView = binding.textHome;
        secondTextView = binding.secondTextView;
        viewPager = binding.viewPager;
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button recommendedArtists = binding.recommendButton;
        recommendedArtists.setVisibility(View.VISIBLE);
        // Set OnClickListener for the button
        DocumentReference docRef = db.collection("Users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mAccessToken = (String) document.get("apiKey");
                    } else {
                        Toast.makeText(requireContext(), "no such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "get failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recommendedArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendedArtists.setVisibility(View.INVISIBLE);
                //Toast.makeText(requireContext(), mAccessToken, Toast.LENGTH_SHORT).show();
                onGetUserProfileClicked();
                //getTracks();
            }
        });

        Window window = requireActivity().getWindow();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);


// Set background color based on the date
        if (month == 10 && day == 31) {
            // October 31st - Orange
            //sendNotification("Halloween Edition", "Check out the new colors for Halloween!!!");
            window.setStatusBarColor(getResources().getColor(R.color.black));
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.orange)));
            }
        } else if (month == 12 && day == 25) {
            // December 25th - Red
            window.setStatusBarColor(getResources().getColor(R.color.green));
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.red)));
            }
        } else if (month == 1 && day == 1) {
            // January 1st - Gold
            window.setStatusBarColor(getResources().getColor(R.color.black));
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gold)));
            }
        } else if (month == 7 && day == 4) {
            // July 4th - Blue
            window.setStatusBarColor(getResources().getColor(R.color.blue));
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.blue)));
            }
        } else {
            // Default background color
            window.setStatusBarColor(getResources().getColor(R.color.black));
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_700)));
            }
        }

        adapter = new CardAdapter(cards);
        viewPager.setAdapter(adapter);



        return root;
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
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {

        if (mAccessToken == null) {
            Toast.makeText(requireContext(), "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user's top artists
        final Request artistsRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        // Create a request to get the user's top tracks
        final Request tracksRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();


        // Cancel any ongoing calls
        cancelCall();

        // Enqueue the request to fetch top artists
        mCall = mOkHttpClient.newCall(artistsRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch artists data: " + e);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to fetch artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        JSONArray itemsArray = new JSONObject(response.body().string()).getJSONArray("items");
                        // Extract top artists' names
                        // Loop through the items and extract artist names
                        int count = Math.min(itemsArray.length(), 3);
                        StringBuilder topArtists = new StringBuilder();
                        topArtists.append("Top Artists\n\n");
                        for (int i = 0; i < count; i++) {
                            JSONObject artistObject = itemsArray.getJSONObject(i);
                            String artistName = artistObject.getString("name");
                            topArtists.append((i + 1)).append(". ").append(artistName).append("\n");
                            artists.add(artistObject);
                        }
                        artistId = artists.get(0).getString("id");

                        // Update the UI with the top artists
                        getActivity().runOnUiThread(() -> {
                            //profileTextView.setText(topArtists.toString());
                            adapter.addItem(topArtists.toString());
                        });

                        final Request relatedRequest = new Request.Builder()
                                .url("https://api.spotify.com/v1/artists/" + artistId + "/related-artists")
                                .addHeader("Authorization", "Bearer " + mAccessToken)
                                .build();

                        mCall = mOkHttpClient.newCall(relatedRequest);
                        mCall.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("HTTP", "Failed to fetch related artist data: " + e);
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Failed to fetch related artist data, watch Logcat for more details",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    if (response.isSuccessful()) {
                                        JSONArray itemsArray = new JSONObject(response.body().string()).getJSONArray("artists");
                                        // Extract top tracks' names
                                        // Loop through the items and extract track names
                                        int count = Math.min(itemsArray.length(), 3);
                                        StringBuilder topTracks = new StringBuilder();
                                        topTracks.append("Recommended Artists\n\n");
                                        for (int i = 0; i < count; i++) {
                                            JSONObject trackObject = itemsArray.getJSONObject(i);
                                            String trackName = trackObject.getString("name");
                                            topTracks.append((i + 1)).append(". ").append(trackName).append("\n");
                                        }

                                        // Update the UI with the top tracks
                                        getActivity().runOnUiThread(() -> {
                                            // Append the top tracks to the existing text
                                            //profileTextView.append(topTracks.toString());
                                            adapter.addItem(topTracks.toString());
                                        });
                                    } else {
                                        Log.d("HTTP", "Failed to fetch related artists data: " + response.code());
                                        getActivity().runOnUiThread(() -> {
                                            Toast.makeText(requireContext(), "Failed to fetch related artist data, watch Logcat for more details",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } catch (JSONException e) {
                                    Log.d("JSON", "Failed to parse related artists data: " + e);
                                    Toast.makeText(requireContext(), "Failed to parse tracks data, watch Logcat for more details",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("HTTP", "Failed to fetch artists data: " + response.code());
                        Toast.makeText(requireContext(), "Failed to fetch artists data: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse artists data: " + e);
                    Toast.makeText(requireContext(), "Failed to parse artists data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Enqueue the request to fetch top tracks
        mCall = mOkHttpClient.newCall(tracksRequest);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch tracks data: " + e);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to fetch tracks data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        JSONArray itemsArray = new JSONObject(response.body().string()).getJSONArray("items");
                        // Extract top tracks' names
                        // Loop through the items and extract track names
                        int count = Math.min(itemsArray.length(), 3);
                        StringBuilder topTracks = new StringBuilder();
                        topTracks.append("Top Tracks\n\n");
                        for (int i = 0; i < count; i++) {
                            JSONObject trackObject = itemsArray.getJSONObject(i);
                            String trackName = trackObject.getString("name");
                            topTracks.append((i + 1)).append(". ").append(trackName).append("\n");
                        }

                        // Update the UI with the top tracks
                        getActivity().runOnUiThread(() -> {
                            // Append the top tracks to the existing text
                            //profileTextView.append(topTracks.toString());
                            adapter.addItem(topTracks.toString());
                        });
                    } else {
                        Log.d("HTTP", "Failed to fetch tracks data: " + response.code());
                        Toast.makeText(requireContext(), "Failed to fetch tracks data: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse tracks data: " + e);
                    Toast.makeText(requireContext(), "Failed to parse tracks data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Create a request to get the user's top tracks

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
                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}