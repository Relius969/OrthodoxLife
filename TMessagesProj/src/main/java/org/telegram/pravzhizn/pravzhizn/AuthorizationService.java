package org.telegram.pravzhizn.pravzhizn;

import org.telegram.pravzhizn.pravzhizn.responses.RegisterResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by matelskyvv on 6/20/16.
 */
public interface AuthorizationService {

    @FormUrlEncoded
    @POST("hramy/api/messenger/user/register/")
    Call<RegisterResponse> register(
            @Field("access_hash") long access_hash,
            @Field("user_id") int user_id,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("username") String username,
            @Field("phone") String phone);

    Retrofit instance = new Retrofit.Builder()
            .baseUrl(PravzhiznService.HTTPS_PRAVZHIZN_RU)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
