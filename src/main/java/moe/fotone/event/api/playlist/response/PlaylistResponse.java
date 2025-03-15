package moe.fotone.event.api.playlist.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moe.fotone.event.domain.Playlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {
    private Long id;
    private String title;
    private String description;
    private String cover;
    private List<String> tags;
    private List<SongResponse> songs;
    private String username;
    private String userId;
    private LocalDateTime createdAt;

    public static PlaylistResponse fromEntity(Playlist playlist, boolean includeSongs) {
        return new PlaylistResponse(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getCover(),
                playlist.getTaglist().stream()
                        .map(taglist -> taglist.getTag().getName())
                        .collect(Collectors.toList()),
                includeSongs ? playlist.getSongs().stream()
                        .map(SongResponse::fromEntity)
                        .collect(Collectors.toList()) : null,
                playlist.getUser().getName(),
                playlist.getUser().getId().toString(),
                playlist.getCreatedAt()
        );
    }
}
