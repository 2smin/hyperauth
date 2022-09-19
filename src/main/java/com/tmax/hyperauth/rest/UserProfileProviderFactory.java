package com.tmax.hyperauth.rest;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class UserProfileProviderFactory implements RealmResourceProviderFactory {

    // unique identifier (administration page)
    private static String ID = "profile";

    @Override
    public String getId(){
        return ID;
    }
    //객체 생성
    @Override
    public RealmResourceProvider create(KeycloakSession session){
        return new UserProfileProvider(session);
    }

    @Override
    public void init(Config.Scope config){

    }

    @Override
    public void postInit(KeycloakSessionFactory factory){}

    @Override
    public void close(){}
}
