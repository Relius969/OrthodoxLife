package org.telegram.pravzhizn.pravzhizn;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.pravzhizn.config.PravzhiznConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by matelskyvv on 6/20/16.
 */
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request original = chain.request();

        PravzhiznConfig config = new PravzhiznConfig(ApplicationLoader.applicationContext);

        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + config.authToken())
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }

}
