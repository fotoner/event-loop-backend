package moe.fotone.event.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Tag extends BaseTimeEntity{
    @Id
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @OneToMany(targetEntity = Taglist.class, fetch = FetchType.LAZY)
    private List<Taglist> eventTags = new ArrayList<>();
}