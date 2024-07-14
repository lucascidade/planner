package com.planner.trip;

import com.planner.activity.*;
import com.planner.link.*;
import com.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {


    @Autowired
    private LinkService linkService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ParticipantService service;

    @Autowired
    private TripRepository repository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);
        this.repository.save(newTrip);
        this.service.registerParticipantToEvents(payload.emails_to_invite(), newTrip);
        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    //TRIPS
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> tripUpdate(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {

        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()) {
            Trip updatedTrip = trip.get();
            updatedTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setEndsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setDestination(payload.destination());
            this.repository.save(updatedTrip);
            return ResponseEntity.ok(updatedTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> tripConfirm(@PathVariable UUID id){

        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isPresent()){
            Trip tripToConfirm = trip.get();
            tripToConfirm.setIsConfirmed(true);

            this.repository.save(tripToConfirm);
            service.triggerConfirmationEmaiLToParticipant(id);

            return ResponseEntity.ok(tripToConfirm);
        }
        return ResponseEntity.notFound().build();
    }


    //ACTIVITY
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {

        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();

            ActivityResponse activityResponse = this.activityService.saveActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityData = this.activityService.getAllActivities(id);
        return ResponseEntity.ok(activityData);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isPresent()) {
            Trip rawTrip = trip.get();
            ParticipantCreateResponse response = this.service.registerParticipantToEvent(payload.email(), rawTrip);
            if(rawTrip.getIsConfirmed()) this.service.triggerConfirmationEmaiLToParticipants(payload.email());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/{tripId}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID tripId){
        List<ParticipantData> participants = this.service.getAllParticipantsFromEvents(tripId);
        return ResponseEntity.ok(participants);

    }

    //LINK

    @PostMapping("/{tripId}/link")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID tripId, @RequestBody LinkRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(tripId);
        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);
            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("{tripId}/links")
    public ResponseEntity<List<LinkData>> getAllLinksFromTrip (@PathVariable UUID tripId){
        List<LinkData> links = this.linkService.getAllLinksFromActivity(tripId);
        return ResponseEntity.ok(links);

    }


}
