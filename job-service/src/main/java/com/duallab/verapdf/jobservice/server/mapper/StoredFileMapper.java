package com.duallab.verapdf.jobservice.server.mapper;

import com.duallab.verapdf.jobservice.model.dto.StoredFileDTO;
import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import org.springframework.stereotype.Component;

/**
 * @author Maksim Bezrukov
 */
@Component
public class StoredFileMapper {

	public StoredFileDTO createDTOFromEntity(StoredFile entity) {
		StoredFileDTO res = new StoredFileDTO();
		res.setId(entity.getId());
		res.setChecksum(entity.getChecksum());
		res.setFileType(entity.getFileType());
		res.setFileSize(entity.getFileSize());
		res.setFileName(entity.getOriginalName());
		return res;
	}
}
