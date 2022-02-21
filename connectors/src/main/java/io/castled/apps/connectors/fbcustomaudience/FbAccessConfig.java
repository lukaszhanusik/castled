package io.castled.apps.connectors.fbcustomaudience;

import io.castled.oauth.OAuthAccessConfig;
import io.castled.oauth.OAuthServiceType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FbAccessConfig extends OAuthAccessConfig {

    private long expiresIn;
    private long tokenEpochSecond;

    @Builder
    public FbAccessConfig(String accessToken, long expiresIn, long tokenEpochSecond) {
        super(OAuthServiceType.FBCUSTOMAUDIENCE, accessToken, null);
        this.expiresIn = expiresIn;
        this.tokenEpochSecond = tokenEpochSecond;
    }
}
