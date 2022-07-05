/*
 * Spotify
 * Simple API for advanced programming course, Dr. Mojtaba Vahidi Asl, Fall 1400 
 *
 * OpenAPI spec version: 1.0.0
 * Contact: nima.heydari79@yahoo.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.model.InlineResponse2006;
import io.swagger.client.model.InlineResponse401;
import io.swagger.client.model.Playlists;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PremiumUsersApi
 */
@Ignore
public class PremiumUsersApiTest {

    private final PremiumUsersApi api = new PremiumUsersApi();

    /**
     * Add a friend (request for friendship or accept the request) (just for premium users)
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void addFriendTest() throws ApiException {
        String friendUsername = null;
        InlineResponse2006 response = api.addFriend(friendUsername);

        // TODO: test validations
    }
    /**
     * Get playlists of a friend
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getFriendPlaylistsTest() throws ApiException {
        String friendUsername = null;
        Playlists response = api.getFriendPlaylists(friendUsername);

        // TODO: test validations
    }
    /**
     * Get username of users who have requested for friendship (just for premium users)
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getFriendRequestsTest() throws ApiException {
        List<String> response = api.getFriendRequests();

        // TODO: test validations
    }
    /**
     * Get username of friends (just for premium users)
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getFriendsTest() throws ApiException {
        List<String> response = api.getFriends();

        // TODO: test validations
    }
}
