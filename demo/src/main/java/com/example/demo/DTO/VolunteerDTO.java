package com.example.demo.DTO;

import com.example.demo.DummyObject.Project;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class VolunteerDTO {

    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String contact;
    private List<String> skills;
    private List<Project> projects;
    private Integer reputation;
    private List<String> interests;

}