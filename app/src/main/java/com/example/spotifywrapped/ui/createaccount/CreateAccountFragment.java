package com.example.spotifywrapped.ui.createaccount;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.spotifywrapped.databinding.FragmentCreateaccountBinding;
import com.example.spotifywrapped.databinding.FragmentDashboardBinding;
import com.example.spotifywrapped.ui.dashboard.DashboardViewModel;

public class CreateAccountFragment extends Fragment{
    private FragmentCreateaccountBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentCreateaccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}
