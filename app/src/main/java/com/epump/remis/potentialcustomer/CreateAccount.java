package com.epump.remis.potentialcustomer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epump.remis.potentialcustomer.service.IdeaService;
import com.epump.remis.potentialcustomer.service.ServiceBuilder;
import com.epump.remis.potentialcustomer.utils.NewAccount;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccount extends AppCompatActivity {

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEmailEditText;
    private EditText mPasswordOneEditText;
    private EditText mPasswordTwoEditText;
    private Button mSubmitProfile;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mActivity = this;
        mFirstNameEditText = findViewById(R.id.first_name);
        mLastNameEditText = findViewById(R.id.last_name);
        mPhoneNumberEditText = findViewById(R.id.phone_number);
        mEmailEditText = findViewById(R.id.email);
        mPasswordOneEditText = findViewById(R.id.password_create);
        mPasswordTwoEditText = findViewById(R.id.password_create_second);
        mSubmitProfile = findViewById(R.id.submit_create);
        mSubmitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mFirstNameEditText.getText().toString()) ||LoginPage.isEmpty(mLastNameEditText.getText().toString())
                        ||LoginPage.isEmpty(mEmailEditText.getText().toString())
                        ||LoginPage.isEmpty(mPhoneNumberEditText.getText().toString())||LoginPage.isEmpty(mPasswordOneEditText.getText().toString())
                        ||LoginPage.isEmpty(mPasswordTwoEditText.getText().toString())){

                    Snackbar.make(view,"You must fill all fields with data.",Snackbar.LENGTH_LONG).show();
                }else{
                    if(!mPasswordTwoEditText.getText().toString().equals(mPasswordTwoEditText.getText().toString())){
                        Snackbar.make(view,"Password Mismatch,Check password and try again.",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        if(mPasswordOneEditText.getText().toString().length() > 5) {
                            NewAccount newAccount = new NewAccount();
                            newAccount.setFirstName(mFirstNameEditText.getText().toString());
                            newAccount.setLastName(mLastNameEditText.getText().toString());
                            newAccount.setUserName(mEmailEditText.getText().toString());
                            newAccount.setPassword(mPasswordOneEditText.getText().toString());
                            newAccount.setConfirmPassword(mPasswordTwoEditText.getText().toString());
                            newAccount.setPhone(mPhoneNumberEditText.getText().toString());
                            newAccount.setReferralCode("");
                            LoginPage.showDialog(mActivity, "Creating Account");
                            IdeaService ideaService = ServiceBuilder.buildeService(IdeaService.class);
                            Call<NewAccount> ideaCall = ideaService.createAccount(newAccount);
                            ideaCall.enqueue(new Callback<NewAccount>() {
                                @Override
                                public void onResponse(Call<NewAccount> call, Response<NewAccount> response) {
                                    if (response.isSuccessful()) {
                                        LoginPage.removeDialog();
                                        Toast.makeText(mActivity, "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(mActivity,"Check your email confirmation link",Toast.LENGTH_LONG).show();

                                        finish();
                                    } else {
                                        LoginPage.removeDialog();
                                        Toast.makeText(mActivity, "Account wasn't created.", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<NewAccount> call, Throwable t) {
                                    LoginPage.removeDialog();
                                    if (t instanceof IOException) {
                                        Toast.makeText(getApplicationContext(), "A connection error occurred.", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(mActivity, "Account wasn't created.", Toast.LENGTH_SHORT).show();

                                }

                            });
                        }else{
                            Toast.makeText(mActivity, "The Password must be at least 6 and at max 100 characters long.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            }
        });





    }
}
