package com.example.spotifywrapped.ui.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentHomeBinding;

import java.util.Calendar;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //createNotificationChannel();
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}