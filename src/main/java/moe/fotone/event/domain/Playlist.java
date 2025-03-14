package moe.fotone.event.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;


@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor
public class Playlist extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    @NotNull
    private String title;

    @Column
    private String description;

    @Column
    private String cover;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(targetEntity = Song.class, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Song> songs;

    @OneToMany(targetEntity = Taglist.class)
    private List<Taglist> taglist;
}
