package org.oathforge.toolkit.payload;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for paginated API results.
 * <p>
 * It contains the current page elements together with the pagination metadata
 * that clients need to navigate the result set.
 *
 * @param <T> element type exposed by the API
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedDTO<T> implements Serializable {

	private static final long serialVersionUID = 5562697976368303742L;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<T> elements;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private PaginationDTO pagination;

	/**
	 * Creates a paginated response from the provided page elements and metadata.
	 *
	 * @param elements elements included in the current page
	 * @param page zero-based page number
	 * @param size requested page size
	 * @param totalElements total number of available elements across all pages
	 * @return paginated response DTO
	 * @param <T> element type
	 */
	public static <T> PaginatedDTO<T> build(List<T> elements, int page, int size, Long totalElements) {
		PaginationDTO responsePageDto = PaginationDTO.builder().pageNumber(page).pageSize(size)
				.totalElements(totalElements).build();

		return PaginatedDTO.<T>builder().elements(elements).pagination(responsePageDto).build();
	}
}
