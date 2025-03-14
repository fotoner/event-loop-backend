package moe.fotone.event.api.user.repository;

import jakarta.validation.constraints.NotNull;
import moe.fotone.event.domain.SocialType;
import moe.fotone.event.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialIdAndActivatedIsTrue(String socialId);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}