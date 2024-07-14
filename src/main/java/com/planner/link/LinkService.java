package com.planner.link;

import com.planner.activity.ActivityData;
import com.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip){
        Link rawlink = new Link(payload.title(), payload.url(), trip);
        this.repository.save(rawlink);
        return new LinkResponse(rawlink.getId());
    }

    public List<LinkData> getAllLinksFromActivity(UUID id) {
        return this.repository.findByTripId(id).stream().map(link -> new LinkData(link.getId(),
                link.getTitle(), link.getUrl())).toList();

    }

}
