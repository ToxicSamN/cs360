package com.example.happyweight;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.happyweight.models.LoginModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistrationFragment extends Fragment {
    private FirebaseAuth fAuth;
    private NavController navController;

    private TextInputLayout first_name;
    private TextInputLayout last_name;
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout confirm_password;

    private Button btnCreate;
    private Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        first_name = view.findViewById(R.id.etxtFirstName);
        last_name = view.findViewById(R.id.etxtLastName);
        username = view.findViewById(R.id.etxtUsername);
        password = view.findViewById(R.id.etxtPassword);
        confirm_password = view.findViewById(R.id.etxtConfPassword);

        btnCreate = view.findViewById(R.id.btnRegister);
        btnCreate.setOnClickListener(this::onclickRegister);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this::gotoLogin);

        super.onViewCreated(view, savedInstanceState);
    }

    private void clearFieldErrors() {
        first_name.setError(null);
        last_name.setError(null);
        username.setError(null);
        password.setError(null);
        confirm_password.setError(null);
    }

    private boolean validateUsername() {
        String uname = username.getEditText().getText().toString().trim();

        if (uname.isEmpty()) {
            username.setError("Must supply a username");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(uname).matches()){
            username.setError("Not a valid email address");
            return false;
        }

        username.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String pwd = password.getEditText().getText().toString().trim();
        String cpwd = confirm_password.getEditText().getText().toString().trim();

        if (pwd.isEmpty()) {
            password.setError("Must supply a password");
            return false;
        } else if (cpwd.isEmpty()){
            confirm_password.setError("Must confirm password");
            return false;
        } else if (!pwd.equals(cpwd)) {
            password.setError("Passwords do not match");
            confirm_password.setError("Passwords do not match");
            return false;
        }

        password.setError(null);
        confirm_password.setError(null);
        return true;
    }

    private boolean validateFirstName() {
        String name = first_name.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            first_name.setError("Required Input");
            return false;
        }

        first_name.setError(null);
        return true;
    }

    private boolean validateLastName() {
        String name = last_name.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            last_name.setError("Required Input");
            return false;
        }

        last_name.setError(null);
        return true;
    }

    public void onclickRegister(View v){
        if (!this.validateFirstName() | !this.validateLastName() | !this.validateUsername() | !this.validatePassword()){
            Toast.makeText(v.getContext(), "Input Validation failed", Toast.LENGTH_LONG).show();
            return;
        }

        fAuth.createUserWithEmailAndPassword(username.getEditText().getText().toString().trim(), password.getEditText().getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                clearFieldErrors();
                Toast.makeText(v.getContext(), "Registration Success", Toast.LENGTH_SHORT).show();

                // create model and document
                LoginModel model = new LoginModel();
                model.setUserID(authResult.getUser().getUid());
                model.setFirstName(first_name.getEditText().getText().toString());
                model.setLastName(last_name.getEditText().getText().toString());
                model.setData();

                // navigate to login page
                gotoLogin(v);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                clearFieldErrors();
                if (e instanceof FirebaseAuthUserCollisionException) {
                    username.setError(e.getMessage());
                }
                if (e instanceof FirebaseAuthWeakPasswordException){
                    password.setError(((FirebaseAuthWeakPasswordException) e).getReason());
                }
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void gotoLogin(View v) {
        // navigate to Login
        navController.navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment());
    }
}