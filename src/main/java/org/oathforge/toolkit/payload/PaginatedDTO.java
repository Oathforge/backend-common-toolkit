package org.oathforge.toolkit.payload;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	public static <T> PaginatedDTO<T> build(List<T> elements, int page, int size, Long totalElements) {
		PaginationDTO responsePageDto = PaginationDTO.builder().pageNumber(page).pageSize(size)
				.totalElements(totalElements).build();

		return PaginatedDTO.<T>builder().elements(elements).pagination(responsePageDto).build();
	}
}