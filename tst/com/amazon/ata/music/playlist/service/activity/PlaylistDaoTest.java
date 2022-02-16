package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlaylistDaoTest {

    @InjectMocks
    private PlaylistDao playlistDao;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private Playlist playlist;


    @BeforeEach
    void setUp () {
        initMocks(this);
    }

    @Test
    public void getPlaylist_existingPlaylistId_returnsPlaylist() {

        //GIVEN
        String id = "1234";

        //WHEN
        when(dynamoDBMapper.load(Playlist.class, id)).thenReturn(playlist);
        Playlist playlist1 = playlistDao.getPlaylist(id);


        //THEN
        Assertions.assertEquals(playlist1, playlist, "Expected playlistDao's getPlaylist to return the playlist");


    }

    @Test
    public void getPlaylist_nonExistentPlaylistId_throwsPlaylistNotFoundException() {

        //GIVEN
        String id = "1234";

        //WHEN
        when(dynamoDBMapper.load(Playlist.class, id)).thenReturn(null);

        //THEN
        assertThrows(PlaylistNotFoundException.class, () -> { playlistDao.getPlaylist(id); }, "When retrieving playlist with unknown Id, throw PlaylistNotFoundException.");

    }


    @Test
    public void savePlaylist_Playlist_verifySaveMethodCalledOnce() {

        //GIVEN

        //WHEN
        playlistDao.savePlaylist(playlist);

        //THEN
        verify(dynamoDBMapper, times(1)).save(playlist);

    }




}
