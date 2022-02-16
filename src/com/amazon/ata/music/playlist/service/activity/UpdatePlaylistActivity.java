package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);


        //check playlist name and customerId for invalid strings
        if (!MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getName()) || !MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getCustomerId())) {
            throw new InvalidAttributeValueException(String.format("Playlist name %s and/or CustomerId : %s have invalid characters", updatePlaylistRequest.getName(), updatePlaylistRequest.getCustomerId()));
        }

        //retrieve playlist from data base based on partition-key: id
        Playlist playlist;
        try {
            playlist = playlistDao.getPlaylist(updatePlaylistRequest.getId());
        } catch (PlaylistNotFoundException e) {
            throw new PlaylistNotFoundException(String.format("Playlist not found for playlist id: %s", updatePlaylistRequest.getId()));
        }

        if (!updatePlaylistRequest.getCustomerId().equals(playlist.getCustomerId())) {
            throw new InvalidAttributeChangeException(String.format("Playlist customer ids do not match. Request id: %s , playlistCustomerId: %s", updatePlaylistRequest.getCustomerId(), playlist.getCustomerId()));
        }

        //update and save playlist
        //for now only update playlist name, don't modify any other attributes 2/14
        playlist.setName(updatePlaylistRequest.getName());
        playlistDao.savePlaylist(playlist);


        //create playlistModel with playlist data
        PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);

        return UpdatePlaylistResult.builder()
                .withPlaylist(playlistModel)
                .build();
    }
}
