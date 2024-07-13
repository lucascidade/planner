package com.planner.activities;

import com.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse saveActivity(ActivityRequestPayload payload, Trip trip) {
        Activity activity = new Activity(payload.title(), payload.occursAt(), trip);

        this.activityRepository.save(activity);

        return new ActivityResponse(activity.getId()    );

    }
}
