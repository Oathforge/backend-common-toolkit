package org.oathforge.toolkit.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination metadata returned together with a paginated response.
 * <p>
 * This DTO represents the current page number, requested page size, and total
 * number of available elements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDTO implements Serializable {
	private static final long serialVersionUID = -7969897162351176449L;
	private Integer pageNumber;
	private Integer pageSize;
	private Long totalElements;
}
