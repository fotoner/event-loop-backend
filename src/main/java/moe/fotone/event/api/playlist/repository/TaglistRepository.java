package moe.fotone.event.api.playlist.repository;

import moe.fotone.event.domain.Tag;
import moe.fotone.event.domain.Taglist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaglistRepository extends JpaRepository<Taglist, Long> {
}
