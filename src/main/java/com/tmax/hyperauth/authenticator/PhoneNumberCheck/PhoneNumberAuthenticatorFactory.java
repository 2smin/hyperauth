package com.tmax.hyperauth.authenticator.PhoneNumberCheck;

import com.tmax.hyperauth.authenticator.AuthenticatorConstants;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    public static final String PROVIDER_ID = "phoneNumber-check";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    private static PhoneNumberAuthenticator SINGLETON;

    @Override
    public String getId(){
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session){
        return SINGLETON(session);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    private Authenticator SINGLETON(KeycloakSession session) {
        this.SINGLETON = new PhoneNumberAuthenticator(session);
        return SINGLETON;
    }

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.ALTERNATIVE,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() { return false; }

    @Override
    public boolean isConfigurable() {return true;}

    @Override
    public List<ProviderConfigProperty> getConfigProperties(){
        return configProperties;
    }

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(AuthenticatorConstants.PHONE_NUM_CHECK);
        property.setLabel("Check User Profile PhoneNumber");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Only user with phoneNumber could login");
        configProperties.add(property);
    }

    @Override
    public String getDisplayType() {return "User PhoneNumber check";}

    @Override
    public String getReferenceCategory() {
        return "User PhoneNumber check";
    }

    @Override
    public String getHelpText() { return "Only user with phoneNumber could login"; }


}
