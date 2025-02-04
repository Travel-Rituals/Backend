package travel.travel.location.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.image.domain.Image;
import travel.travel.image.dto.ImageResDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name= "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String locationName;

    private double latitude;
    private double longitude;

    private String address;

    private LocalDate day;
    private Integer scheduleOrder;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    public LocationResDto fromEntity() {
        ImageResDto imageResDto = ImageResDto.builder()
                .imageId(image.getImageId())
                .imageUrl(image.getImageUrl())
                .build();

        return LocationResDto.builder()
                .locationId(this.locationId)
                .locationName(this.locationName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .address(this.address)
                .day(this.day)
                .scheduleOrder(this.scheduleOrder)
                .category(this.category)
                .image(imageResDto)
                .build();
    }

    public void updateLocation(Location location) {
        this.scheduleOrder = location.getScheduleOrder();
    }
}
