package ru.gnidenko.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String url;
    private Long version;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Page parent;


    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Block> blocks;

    @ManyToMany(mappedBy = "pages")
    private Set<User> users;

    @ManyToMany(mappedBy = "pages")
    private Set<Tag> tags;

}
