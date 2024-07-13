package com.planner.link;

import com.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponse regiterLink(LinkRequestPayload payload, Trip trip){
        Link rawlink = new Link(payload.title(), payload.URL(), trip);

        this.repository.save(rawlink);
        return new LinkResponse(rawlink.getId());
    }

}
