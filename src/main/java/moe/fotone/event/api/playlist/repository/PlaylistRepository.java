package moe.fotone.event.api.playlist.repository;

import moe.fotone.event.domain.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findTop7ByOrderByIdDesc();

    Page<Playlist> findAll(Pageable pageable);

    List<Playlist> findByUserIdOrderByCreatedAtDesc(Long id);
}