package com.example.demo.Controller;

import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.DummyObject.Project;
import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling DummyEntity requests.
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api")
public class VolunteerController {
    @Autowired
    private VolunteerService volunteerService;

    /**
     * Receives data from logic part of application and saves received data to database
     *
     * @return The ResponseEntity object which contains saved entity
     */
    @PostMapping(value = "/volunteer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Volunteer> postVolunteer(@RequestBody @Valid Volunteer volunteer) {

        if (volunteer != null) {

            Volunteer savedVolunteer = volunteerService.saveVolunteer(volunteer);

            return new ResponseEntity<>(savedVolunteer, HttpStatus.CREATED);
        } else {
            throw new IllegalArgumentException("Posted volunteer cannot be null");
        }
    }

    /**
     * Retrieves estimation data from database based on parameters provided for filtering.
     */
    @GetMapping(value = "/volunteers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VolunteerDTO>> getVolunteers() {

        List<Volunteer> foundVolunteers = volunteerService.searchVolunteers();

        if (foundVolunteers != null) {

            List<VolunteerDTO> volunteerDTOS = foundVolunteers.stream()
                    .map(VolunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(volunteerDTOS, HttpStatus.OK);

        } else {

            throw new EntityNotFoundException("Requested volunteers could not be found");
        }
    }

    @GetMapping(value = "/volunteer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getVolunteer(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        return new ResponseEntity<>(VolunteerMapper.mapVolunteerToDTO(foundVolunteer), HttpStatus.OK);
    }

    @GetMapping(value = "/volunteer/{id}/skills", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getSkills(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        return new ResponseEntity<>(foundVolunteer.getSkills(), HttpStatus.OK);

    }

    @GetMapping(value = "/volunteer/{id}/interests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getInterests(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        return new ResponseEntity<>(foundVolunteer.getInterests(), HttpStatus.OK);
    }

    @GetMapping(value = "/volunteer/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getProjects(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        return new ResponseEntity<>(foundVolunteer.getParticipatingProjects(), HttpStatus.OK);
    }

    //URL to change!
    @GetMapping(value = "/volunteer/{id}/ownedProjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getOwnedProjects(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        return new ResponseEntity<>(foundVolunteer.getOwnedProjects(), HttpStatus.OK);
    }

    /**
     * Updates an entity in database
     *
     * @param id An ID value of updated object
     * @return The ResponseEntity object containing updated object
     */
    @PatchMapping(value = "/volunteer/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> updateVolunteer(@PathVariable Long id, @RequestBody Volunteer volunteer) {
        if (id != null) {

            Volunteer editedVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Requested entity was not found."));

            VolunteerMapper.mapPropertiesToVolunteer(volunteer, editedVolunteer);
            volunteerService.saveVolunteer(editedVolunteer);
            VolunteerDTO returnedDTO = VolunteerMapper.mapVolunteerToDTO(editedVolunteer);

            return new ResponseEntity<>(returnedDTO, HttpStatus.OK);
        } else {

            throw new IllegalArgumentException("An ID of requested volunteer to patch cannot be null.");
        }
    }

    /**
     * Deletes an entity from database
     *
     * @param id An ID value of deleted object
     * @return The String with deletion message
     */
    @DeleteMapping(value = "/volunteer/{id}")
    public ResponseEntity<VolunteerDTO> deleteEntity(@PathVariable Long id) {

        if (id != null) {

            Volunteer volunteerToDelete = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer to delete could not be found."));
            VolunteerDTO returnedDTO = VolunteerMapper.mapVolunteerToDTO(volunteerToDelete);
            volunteerService.deleteVolunteer(volunteerToDelete);
            return new ResponseEntity<>(returnedDTO, HttpStatus.OK);
        } else {

            throw new IllegalArgumentException("An ID of requested volunteer to patch cannot be null.");
        }


    }
}