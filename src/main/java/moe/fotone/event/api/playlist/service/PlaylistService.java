package moe.fotone.event.api.playlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.fotone.event.api.playlist.response.PlaylistResponse;
import moe.fotone.event.api.playlist.repository.PlaylistRepository;
import moe.fotone.event.api.playlist.repository.SongRepository;
import moe.fotone.event.api.playlist.repository.TagRepository;
import moe.fotone.event.api.playlist.repository.TaglistRepository;
import moe.fotone.event.api.playlist.request.PlaylistRequest;
import moe.fotone.event.api.user.repository.UserRepository;
import moe.fotone.event.common.auth.TwitterUser;
import moe.fotone.event.domain.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    private final PlaylistRepository playlistRepository;
    private final TagRepository tagRepository;
    private final TaglistRepository taglistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @Transactional(readOnly = true)
    public List<PlaylistResponse> getLatestPlaylists() {
        List<Playlist> playlists = playlistRepository.findTop7ByOrderByIdDesc();
        return playlists.stream()
                .map(playlist -> PlaylistResponse.fromEntity(playlist, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createPlaylist(TwitterUser twitterUser, PlaylistRequest request) throws IOException {
        if (request.getCover() == null || request.getCover().isEmpty()) {
            throw new IllegalArgumentException("커버 이미지가 필요합니다.");
        }

        String filePath = saveCoverImage(request.getCover());
        log.info("저장된 커버 이미지 경로: {}", filePath);

        User user = userRepository.findById(twitterUser.getUserId()).orElseThrow(IllegalAccessError::new);

        // 플레이리스트 엔티티 생성
        Playlist playlist = playlistRepository.save(Playlist.createPlaylist(
                request.getTitle(),
                request.getDescription(),
                filePath,
                user
        ));

        // 곡 정보 설정
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < request.getSongs().size(); i++) {
            var songRequest = request.getSongs().get(i);
            Song song = Song.createSong(songRequest.getTitle(), songRequest.getArtist(), songRequest.getBpm(), i + 1, playlist);
            songs.add(song);
        }
        songRepository.saveAll(songs);

        playlist.setSongs(songs);

        // 태그 정보 설정
        List<Taglist> taglists = request.getTags().stream().map(tagName -> {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });

            Taglist taglist = new Taglist();
            taglist.setPlaylist(playlist);
            taglist.setTag(tag);
            return taglist;
        }).collect(Collectors.toList());
        taglistRepository.saveAll(taglists);

        playlist.setTaglist(taglists);
        // DB 저장
        playlistRepository.save(playlist);

        log.info("플레이리스트 저장 완료: 제목={}, 설명={}, 태그={}, 곡 수={}",
                request.getTitle(), request.getDescription(), request.getTags(), request.getSongs().size());
        return playlist.getId();
    }

    private String saveCoverImage(String base64Image) throws IOException {
        if (!base64Image.startsWith("data:image")) {
            throw new IllegalArgumentException("유효하지 않은 이미지 데이터입니다.");
        }

        String[] parts = base64Image.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("올바른 Base64 이미지 형식이 아닙니다.");
        }

        byte[] imageBytes = Base64.decodeBase64(parts[1]);
        String fileName = UUID.randomUUID() + ".webp";  // 이미지 확장자 결정
        File file = new File(UPLOAD_DIR + fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        }

        return file.getAbsolutePath();
    }

    public PlaylistResponse getPlaylistById(Long id) {
        return PlaylistResponse.fromEntity(
                playlistRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Playlist not found")),
                true
        );
    }
}