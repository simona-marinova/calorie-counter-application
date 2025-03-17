package app.activity.client.dto;

import lombok.Builder;
import lombok.Data;


import java.util.UUID;

@Data
@Builder
public class ActivityRequest {

    private UUID userId;


    private String activityType;


    private int duration;


}
