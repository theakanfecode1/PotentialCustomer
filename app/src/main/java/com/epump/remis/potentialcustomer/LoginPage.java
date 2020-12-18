package com.epump.remis.potentialcustomer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epump.remis.potentialcustomer.service.IdeaService;
import com.epump.remis.potentialcustomer.service.ServiceBuilder;
import com.epump.remis.potentialcustomer.utils.LoginResponse;
import com.epump.remis.potentialcustomer.utils.UserSignIn;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {

    public static Dialog mDialog;
    private Button mSignInBtn;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mCreateAccount;
    private SharedPreferences mSharedPreferences;
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mSharedPreferences = getApplicationContext().getSharedPreferences("SHAREDP", Context.MODE_PRIVATE);
        boolean loggedIn = mSharedPreferences.getBoolean("LOGINSTATE",false);
        if(loggedIn){
            Intent intent = new Intent(LoginPage.this,RegisteredCustomers.class);
            startActivity(intent);
            finish();
        }
        mEmailEditText = findViewById(R.id.loginemail);
        mPasswordEditText = findViewById(R.id.passwordlogin);
        mSignInBtn = findViewById(R.id.sign_in);
        mCreateAccount = findViewById(R.id.create_account);
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(mEmailEditText.getText().toString()) ||isEmpty(mPasswordEditText.getText().toString())){
                    Snackbar.make(view,"You must fill all fields with data",Snackbar.LENGTH_LONG).show();
                }else{
                    showDialog(LoginPage.this,"Logging in");
                    UserSignIn userSignIn = new UserSignIn();
                    userSignIn.setUserName(mEmailEditText.getText().toString());
                    userSignIn.setPassWord(mPasswordEditText.getText().toString());
                    userSignIn.setDeviceId("");
                    IdeaService ideaService = ServiceBuilder.buildeService(IdeaService.class);
                    Call<LoginResponse> ideaCall = ideaService.loginAccount(userSignIn);
                    ideaCall.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if(response.isSuccessful()){
                                token = response.body().getToken();
                                String email = response.body().getEmail();
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean("LOGINSTATE",true);
                                editor.putString("token",token);
                                editor.putString("email",email);
                                editor.commit();
                                Intent intent = new Intent(LoginPage.this,RegisteredCustomers.class);
                                startActivity(intent);
                                removeDialog();
                                finish();
                            }
                            else if(response.code() == 401){
                                removeDialog();
                                Toast.makeText(LoginPage.this,"Invalid login password",Toast.LENGTH_SHORT).show();
                            }else{
                                removeDialog();
                                Toast.makeText(LoginPage.this,"Couldn't Login",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            if(t instanceof IOException){
                                Toast.makeText(getApplicationContext(),"A connection Error occured.",Toast.LENGTH_SHORT).show();
                            }
                            removeDialog();
                            Toast.makeText(LoginPage.this,"Couldn't Login",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,CreateAccount.class));
            }
        });
    }
    public static void showDialog(Activity activity, String message){
        mDialog = new Dialog(activity,R.style.DialogFullScreen);
        mDialog.setContentView(R.layout.loading_screen);
        TextView title = mDialog.findViewById(R.id.dialog_message);
        title.setText(message);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }
    public static void removeDialog(){
        mDialog.dismiss();
    }
    public static boolean isEmpty(String string){
        return string.equals("");
    }

}
