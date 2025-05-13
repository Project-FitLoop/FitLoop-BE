package fitloop.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private double price;
    private boolean isFree;
    private boolean includeShipping;
    private LocalDateTime createdAt;
    private List<String> imageUrls;
    private List<String> tags;
    private Long likeCount;
    private Integer rank;
}
