package com.example.dailymate.Presentation.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.dailymate.Data.database.DBHelper;
import com.example.dailymate.R;

public class ProfileFragment extends Fragment {
    ImageView imageProfile;
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        TextView textName = view.findViewById(R.id.text_name);
        Button btnEditProfile = view.findViewById(R.id.button_edit_profile);

        DBHelper dbHelper = new DBHelper(requireContext());
        textName.setText(dbHelper.getUserName());

        btnEditProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        getParentFragmentManager().setFragmentResultListener("editProfileResult", this,
                (requestKey, result) -> {
                    String uriString = result.getString("imageUri");
                    if (uriString != null) {
                        Uri imageUri = Uri.parse(uriString);
                        imageProfile.setImageURI(imageUri);
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView textName = getView().findViewById(R.id.text_name);
        DBHelper dbHelper = new DBHelper(requireContext());
        textName.setText(dbHelper.getUserName());
    }

}
