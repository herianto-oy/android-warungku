package com.warungku.utils.api;

import com.warungku.models.pos.TransactionModel;
import com.warungku.models.user.UserModel;
import com.warungku.utils.api.request.RequestRegister;
import com.warungku.utils.api.request.RequestLogin;
import com.warungku.utils.api.response.ResponseLogin;
import com.warungku.utils.api.response.ResponseProduct;
import com.warungku.utils.api.response.ResponseProductList;
import com.warungku.utils.api.response.ResponseRegister;
import com.warungku.utils.api.response.ResponseTransaction;
import com.warungku.utils.api.response.ResponseTransactions;
import com.warungku.utils.api.response.ResponseUser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);

    @POST("register")
    Call<ResponseRegister> register(@Body RequestRegister requestRegister);

    @GET("user/{id}")
    Call<ResponseUser> findUserById(@Path("id") int id);

    @POST("user")
    Call<ResponseUser> updateUser (@Body UserModel user);

    @Multipart
    @POST("pupload")
    Call<ResponseUser> uploadImg(@Part MultipartBody.Part img, @Part("id") RequestBody id);

    @Multipart
    @POST("product")
    Call<ResponseProduct> createUpdateProduct(@Part("id") RequestBody id,
                                              @Part MultipartBody.Part img,
                                              @Part("user_id") RequestBody userId,
                                              @Part("code") RequestBody code,
                                              @Part("name") RequestBody name,
                                              @Part("price") RequestBody price,
                                              @Part("stock") RequestBody stock);

    @GET("product")
    Call<ResponseProductList> getProductList(@Query("id") int id, @Query("page") int page);

    @GET("product/search")
    Call<ResponseProduct> getProductByCode(@Query("user_id") int id, @Query("code") String code);

    @GET("product/id/{id}")
    Call<ResponseProduct> getProductById(@Path("user_id") int id);

    @DELETE("product/{id}")
    Call<ResponseProduct> deleteProduct(@Path("id") int id);

    @GET("transaction")
    Call<ResponseTransactions> getTransactions(@Query("id") int id, @Query("page") int page);

    @GET("transaction/id/{id}")
    Call<ResponseTransaction> getTransactionById(@Path("id") int id);

    @POST("transaction")
    Call<ResponseTransaction> createTransaction(@Body TransactionModel transactionModel);

    @PUT("transaction")
    Call<ResponseTransaction> cancelTransaction(@Query("id") int id);
}
