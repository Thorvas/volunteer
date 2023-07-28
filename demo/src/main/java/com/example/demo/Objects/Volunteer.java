package com.example.demo.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "volunteer")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String contact;

    @ElementCollection
    private List<String> skills;


    @ManyToMany(mappedBy = "projectVolunteers")
    private List<Project> participatingProjects;

    @OneToMany(mappedBy = "ownerVolunteer")
    private List<Project> ownedProjects;

    @Column(name = "reputation")
    private Integer reputation;

    @ElementCollection
    private List<String> interests;

    @OneToOne(mappedBy = "referencedVolunteer")
    private UserData userData;

}