package com.tmax.hyperauth.identity.initech;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.core.Response;
import java.net.URI;

@Slf4j
public class InitechIdentityProvider extends AbstractOAuth2IdentityProvider implements SocialIdentityProvider {
    public static final String AUTH_URL = "https://tmax.initech.com/externalauth"; //FIXME!!
    public static final String TOKEN_URL = "https://tmax.initech.com/token";
    public static final String PROFILE_URL = "https://tmax.initech.com/user";
    public static final String DEFAULT_SCOPE = "basic";

    public InitechIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

//    @Override
//    public Response performLogin(AuthenticationRequest request) {
//        try {
//            URI authorizationUrl = createAuthorizationUrl(request).build();
//
//            return Response.seeOther(authorizationUrl).build();
//        } catch (Exception e) {
//            throw new IdentityBrokerException("Could not create authentication request.", e);
//        }
//    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        BrokeredIdentityContext user = new BrokeredIdentityContext(profile.get("id").asText());
//        String email = profile.get("email").asText();
        String username = profile.get("username").asText();
        user.setIdpConfig(getConfig());
        user.setUsername(username);
//        user.setEmail(email);
        user.setIdp(this);
        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());
        return user;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        try {
            JsonNode profile = SimpleHttp.doGet(PROFILE_URL, session).param("access_token", accessToken).asJson();
            log.info("httpGet Success!!");
            BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);
            return user;
        } catch (Exception e) {
            log.error("Error Occurs!!", e);
            throw new IdentityBrokerException("Could not obtain user profile from initech.", e);
        }
    }

    @Override
    protected String getDefaultScopes() {
        return "";
    }
}