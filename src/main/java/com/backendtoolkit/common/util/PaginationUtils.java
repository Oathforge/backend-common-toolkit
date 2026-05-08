package com.backendtoolkit.common.util;

import java.util.List;

import com.backendtoolkit.common.payload.PaginatedDTO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtils {

	/**
	 * Creates a {@link PaginatedDTO} from an in-memory list using the requested
	 * page and size.
	 * <p>
	 * This helper is useful when the source data does not come from Spring Data
	 * pagination directly, but you still want to return the same response shape
	 * exposed by the toolkit pagination DTOs.
	 *
	 * @param list full list to paginate
	 * @param page zero-based page number
	 * @param size requested page size
	 * @return paginated DTO for the requested slice
	 * @param <T> element type
	 */
	public <T> PaginatedDTO<T> createPaginatedDto(List<T> list, int page, int size) {
		long totalElements = list.size();

		int fromIndex = Math.min(page * size, list.size());
		int toIndex = Math.min((page * size) + size, list.size());

		List<T> paginatedList = list.subList(fromIndex, toIndex);

		return PaginatedDTO.build(paginatedList, page, size, totalElements);
	}
}
