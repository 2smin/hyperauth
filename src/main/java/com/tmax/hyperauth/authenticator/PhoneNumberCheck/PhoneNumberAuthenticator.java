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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

        if(!validatePhoneNumber(context)){
            log.error("retry submit phoneNumber.....");
            authenticate(context);
            return;
        }

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

    public boolean validatePhoneNumber(AuthenticationFlowContext context){

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        for(Entry<String, List<String>> map : formData.entrySet()){
            System.out.println(map.getKey() + " : " + map.getValue());
        }

        String phoneNumber = formData.getFirst("phone_number");

        UserModel user = context.getUser();
    
        log.info("user: " + user.toString());
        log.info("userID: " + user.getId());
        log.info("phone_number: " + phoneNumber);

        try{
            getEntityManager().createNamedQuery("findByUserId", UserProfile.class).setParameter("id", user.getId()).getSingleResult();

        }catch(NoResultException e){
         
            UserProfile userProfile = new UserProfile();
            userProfile.setId(user.getId());
            userProfile.setPhoneNumber(phoneNumber);
            userProfile.setEmail(user.getEmail());

            getEntityManager().persist(userProfile);
            log.info("user phoneNumber has been saved");
            
            return true;
        }

        int isUpdated = getEntityManager().createNamedQuery("updatePhoneNumber")
                .setParameter("phoneNumber", phoneNumber)
                .setParameter("id", user.getId())
                .executeUpdate();
        
        log.info("isUpdated: " + isUpdated );
        if(isUpdated!=1){
            log.error("phoneNumber update fail");
            return false;
        }

        log.info("user " + user.getEmail() + "'s phoneNumber has been updated : " + phoneNumber);
        return true;
    }

}
