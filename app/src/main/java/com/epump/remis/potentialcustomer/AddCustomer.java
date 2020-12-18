package com.epump.remis.potentialcustomer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epump.remis.potentialcustomer.service.IdeaService;
import com.epump.remis.potentialcustomer.service.ServiceBuilder;
import com.epump.remis.potentialcustomer.utils.NewCustomer;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomer extends AppCompatActivity {

    private EditText mCustomerName;
    private EditText mCustomerPhone;
    private EditText mCustomerEmail;
    private Button mRegisterCustomer;

    private String mHeader;
    private SharedPreferences mSharedPreferences;
    private ImageView mGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        mSharedPreferences = getApplicationContext().getSharedPreferences("SHAREDP",Context.MODE_PRIVATE);
        mHeader = "Bearer "+ mSharedPreferences.getString("token","");
        mCustomerName = findViewById(R.id.name_of_customer);
        mCustomerPhone = findViewById(R.id.phone_of_customer);
        mCustomerEmail = findViewById(R.id.email_of_customer);
        mRegisterCustomer = findViewById(R.id.register_customer);
        mGoBack = findViewById(R.id.register_new_to_registered);
        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mRegisterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPage.isEmpty(mCustomerName.getText().toString())
                        ||LoginPage.isEmpty(mCustomerPhone.getText().toString())
                ){
                    Snackbar.make(view,"You must fill all fields with data",Snackbar.LENGTH_LONG).show();
                }else{
                    LoginPage.showDialog(AddCustomer.this,"Registering Customer");
                    NewCustomer newCustomer = new NewCustomer();
                    newCustomer.setEmail(mCustomerEmail.getText().toString().equals("")?"":mCustomerEmail.getText().toString());
                    newCustomer.setPhone(mCustomerPhone.getText().toString());
                    newCustomer.setFirstName(mCustomerName.getText().toString());
                    newCustomer.setLastName("");
                    newCustomer.setReferrer(mSharedPreferences.getString("email",""));
                    IdeaService ideaService = ServiceBuilder.buildeService(IdeaService.class);
                    Call<NewCustomer> ideaCall = ideaService.createNewCustomer(newCustomer,mHeader);
                    ideaCall.enqueue(new Callback<NewCustomer>() {
                        @Override
                        public void onResponse(Call<NewCustomer> call, Response<NewCustomer> response) {
                            LoginPage.removeDialog();
                            if(response.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Successfully registered customer.",Toast.LENGTH_LONG).show();
                                mCustomerName.setText("");
                                mCustomerEmail.setText("");
                                mCustomerPhone.setText("");
                                mCustomerName.requestFocus();
                            }else if(response.code() == 401){
                                Toast.makeText(getApplicationContext(),"You cant perform this operation,upgrade to admin to use this feature.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Couldn't register customer.",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<NewCustomer> call, Throwable t) {
                            if(t instanceof IOException){
                                Toast.makeText(getApplicationContext(),"A connection error occurred.",Toast.LENGTH_SHORT).show();
                            }
                            LoginPage.removeDialog();
                            Toast.makeText(getApplicationContext(),"Couldn't register customer.",Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }
        });



    }
}
