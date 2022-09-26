package com.tmax.hyperauth.rest;

import com.tmax.hyperauth.caller.StringUtil;
import com.tmax.hyperauth.jpa.Agreement;
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
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Null;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    @Path("{userName}")
    public Response get(@HeaderParam("Authorization") String authorization,
                        @PathParam("userName")  final String userName) {

//        boolean flag = isPermitted(authorization);
//
//        if(!flag){
//            status = Response.Status.UNAUTHORIZED;
//            out = "Not Permitted";
//            return Util.setCors(status, out);
//        }


        log.info("invoke user profile: " + userName);


        RealmModel realm = session.getContext().getRealm();
        String realmName = realm.getName();
        if (realmName == null) {realmName = realm.getName();}

        UserModel user = session.users().getUserByUsername(userName, session.realms().getRealmByName(realmName));

        log.info("user ID: " + user.getId());
        UserProfile userProfile = getEntityManager().createNamedQuery("findByUserId",UserProfile.class).setParameter("id",user.getId()).getSingleResult();
        log.info("user Profile: " + userProfile.toString());

        status = Response.Status.OK;
        out = userProfile.toString();

        return Util.setCors(status, out);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{userEmail}")
    public Response update(@HeaderParam("Authorization") String authorization,
                           @PathParam("userEmail") final String userEmail,
                           @QueryParam("age") int age,
                           @QueryParam("sex") String sex,
                           @QueryParam("phoneNumber") String phoneNumber,
                           @QueryParam("job") String job){

//        boolean flag = isPermitted(authorization);
//        if(!flag){
//            status = Response.Status.UNAUTHORIZED;
//            out = "Not Permitted";
//            return Util.setCors(status, out);
//        }

        log.info("update user profile: " + userEmail);

        RealmModel realm = session.getContext().getRealm();
        String realmName = realm.getName();
        if (realmName == null) {realmName = realm.getName();}


        //1. 유저 프로필이 있으면 저장
        UserModel user = null;

        try {
            user = session.users().getUserByUsername(userEmail, session.realms().getRealmByName(realmName));
            getEntityManager().createNamedQuery("findByUserId", UserProfile.class).setParameter("id", user.getId()).getSingleResult();

            //있는 유저면 update문으로 실행.
            getEntityManager().createNamedQuery("updateUserProfile")
                    .setParameter("id", user.getId())
                    .setParameter("age", age)
                    .setParameter("sex", sex)
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("job", job)
                    .executeUpdate();
            status = Response.Status.OK;
            out = "user profile updated";

        }catch (NullPointerException e){
            log.error("Cannot find User: " + userEmail);
            status = Response.Status.BAD_REQUEST;
            out = "Cannot find user: " + userEmail;
        }catch (NoResultException e){

            //없는 유저면 insert를 실행한다.
            UserProfile userProfile = new UserProfile();
                userProfile.setId(user.getId());
                userProfile.setEmail(userEmail);
                userProfile.setAge(age);
                userProfile.setSex(sex);
            userProfile.setPhoneNumber(phoneNumber);
            userProfile.setJob(job);

            getEntityManager().persist(userProfile);
            status = Response.Status.OK;
            out = "user profile saved";

        }catch (Exception e){
            log.error("error occured while executing update sql");
            e.printStackTrace();
            status = Response.Status.INTERNAL_SERVER_ERROR;
            out = "error occured...";
        }


        return Util.setCors(status, out);
    }

    public boolean isPermitted(String authorization){

        if(StringUtil.isEmpty(authorization)){return false;}

        String tokenString = authorization.split("Bearer ")[1];

        if(!Util.isHyperauthAdmin(session,tokenString)){return false;}

        return true;
    }

}
