package com.epump.remis.potentialcustomer.service;

import com.epump.remis.potentialcustomer.utils.LoginResponse;
import com.epump.remis.potentialcustomer.utils.NewAccount;
import com.epump.remis.potentialcustomer.utils.NewCustomer;
import com.epump.remis.potentialcustomer.utils.UserSignIn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IdeaService {
    @POST("Account/Register")
    Call<NewAccount> createAccount(@Body NewAccount newAccount);

    @POST("Account/login")
    Call<LoginResponse> loginAccount(@Body UserSignIn userSignIn);

    @POST("Customer/Expected")
    Call<NewCustomer> createNewCustomer(@Body NewCustomer newCustomer, @Header("Authorization")String auth);

    @GET("Customer/CountExpected/{referrer}")
    Call<String> getAmountOfRegisteredCustomers(@Path("referrer")String email,@Header("Authorization")String auth);
}
