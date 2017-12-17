package io.smartin.id1212.hw5.interfaces;

/**
 * Created by sam on 2017-12-17.
 */

public interface UsernameRequest {
    void usernameAccepted(String username);
    void usernameDenied(String username, String error);
}
