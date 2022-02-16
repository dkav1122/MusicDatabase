package com.amazon.ata.music.playlist.service.converters;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;

import java.util.ArrayList;
import java.util.List;

public class ModelConverter {
    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {
        //dont put business logic in model conversion
        //validate requests in one place aka handleReq

    List<String> tagsList = new ArrayList<>();
    if (playlist.getTags() == null) {
        //tried making change to empty list, didnt solve lambda null pointer 2/10/22
        tagsList = null;
    } else {
        tagsList = new ArrayList<>(playlist.getTags());
    }



        return PlaylistModel.builder()
            .withId(playlist.getId()).withName(playlist.getName()).withCustomerId(playlist.getCustomerId())
                .withTags(tagsList).build();

    //got rid of song count method...songCount wasnt being added in createPlaylistActivity
    }
}
