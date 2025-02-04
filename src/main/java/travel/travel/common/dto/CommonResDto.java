package travel.travel.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class CommonResDto {
    private int status;
    private String message;
    private Object result;

    public CommonResDto(HttpStatus status, String message, Object result) {
        this.status = status.value();
        this.message = message;
        this.result = result;
    }
}
