package moe.fotone.event.api.playlist.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moe.fotone.event.domain.Song;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    private Long id;
    private String title;
    private String artist;
    private String bpm;
    private String genre;
    private Integer seq;

    public static SongResponse fromEntity(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getBpm(),
                song.getGenre(),
                song.getSeq()
        );
    }
}
