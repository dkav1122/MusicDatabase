package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreatePlaylistActivityTest {

    @Mock
    private PlaylistDao playlistDao;

    @Mock
    private ModelConverter modelConverter;

    private CreatePlaylistActivity createPlaylistActivity;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        createPlaylistActivity = new CreatePlaylistActivity(playlistDao);

    }

    @Test
    public void handleRequest_goodRequest_returnsCreatePlaylistResult() {

        //GIVEN
        String name = "name";
        String expectedCustomerId = "expectedCustomerId";
        List<String> tags = new ArrayList<>();
        Set<String> tagsFromPlaylist = new HashSet<>();

        CreatePlaylistRequest request = CreatePlaylistRequest.builder().withName(name).withCustomerId(expectedCustomerId).withTags(tags).build();
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCustomerId(expectedCustomerId);
        playlist.setTags(tagsFromPlaylist);

        PlaylistModel playlistModel = new PlaylistModel();
        playlistModel.setCustomerId(expectedCustomerId);
        playlistModel.setName(name);
        playlistModel.setId("test Id");
        playlistModel.setTags(tags);


        //WHEN

        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);
        when(playlistDao.savePlaylist(playlist)).thenReturn(playlist);
        when(modelConverter.toPlaylistModel(playlist)).thenReturn(playlistModel);


        //THEN
        assertEquals(playlistModel.getCustomerId(), result.getPlaylist().getCustomerId());
        assertEquals(playlistModel.getName(), result.getPlaylist().getName());

    }

    @Test
    public void handleRequest_invalidRequestName_throwsInvalidAtrributeValueException() {
        String invalidName = "name'";
        String expectedCustomerId = "expectedCustomerId";
        List<String> tags = new ArrayList<>();


        CreatePlaylistRequest request = CreatePlaylistRequest.builder().withName(invalidName).withCustomerId(expectedCustomerId).withTags(tags).build();

        assertThrows(InvalidAttributeValueException.class, () -> { createPlaylistActivity.handleRequest(request, null);
        }, "When given invalid name, throw Invalid Attribute Value Exception");


    }

    @Test
    public void handleRequest_invalidCustomerId_throwsInvalidAttributeValueException() {
        String name = "name";
        String invalidCustomerId = "expectedCustomerId''";
        List<String> tags = new ArrayList<>();


        CreatePlaylistRequest request = CreatePlaylistRequest.builder().withName(name).withCustomerId(invalidCustomerId).withTags(tags).build();

        assertThrows(InvalidAttributeValueException.class, () -> { createPlaylistActivity.handleRequest(request, null);
        }, "When given invalid customerId, throw Invalid Attribute Value Exception");

    }

    @Test
    public void handleRequest_requestHasEmptyTags_verifyTagsSizeIsZero() {
        String name = "name";
        String expectedCustomerId = "expectedCustomerId";
        List<String> tags = new ArrayList<>();
        Set<String> tagsFromPlaylist = new HashSet<>();

        CreatePlaylistRequest request = CreatePlaylistRequest.builder().withName(name).withCustomerId(expectedCustomerId).withTags(tags).build();
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCustomerId(expectedCustomerId);
        playlist.setTags(tagsFromPlaylist);

        PlaylistModel playlistModel = new PlaylistModel();
        playlistModel.setCustomerId(expectedCustomerId);
        playlistModel.setName(name);
        playlistModel.setId("test Id");
        playlistModel.setTags(tags);


        //WHEN

        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);
        when(playlistDao.savePlaylist(playlist)).thenReturn(playlist);
        when(modelConverter.toPlaylistModel(playlist)).thenReturn(playlistModel);


        //THEN
        assertTrue(result.getPlaylist().getTags().isEmpty());
        assertNotNull(result.getPlaylist().getTags());



    }

    @Test
    public void handleRequest_requestHasTags_verifyTagsRemainIntact() {

        String name = "name";
        String expectedCustomerId = "expectedCustomerId";
        List<String> tags = new ArrayList<>();
        Set<String> tagsFromPlaylist = new HashSet<>();
        tags.add("hello");
        tags.add("testing");

        CreatePlaylistRequest request = CreatePlaylistRequest.builder().withName(name).withCustomerId(expectedCustomerId).withTags(tags).build();
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCustomerId(expectedCustomerId);
        playlist.setTags(tagsFromPlaylist);

        PlaylistModel playlistModel = new PlaylistModel();
        playlistModel.setCustomerId(expectedCustomerId);
        playlistModel.setName(name);
        playlistModel.setId("test Id");
        playlistModel.setTags(tags);


        //WHEN

        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);
        when(playlistDao.savePlaylist(playlist)).thenReturn(playlist);
        when(modelConverter.toPlaylistModel(playlist)).thenReturn(playlistModel);


        //THEN
        assertFalse(result.getPlaylist().getTags().isEmpty());
        assertNotNull(result.getPlaylist().getTags());




    }


}
