package com.tmax.hyperauth.authenticator.PhoneNumberCheck;

import com.tmax.hyperauth.jpa.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
@Slf4j
public class PhoneNumberAuthenticator implements Authenticator {

    @Context
    private KeycloakSession session;

    public PhoneNumberAuthenticator(KeycloakSession session){this.session = session;}

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @Override
    public void authenticate(AuthenticationFlowContext context){

        UserModel user = context.getUser();

        log.info("phoneNumber auth..... user info: " + user);
        log.info("phoneNumber auth..... user info: " + user.getId());
        log.info("phoneNumber auth..... user info: " + user.getEmail());
        log.info("phoneNumber auth..... user info: " + user.getUsername());
        try{
            UserProfile userProfile = getEntityManager().createNamedQuery("findByUserId", UserProfile.class).setParameter("id", user.getId()).getSingleResult();

            log.info("user phoneNumber: " + userProfile.getPhoneNumber());
            if(userProfile.getPhoneNumber() == null || userProfile.getPhoneNumber().length() == 0){
                throw new NoResultException();
            }

            context.success();

        }catch (NoResultException e){
            log.error("user phoneNumber not set");
            Response challenge = context.form()
                    .createForm("phone-number.ftl");
            context.challenge(challenge);
        }

    }

    @Override
    public void action(AuthenticationFlowContext context){
        //phoneNumber 입력받도록 추가
        log.info("phoneNumber check action success");
        context.success();
    }

    @Override
    public boolean requiresUser(){return false;}

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user){
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) { }

    @Override
    public void close() { }

}
