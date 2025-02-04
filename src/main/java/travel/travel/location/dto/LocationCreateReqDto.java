package travel.travel.location.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel.image.domain.Image;
import travel.travel.location.domain.Category;
import travel.travel.location.domain.Location;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationCreateReqDto {

    private String locationName;

    private double latitude;
    private double longitude;

    private String address;

    private LocalDate day;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Long planId;

    public Location toEntity(Plan plan, Image image,Integer newOrderNumber) {
        return Location.builder()
                .locationName(this.locationName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .address(this.address)
                .day(this.day)
                .scheduleOrder(newOrderNumber)
                .category(this.category)
                .plan(plan)
                .image(image)
                .build();
    }
}
