package travel.travel.common.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonErrorDto {
    private int status;
    private String error;

    public CommonErrorDto(HttpStatus status, String error) {
        this.status = status.value();
        this.error = error;
    }
}
