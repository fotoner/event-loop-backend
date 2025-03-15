package moe.fotone.event.api.user.service;

import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
    }
}
