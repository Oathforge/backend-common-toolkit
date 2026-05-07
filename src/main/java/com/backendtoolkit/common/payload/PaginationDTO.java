package com.backendtoolkit.common.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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