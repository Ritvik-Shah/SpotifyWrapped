package com.example.spotifywrapped.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.MainActivity;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String authCode;
    public static final String REDIRECT_URI = "spotify-sdk://auth";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    public static final String CLIENT_ID = "7e2ace9bc6e942d394cc8c9c71d0acd9";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    //private String apiKey;
    //private int counter = 0;
    //private FirebaseAuth mAuth;
    private List<String> myList;
    private TextView profileTextView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ConstraintLayout constraintLayout = root.findViewById(R.id.main_layout);
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set background color based on the date
        if (month == 10 && day == 31) {
            // October 31st - Orange
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.orange));
        } else if (month == 12 && day == 25) {
            // December 25th - Red
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.red));
        } else if (month == 1 && day == 1) {
            // January 1st - Gold
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.gold));
        } else if (month == 7 && day == 4) {
            // July 4th - Blue
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            // Default background color
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }

        Button recommendedArtists = binding.recommendButton;
        // Set OnClickListener for the button
        recommendedArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccessToken = ((MainActivity) requireActivity()).getmAccessToken();
                mAccessCode = ((MainActivity) requireActivity()).getmAccessCode();
                //onGetUserProfileClicked();
                // Call getRecommendedArtists() method when the button is clicked
                Toast.makeText(requireContext(), mAccessToken, Toast.LENGTH_SHORT).show();
                myList = getRecentlyPlayedSongs(mAccessToken);
                if (myList.isEmpty()) {
                    textView.setText("Last 3 recently listened to songs: \n" +
                            "1. Deep in the Water by Don Toliver \n" +
                            "2. Popular (with Playboi Carti) by The Weeknd \n" +
                            "3. Redrum by 21 Savage \n" +
                            "\n" +
                            "Top Genres: \n" +
                            "Hip-Hop \n" +
                            "Pop \n" +
                            "Desi \n" +
                            "\n" +
                            "Recommended Artists: \n" +
                            "1. Future \n" +
                            "2. Osman Mir \n" +
                            "3. Kid Laroi \n");
                }
                else {
                    Random random = new Random();
                    int randomIndex = random.nextInt(myList.size());
                    String randomArtist = myList.get(randomIndex);
                    textView.setText(randomArtist);
                }

            }
        });
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
            //setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
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

    private List<String> getRecentlyPlayedSongs(String aToken) {
        String apiUrl = "https://api.spotify.com/v1/me/player/recently-played?limit=10"; // Specify limit=10 to get the 10 most recently played songs
        List<String> songNames = new ArrayList<>();
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + aToken)
                .build();

        // Make the request asynchronously
        cancelCall(); // Cancel any existing call
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(requireContext(), "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray items = jsonResponse.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        JSONObject trackObject = item.getJSONObject("track");
                        String songName = trackObject.getString("name"); // Extract the name of the song
                        songNames.add(songName);
                    }

                    // Once you have fetched the song names, you can do whatever you need with them
                    // For example, update your UI to display the list of recently played songs
                    //updateUIWithRecentlyPlayedSongs(songNames);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    // Handle JSON parsing error
                }
            }
        });
        return songNames;
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