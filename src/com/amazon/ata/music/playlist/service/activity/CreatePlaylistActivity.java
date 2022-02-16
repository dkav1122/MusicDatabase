package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the CreatePlaylistActivity for the MusicPlaylistService's CreatePlaylist API.
 *
 * This API allows the customer to create a new playlist with no songs.
 */
public class CreatePlaylistActivity implements RequestHandler<CreatePlaylistRequest, CreatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;


    //do i nned a zero argument constructor
    /**
     * Instantiates a new CreatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlists table.
     */

    @Inject
    public CreatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }



    /**
     * This method handles the incoming request by persisting a new playlist
     * with the provided playlist name and customer ID from the request.
     * <p>
     * It then returns the newly created playlist.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     *
     * @param createPlaylistRequest request object containing the playlist name and customer ID
     *                              associated with it
     * @return createPlaylistResult result object containing the API defined {@link PlaylistModel}
     */

//    This API must create the playlist with an empty list so we can later add songs to it through the subsequent APIs.

//    We do not want to unnecessarily store duplicate tags, so we will choose a data structure that can provide us this behavior.

//    The music playlist client will provide a non-empty list of tags or null in the request to indicate no tags were provided.
//            Note: Unlike the playlist name, tags do not have any character restrictions.
    //if null provide empty set
    @Override
    public CreatePlaylistResult handleRequest(final CreatePlaylistRequest createPlaylistRequest, Context context) {
        log.info("Received CreatePlaylistRequest {}", createPlaylistRequest);

        if(MusicPlaylistServiceUtils.isValidString(createPlaylistRequest.getName()) != true || MusicPlaylistServiceUtils.isValidString(createPlaylistRequest.getCustomerId()) != true) {
            throw new InvalidAttributeValueException("playlist name or customerID cannot have invalid characters.");
        }

       // createPlaylistRequest(MusicPlaylistServiceUtils.generatePlaylistId());
        //save all data
        //not null --> set tags

        Set<String> tagsAsSet = new HashSet<>();
        Playlist playlist = new Playlist();

        //if not null, copy tags list to set

        if (createPlaylistRequest.getTags() != null) {
        //    tagsAsSet = new HashSet<>(createPlaylistRequest.getTags());
            for (String tag : createPlaylistRequest.getTags()) {
                    tagsAsSet.add(tag);
            }
        //add set of tags to playlist with setTags
        playlist.setTags(tagsAsSet);
        }
        playlist.setId(MusicPlaylistServiceUtils.generatePlaylistId());
        playlist.setName(createPlaylistRequest.getName());
        playlist.setCustomerId(createPlaylistRequest.getCustomerId());
        //is there no song count?
        //playlist.setSongCount(createPlaylistRequest.);
        //@DIxon this is new
        PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);


        playlistDao.savePlaylist(playlist);
        return CreatePlaylistResult.builder()
                .withPlaylist(playlistModel) //this is new
                .build();
    }
}
