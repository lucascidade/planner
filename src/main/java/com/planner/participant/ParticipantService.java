package com.planner.participant;

import com.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private  ParticipantRepository participantRepository;
    public void registerParticipantToEvents(List<String> participantsToInvite, Trip trip){
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();
        participantRepository.saveAll(participants);
        System.out.println(participants.get(0).getId());
    }
    public void triggerConfirmationEmaiLToParticipant(UUID tripId){}

    public void triggerConfirmationEmaiLToParticipants(String email){}

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip tip){
        Participant participant = new Participant(email, tip);
        this.participantRepository.save(participant);

        return new ParticipantCreateResponse(participant.getId());
    }

    public List<ParticipantData> getAllParticipantsFromEvents(UUID tripId){
        return this.participantRepository.findByTripId(tripId)
                .stream().map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
