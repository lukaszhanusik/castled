package io.castled.oauth;

public interface AccessTokenRefresher<T extends OAuthAccessConfig> {

    T refreshAndPersistAccessConfig(Long oauthToken);
}
