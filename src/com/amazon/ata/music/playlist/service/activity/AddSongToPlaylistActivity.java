package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AddSongToPlaylistActivity for the MusicPlaylistService's AddSongToPlaylist API.
 *
 * This API allows the customer to add a song to their existing playlist.
 */
public class AddSongToPlaylistActivity implements RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;

    /**
     * Instantiates a new AddSongToPlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     * @param albumTrackDao AlbumTrackDao to access the album_track table.
     */
    @Inject
    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
    }



    /**
     * This method handles the incoming request by adding an additional song
     * to a playlist and persisting the updated playlist.
     * <p>
     * It then returns the updated song list of the playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the album track does not exist, this should throw an AlbumTrackNotFoundException.
     *
     * @param addSongToPlaylistRequest request object containing the playlist ID and an asin and track number
     *                                 to retrieve the song data
     * @return addSongToPlaylistResult result object containing the playlist's updated list of
     *                                 API defined {@link SongModel}s
     */
    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest, Context context) {
        log.info("Received AddSongToPlaylistRequest {} ", addSongToPlaylistRequest);

        AlbumTrack albumTrack;
        Playlist playlist;

//        String playlistID = addSongToPlaylistRequest.getId();
//        String asinForSong = addSongToPlaylistRequest.getAsin();
//        int songTrackNumber = addSongToPlaylistRequest.getTrackNumber();

        try {
            //get playlist we want to add to
            playlist = playlistDao.getPlaylist(addSongToPlaylistRequest.getId());
        } catch (PlaylistNotFoundException e) {
            throw new PlaylistNotFoundException("Playlist for request not found", e);
        }

        try {
            //get single song from album
           albumTrack = albumTrackDao.getAlbumTrack(addSongToPlaylistRequest.getAsin(), addSongToPlaylistRequest.getTrackNumber());
        } catch (AlbumTrackNotFoundException e) {
            throw new AlbumTrackNotFoundException("Error finding track number and album", e);
        }

        //add album track to playlist
        if (playlist.getSongList() == null) {
            List<AlbumTrack> freshList = new ArrayList<>();
            playlist.setSongList(freshList);
        }

        //add album track to playlist
        playlist.getSongList().add(albumTrack);
        //i havent done song count
        int songCount = playlist.getSongCount();
        playlist.setSongCount(songCount + 1);

        //save playlist with updated song list
        playlist = playlistDao.savePlaylist(playlist);



        //convert all album tracks in playlist into Song Models
//        List<SongModel> playlistTracksAsSongModels = new ArrayList<>();
//        for(AlbumTrack track : playlist.getSongList()) {
//            playlistTracksAsSongModels.add(new ModelConverter().toSongModel(track));
//        }

        //turn playlist into song model before loading it in playlist dao


        return AddSongToPlaylistResult.builder()
                .withSongList(new ModelConverter().toSongModelList(playlist.getSongList()))
                .build();
    }
}
