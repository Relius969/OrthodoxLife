package org.telegram.pravzhizn.pravzhizn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.pravzhizn.pravzhizn.deserializers.TempleDetailsDeserializer;
import org.telegram.pravzhizn.pravzhizn.deserializers.TemplesResponseDeserializer;
import org.telegram.pravzhizn.pravzhizn.responses.CitiesResponse;
import org.telegram.pravzhizn.pravzhizn.responses.CountriesResponse;
import org.telegram.pravzhizn.pravzhizn.responses.CountryByCodeResponse;
import org.telegram.pravzhizn.pravzhizn.responses.ProfileResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SaintsResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.AddChatResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.AddMyTempleResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.RemoveMyTempleResponse;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.SendCreateChatRequest;
import org.telegram.pravzhizn.pravzhizn.responses.TempleDetailsResponse;
import org.telegram.pravzhizn.pravzhizn.responses.TemplesResponse;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Vlad on 5/31/16.
 */
public interface PravzhiznService {

    String HTTPS_PRAVZHIZN_RU = "https://pravzhizn.ru/";

    @GET("hramy/api/messenger/temple/list")
    Call<TemplesResponse> templesByName(@Query("name") String name, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/temple/list")
    Call<TemplesResponse> templesByRadius(@Query("lat") double lat, @Query("lng") double lng, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/temple/list")
    Call<TemplesResponse> templesByRadius(@Query("lat") double lat, @Query("lng") double lng, @Query("radius") int radius, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/temple/list")
    Call<TemplesResponse> templesByCity(@Query("country_id")int country_id, @Query("city_id") int city_id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/temple/view")
    Call<TempleDetailsResponse> templeDetails(@Query("id") int id);

    @FormUrlEncoded
    @POST("hramy/api/messenger/chat/create/")
    Call<AddChatResponse> addChatToTemple(@Field("type") int type, @Field("temple_id") int temple_id, @Field("telegram_gid") int telegram_gid);

    @GET("hramy/api/messenger/user/temples/")
    Call<TemplesResponse> myTemples(@Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/city/list/")
    Call<CitiesResponse> citiesList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/city/list/")
    Call<CitiesResponse> citiesList(@Query("name") String name, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/city/list/")
    Call<CitiesResponse> citiesList(@Query("country_id") int country_id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/profile/view/")
    Call<ProfileResponse> getProfile();

    @GET("hramy/api/messenger/profile/view/")
    Call<ProfileResponse> getProfileOfUser(@Query("id") int id);

    @GET("hramy/api/messenger/profile/getSaints/")
    Call<SaintsResponse> saintsByName(@Query("name") String name);

    @FormUrlEncoded
    @POST("hramy/api/messenger/profile/save/")
    Call<ProfileResponse> saveProfile(
            @Field("birthday") String birthday,
            @Field("profession") String profession,
            @Field("saint_id") Integer saint_id,
            @Field("country_id") Integer country_id,
            @Field("city_id") Integer city_id);

    @FormUrlEncoded
    @POST("hramy/api/messenger/profile/save/")
    Call<ProfileResponse> saveBirthday(@Field("birthday") String birthday);

    @FormUrlEncoded
    @POST("hramy/api/messenger/profile/save/")
    Call<ProfileResponse> saveProfession(@Field("profession") String profession);

    @FormUrlEncoded
    @POST("hramy/api/messenger/profile/save/")
    Call<ProfileResponse> saveSaint(@Field("saint_id") Integer saint_id);

    @GET("hramy/api/messenger/city/list/")
    Call<CitiesResponse> citiesList(@Query("country_id") int country_id, @Query("name") String name, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/country/list/")
    Call<CountriesResponse> countriesList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/country/list/")
    Call<CountriesResponse> countriesList(@Query("name") String name, @Query("limit") int limit, @Query("offset") int offset);

    @GET("hramy/api/messenger/country/findByCode/")
    Call<CountryByCodeResponse> findCountryByCode(@Query("code") String code);


    @FormUrlEncoded
    @POST("hramy/api/messenger/user/addTemple/")
    Call<AddMyTempleResponse> addMyTemple(@Field("temple_id") int templeId);

    @FormUrlEncoded
    @POST("hramy/api/messenger/user/removeTemple/")
    Call<RemoveMyTempleResponse> removeMyTemple(@Field("temple_id") int templeId);

    @FormUrlEncoded
    @POST("hramy/api/messenger/chat/request/")
    Call<SendCreateChatRequest> sendChatRequest(@Field("user_phone") String userPhone, @Field("temple_id") int templeId, @Field("type") int type);



    @Multipart
    @POST("hramy/api/messenger/user/createTemple")
    Call<ResponseBody> createTempleRequest(
            @Part("churchName") RequestBody churchName,
            @Part("churchAddress") RequestBody churchAddress);

    @Multipart
    @POST("hramy/api/messenger/user/createTemple")
    Call<ResponseBody> createTempleRequest(
            @Part("churchName") RequestBody churchName,
            @Part("churchAddress") RequestBody churchAddress,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("hramy/api/messenger/user/createTemple")
    Call<ResponseBody> createTempleRequest(
            @Part("churchName") RequestBody churchName,
            @Part("churchAddress") RequestBody churchAddress,
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2);

    @Multipart
    @POST("hramy/api/messenger/user/createTemple")
    Call<ResponseBody> createTempleRequest(
            @Part("churchName") RequestBody churchName,
            @Part("churchAddress") RequestBody churchAddress,
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2,
            @Part MultipartBody.Part file3);

    Gson gsonForRetrofit = new GsonBuilder()
            .registerTypeAdapter(TemplesResponse.class, new TemplesResponseDeserializer())
            .registerTypeAdapter(TempleDetailsResponse.class, new TempleDetailsDeserializer())
            .create();

    OkHttpClient client = new OkHttpClient.Builder()
            .authenticator(new TokenAuthenticator())
            .addInterceptor(new TokenInterceptor())
            .build();

    Retrofit instance = new Retrofit.Builder()
            .baseUrl(HTTPS_PRAVZHIZN_RU)
            .addConverterFactory(GsonConverterFactory.create(gsonForRetrofit))
            .client(client)
            .build();

}