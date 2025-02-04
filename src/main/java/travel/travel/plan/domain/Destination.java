package travel.travel.plan.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;




@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name= "destination")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinationId;

    @Column(name = "destination_name", nullable = false)
    private String destinationName;

}
