package moe.fotone.event.api.playlist.request;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistRequest {
    private String title;
    private String description;
    private String cover;  // Base64로 인코딩된 이미지
    private List<String> tags;
    private List<SongRequest> songs;

    @Data
    public static class SongRequest {
        private String title;
        private String artist;
        private String bpm;
        private String genre;
    }
}