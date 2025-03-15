package moe.fotone.event.api.playlist.repository;

import jakarta.validation.constraints.NotNull;
import moe.fotone.event.domain.Playlist;
import moe.fotone.event.domain.SocialType;
import moe.fotone.event.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findTop7ByOrderByIdDesc();
}