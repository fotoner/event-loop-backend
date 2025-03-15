package moe.fotone.event.api.playlist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.fotone.event.api.BaseResponse;
import moe.fotone.event.api.playlist.request.PlaylistRequest;
import moe.fotone.event.api.playlist.response.PlaylistResponse;
import moe.fotone.event.api.playlist.service.PlaylistService;
import moe.fotone.event.common.auth.TwitterUser;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
@Slf4j
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping("/latest")
    public BaseResponse<List<PlaylistResponse>> getLatestPlaylists(){
        return BaseResponse.OK(playlistService.getLatestPlaylists());
    }

    @GetMapping("/page")
    public BaseResponse<Page<PlaylistResponse>> getPlaylistsPage(@RequestParam(value="page", defaultValue="0") int page){
        return BaseResponse.OK(playlistService.getPlaylistPage(page));
    }

    @PostMapping("/create")
    public BaseResponse<Long> createPlaylist(
            @AuthenticationPrincipal TwitterUser user,
            @RequestBody PlaylistRequest request
    ) throws IOException {
        Long id = playlistService.createPlaylist(user, request);
        return BaseResponse.OK(id);
    }

    @GetMapping("/{id}")
    public BaseResponse<PlaylistResponse> getPlaylistById(@PathVariable Long id) {
        return BaseResponse.OK(playlistService.getPlaylistById(id));
    }

    @GetMapping("/user/{id}")
    public BaseResponse<List<PlaylistResponse>> getPlaylistsByUser(@PathVariable Long id) {
        return BaseResponse.OK(playlistService.getPlaylistsByUser(id));
    }
}