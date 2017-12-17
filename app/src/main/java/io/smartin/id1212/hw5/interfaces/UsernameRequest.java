package io.smartin.id1212.hw5.interfaces;

import io.smartin.id1212.hw5.controller.FirebaseController;

/**
 * Created by sam on 2017-12-17.
 */

public interface UsernameRequest {
    void usernameAccepted(String username);
    void usernameDenied(String username, String error);
}
