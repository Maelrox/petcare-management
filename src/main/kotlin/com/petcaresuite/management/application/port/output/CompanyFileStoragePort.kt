package com.petcaresuite.management.application.port.output

import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface CompanyFileStoragePort {

    fun store(file: MultipartFile, companyId: Long): Path

}