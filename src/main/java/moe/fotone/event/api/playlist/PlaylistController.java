package moe.fotone.event.api.playlist;


import lombok.RequiredArgsConstructor;
import moe.fotone.event.api.playlist.service.PlaylistService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

}
