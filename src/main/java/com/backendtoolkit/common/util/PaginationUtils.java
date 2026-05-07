package com.backendtoolkit.common.util;

import java.util.List;

import com.backendtoolkit.common.payload.PaginatedDTO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtils {

	public <T> PaginatedDTO<T> createPaginatedDto(List<T> list, int page, int size) {
		long totalElements = list.size();

		int fromIndex = Math.min(page * size, list.size());
		int toIndex = Math.min((page * size) + size, list.size());

		List<T> paginatedList = list.subList(fromIndex, toIndex);

		return PaginatedDTO.build(paginatedList, page, size, totalElements);
	}
}
