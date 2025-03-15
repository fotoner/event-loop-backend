package moe.fotone.event.api.playlist.repository;

import moe.fotone.event.domain.Playlist;
import moe.fotone.event.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    public Optional<Tag> findByName(String name);
}