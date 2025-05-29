package com.example.dailymate.Presentation.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dailymate.Data.database.DBHelper;
import com.example.dailymate.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends Fragment {
    private EditText editName;
    Button btnCamera, btnGallery;
    ImageView imageProfile;
    private Uri selectedImageUri;

    private Button btnSave;
    private DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        checkAndRequestPermissions();

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editName = view.findViewById(R.id.edit_name);
        btnSave = view.findViewById(R.id.btn_save);
        dbHelper = new DBHelper(requireContext());

        btnCamera = view.findViewById(R.id.btn_camera);
        btnGallery = view.findViewById(R.id.btn_gallery);
        imageProfile = view.findViewById(R.id.image_profile);

        editName.setText(dbHelper.getUserName());

        btnCamera.setOnClickListener(v -> {
            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camIntent, 100);
        });


        btnGallery.setOnClickListener(v -> {
            Intent galIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galIntent, 101);
        });

        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            dbHelper.updateUserName(newName);  // Anda perlu menambahkan method ini

            if (selectedImageUri != null) {
                dbHelper.updateUserPhoto(selectedImageUri.toString());
            }

            // Kirim hasil ke ProfileFragment
            Bundle result = new Bundle();
            if (selectedImageUri != null) {
                result.putString("imageUri", selectedImageUri.toString());
            }
            getParentFragmentManager().setFragmentResult("editProfileResult", result);

            Toast.makeText(getContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsNeeded.toArray(new String[0]), 1);
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        }, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageProfile.setImageBitmap(photo);

                // Simpan bitmap ke file dan dapatkan Uri-nya
                selectedImageUri = saveImageAndGetUri(photo);

            } else if (requestCode == 101) {
                selectedImageUri = data.getData();
                imageProfile.setImageURI(selectedImageUri);
            }
        }
    }

    private Uri saveImageAndGetUri(Bitmap bitmap) {
        try {
            File file = new File(requireContext().getCacheDir(), "profile_image.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
