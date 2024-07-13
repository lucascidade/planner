package com.planner.activity;

import com.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse saveActivity(ActivityRequestPayload payload, Trip trip) {
        Activity activity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.activityRepository.save(activity);

        return new ActivityResponse(activity.getId());
    }
    public List<ActivityData> getAllActivities(UUID id) {
        return this.activityRepository.findByTripId(id).stream().map(activity -> new ActivityData(activity.getId(),
                activity.getTitle(), activity.getOccursAt())).toList();

    }
}
