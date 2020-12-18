package com.epump.remis.potentialcustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epump.remis.potentialcustomer.service.IdeaService;
import com.epump.remis.potentialcustomer.service.ServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredCustomers extends AppCompatActivity {

    private TextView mRegisteredCustomers;
    private SharedPreferences mSharedPreferences;
    private TextView mTitle;
    private Button registerNew;
    private ImageView mRefresh;
    private TextView mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_customers);
        LoginPage.showDialog(RegisteredCustomers.this,"Loading");
        mSharedPreferences = getApplicationContext().getSharedPreferences("SHAREDP", Context.MODE_PRIVATE);
        mRegisteredCustomers = findViewById(R.id.total_number_of_registered_customers);
        mRefresh = findViewById(R.id.refresh_registered);
        mLogout = findViewById(R.id.logout);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginPage.showDialog(RegisteredCustomers.this,"Loading");
                getData();
            }
        });
        registerNew = findViewById(R.id.register_new);
        registerNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisteredCustomers.this,AddCustomer.class));
            }
        });

        mTitle = findViewById(R.id.title_registered);

        getData();
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove("LOGINSTATE");
                editor.remove("token");
                editor.remove("email");
                editor.commit();
                startActivity(new Intent(RegisteredCustomers.this,LoginPage.class));
                finish();
            }
        });

    }

    private void getData() {
        String header = "Bearer "+mSharedPreferences.getString("token","");
        String email = mSharedPreferences.getString("email","");
        IdeaService ideaService = ServiceBuilder.buildeService(IdeaService.class);
        Call<String> ideaCall = ideaService.getAmountOfRegisteredCustomers(email,header);
        ideaCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoginPage.removeDialog();
                if(response.isSuccessful()){
                    mTitle.setVisibility(View.VISIBLE);
                    mRegisteredCustomers.setVisibility(View.VISIBLE);
                    mRegisteredCustomers.setText(response.body());
                }
                else {
                    mTitle.setVisibility(View.INVISIBLE);
                    mRegisteredCustomers.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Couldnt fetch data",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LoginPage.removeDialog();
                mTitle.setVisibility(View.INVISIBLE);
                mRegisteredCustomers.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Couldnt data",Toast.LENGTH_SHORT).show();

            }
        });

    }
}
