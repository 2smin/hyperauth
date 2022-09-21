package com.tmax.hyperauth.rest;

import com.tmax.hyperauth.jpa.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.bouncycastle.est.ESTException;
import org.hibernate.engine.jdbc.StreamUtils;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class UserProfileProvider implements RealmResourceProvider {

    @Context
    private KeycloakSession session;

    @Context
    private HttpResponse response;

    public UserProfileProvider(KeycloakSession session){this.session = session;}

    @Override
    public Object getResource(){
        return this;
    }

    @Override
    public void close() {

    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    Response.Status status;
    String out;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("test")
    public Response test() {
        return Response.ok("hello").build();
    }

    //1. admin이 직접 하는 기능으로 해보자
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userName}")
    public Response get(@PathParam("userName")  final String userName) {

        log.info("invoke user profile: " + userName);

        RealmModel realm = session.getContext().getRealm();
        log.info("realmmodel: " + realm.toString());
        String realmName = realm.getName();
        if (realmName == null) {
            realmName = realm.getName();
        }

        log.info("realmName: " + realmName);

        UserModel user = session.users().getUserByUsername(userName, session.realms().getRealmByName(realmName));
        StringBuilder query;
//        String sql = new StringBuilder("select m from USER_PROFILE as m where m.user_id = '" + user.getId() + "'").toString();

        UserProfile userProfile = getEntityManager().createNamedQuery("findByUserId",UserProfile.class).setParameter("userId",user.getId()).getSingleResult();
//        Object userProfile = getEntityManager().createQuery(sql).setParameter("userId",user.getId()).getSingleResult();
        log.info("user Profile: " + userProfile.toString());

        status = Response.Status.OK;
        out = userProfile.toString();

        return Util.setCors(status, out);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{userName}")
    public Response set(@PathParam("userName") final String username,
                               UserProfile userProfile){

        log.info("userName: " + username);
        log.info(userProfile.toString());


        try{
            getEntityManager().persist(userProfile);
            status = Response.Status.OK;
            out = "Profile Updated";
        } catch (Exception e){
            log.error("error occured while persisting userProfile");
            status = Response.Status.INTERNAL_SERVER_ERROR;
            out = "Error occured while update userProfile";
        }

        return Util.setCors(status, out);
    }

}
