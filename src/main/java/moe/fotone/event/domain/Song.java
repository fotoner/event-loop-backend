package moe.fotone.event.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Song extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column
    private String bpm;

    @Column
    private Integer seq;

    @Column
    private String genre;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    public static Song createSong(String title, String artist, String bpm, int seq, String genre, Playlist playlist){
        return Song.builder()
                .title(title)
                .artist(artist)
                .bpm(bpm)
                .seq(seq)
                .genre(genre)
                .playlist(playlist)
                .build();
    }
}