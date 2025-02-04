package travel.travel.image.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import travel.travel.image.dto.ImageResDto;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@DynamicInsert
@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    public ImageResDto fromEntity() {
        return ImageResDto.builder()
                .imageId(this.imageId)
                .imageUrl(this.imageUrl)
                .build();
    }
}
