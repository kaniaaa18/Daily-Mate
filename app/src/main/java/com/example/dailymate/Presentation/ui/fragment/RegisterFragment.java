package com.example.dailymate.Presentation.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailymate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister, btnGoToLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inisialisasi view
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnGoToLogin = view.findViewById(R.id.btnGoToLogin);
        progressBar = view.findViewById(R.id.progressBar);

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Event klik
        btnRegister.setOnClickListener(v -> registerUser());
        btnGoToLogin.setOnClickListener(v -> navigateToLogin());

        return view;
    }

    private void navigateToLogin() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Update display name sesuai username
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                user.updateProfile(profileUpdates);
                            }
                            Toast.makeText(getContext(), "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                            // Kembali ke LoginFragment
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainer, new LoginFragment())
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "Registrasi gagal: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}