package moe.fotone.event.api.playlist.repository;

import moe.fotone.event.domain.Playlist;
import moe.fotone.event.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}