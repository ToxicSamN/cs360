package com.example.happyweight;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.happyweight.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginFragment extends Fragment {
    private TextInputLayout username;
    private TextInputLayout password;
    private AlertDialog.Builder alrtBuilder;
    private LayoutInflater linflater;

    private Button btnLogin;
    private Button btnRegister;
    private Button btnForgotPwd;

    private FirebaseAuth fAuth;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        username = view.findViewById(R.id.etxtUsername);
        password = view.findViewById(R.id.etxtPassword);

        alrtBuilder = new AlertDialog.Builder(view.getContext());
        linflater = this.getLayoutInflater();

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this::onclickLogin);

        btnForgotPwd = view.findViewById(R.id.btnForgotPass);
        btnForgotPwd.setOnClickListener(this::onclickForgot);

        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this::gotoRegistration);

        if (fAuth.getCurrentUser() != null){

            // get user data, update local db
            updateLocalDatabase();

            // navigate to weight overview fragment
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToWeightOverviewFragment());
        }
    }

    private void clearPreviousBackStack(){
        // sometimes  the back stack is populated twice with teh startDestination of nav_graph
        // this will pop off the first entry
        NavBackStackEntry currBackStack = navController.getCurrentBackStackEntry();
        NavDestination currDestination = navController.getCurrentDestination();
        if (currBackStack != null){
            int id = currBackStack.getDestination().getId();
            navController.popBackStack(id, true);
        }
    }

    private void clearFieldErrors() {
        username.setError(null);
        password.setError(null);
    }

    private boolean validateUsername() {
        String uname = username.getEditText().getText().toString().trim();

        if (uname.isEmpty()) {
            username.setError("Must supply a username");
            return false;
        }

        username.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String pwd = password.getEditText().getText().toString().trim();

        if (pwd.isEmpty()) {
            password.setError("Must supply a password");
            return false;
        }

        password.setError(null);
        return true;
    }

    public void onclickLogin(View v){
        if (!this.validateUsername() | !this.validatePassword()){

            Toast.makeText(v.getContext(), "Input Validation failed", Toast.LENGTH_SHORT).show();
            return;
        }

        fAuth.signInWithEmailAndPassword(username.getEditText().getText().toString().trim(), password.getEditText().getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                clearFieldErrors();
                Toast.makeText(v.getContext(), "Login Success", Toast.LENGTH_SHORT).show();

                // get user data, update local db
                updateLocalDatabase();

                // navigate to weight overview
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToWeightOverviewFragment());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                clearFieldErrors();
                if (e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseAuthInvalidUserException) {
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    username.setError("Invalid Credentials");
                    password.setError("Invalid Credentials");
                    return;
                }

                username.setError("Unable to authenticate");
                password.setError("Unable to authenticate");
            }
        });
    }

    public void onclickForgot(View v){
        View fpDialog = linflater.inflate(R.layout.dialog_forgot_password, null);
        EditText email = fpDialog.findViewById(R.id.etxtFPemail);
        alrtBuilder.setTitle("Forgot Password")
                .setMessage("A password reset link will be sent to the registered email address.")
                .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // validate email address
                        String email_txt = email.getText().toString().trim();

                        if (email_txt.isEmpty()) {
                            email.setError("Must supply a valid email address");
                            Toast.makeText(v.getContext(), "Reset Password Email Address is Empty", Toast.LENGTH_LONG).show();
                        } else {
                            // send reset link
                            fAuth.sendPasswordResetEmail(email_txt);

                            // always display email sent whether or not the email is valid or not
                            // this is a more secure option
                            Toast.makeText(v.getContext(), "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(fpDialog)
                .create()
                .show();
    }

    public void gotoRegistration(View v) {
        // navigate to registration
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment());
    }

    private void updateLocalDatabase(){
        User umodel = new User(getContext());
        //LoginModel lmodel = new LoginModel(fAuth.getCurrentUser().getUid());
        //lmodel.getData();
        umodel.updateORcreateUserRecord(fAuth.getCurrentUser().getUid(), null, null);
    }
}