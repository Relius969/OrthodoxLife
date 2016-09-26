package org.telegram.pravzhizn.pravzhizn;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.UserConfig;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.responses.RegisterResponse;
import org.telegram.tgnet.TLRPC;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Class that is responsible for renewing of access token
 *
 * Created by vlad on 6/20/16.
 */
public class TokenAuthenticator implements Authenticator {

    private final AuthorizationService mService;

    public TokenAuthenticator() {
        mService = AuthorizationService.instance.create(AuthorizationService.class);
    }

    @Override
    public Request authenticate(final Route route, final Response response) throws IOException {
        PravzhiznConfig config = new PravzhiznConfig(ApplicationLoader.applicationContext);
        TLRPC.User user = UserConfig.getCurrentUser();
        String newAccessToken = "";

        final retrofit2.Response<RegisterResponse> registerResponseResponse =
                mService.register(
                        user.access_hash,
                        user.id,
                        user.first_name,
                        user.last_name,
                        user.username,
                        user.phone).execute();
        if (registerResponseResponse.isSuccessful()) {
            if (registerResponseResponse.body().success) {
                newAccessToken = registerResponseResponse.body().data.token;
                config.setAuthToken(newAccessToken);
            }
        }

        // Add new header to rejected request and retry it
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }
}