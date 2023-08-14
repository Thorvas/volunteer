package com.example.demo.Request;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectService;
import com.example.demo.Volunteer.VolunteerDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for requests
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RequestService requestService;

    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";
    private final String REQUEST_NOT_FOUND_MESSAGE = "Request could not be found.";

    private final String ROOT_LINK = "root";
    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        return linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel(ROOT_LINK);
    }

    /**
     * POST endpoint for requests. It allows volunteers to create requests for joining to projects
     *
     * @param id Long id value of project that volunteer wants to apply for
     * @return JSON response containing created request
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> createRequest(@RequestParam("projectId") Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        RequestDTO requestDTO = requestService.createRequest(foundProject);

        requestDTO.add(rootLink());

        return new ResponseEntity<>(requestDTO, HttpStatus.CREATED);

    }

    /**
     * GET endpoint for requests. It retrieves list of all stored requests
     *
     * @return JSON response containing retrieved requests
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<RequestDTO>> getAllRequests() {

        List<RequestDTO> requestDTOs = requestService.searchAllRequests().orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        Link selfLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withSelfRel();

        CollectionModel<RequestDTO> resource = CollectionModel.of(requestDTOs, selfLink);

        resource.add(selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for requests. It retrieves specific request based on id parameter
     *
     * @param id Long id value of returned request
     * @return JSON response containing returned request
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> getSpecificRequest(@PathVariable Long id) {

        RequestDTO requestDTO = requestService.searchRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        requestDTO.add(rootLink());

        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for requests. It serves as emergency endpoint for administrators
     *
     * @param id Long id value of deleted request
     * @return JSON response containing deleted request
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> deleteRequest(@PathVariable Long id) {

        VolunteerRequest request = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));
        RequestDTO requestDTO = requestService.deleteRequest(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));

        requestDTO.add(rootLink());
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is sender of request. It can be accessed only by sender itself or receiver of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request sender
     */
    @GetMapping(value = "/{id}/sender", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestSender(@PathVariable Long id) {

        VolunteerRequest request = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        VolunteerDTO volunteerDTO = requestService.getRequestSender(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));
        Link selfLink = linkTo(methodOn(RequestController.class).getRequestSender(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is receiver of request. It can be accessed only by receiver itself or sender of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request receiver
     */
    @GetMapping(value = "/{id}/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestReceiver(@PathVariable Long id) {

        VolunteerRequest request = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        VolunteerDTO volunteerDTO = requestService.getRequestReceiver(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));
        Link selfLink = linkTo(methodOn(RequestController.class).getRequestReceiver(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for project that is associated with request
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing project associated with request
     */
    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getRequestProject(@PathVariable Long id) {

        VolunteerRequest request = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        ProjectDTO projectDTO = requestService.getRequestedProject(request).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Link selfLink = linkTo(methodOn(RequestController.class).getRequestProject(id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for accepting the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of accepted request
     * @return JSON response containing accepted request
     */
    @PatchMapping(value = "/{id}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> acceptRequest(@PathVariable("id") Long requestId) {

        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        RequestDTO requestDTO = requestService.acceptRequest(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));
        Link selfLink = linkTo(methodOn(RequestController.class).acceptRequest(requestId)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for declining the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of declined request
     * @return JSON response containing declined request
     */
    @PatchMapping(value = "/{id}/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> declineRequest(@PathVariable("id") Long requestId) {

        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        RequestDTO requestDTO = requestService.declineRequest(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));
        Link selfLink = linkTo(methodOn(RequestController.class).declineRequest(requestId)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }
}

