package com.petcaresuite.management.infrastructure.persistence.adapter

import com.petcaresuite.management.application.port.output.CompanyFileStoragePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class PatientFileStorageAdapter: CompanyFileStoragePort {

    @Value("\${file.upload-dir}")
    private lateinit var uploadDir: String

    override fun store(file: MultipartFile, companyId: Long): Path {
        val targetDir = Paths.get(uploadDir, companyId.toString())
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir)
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to create directory: $targetDir", e)
        }
        val targetLocation = targetDir.resolve(file.originalFilename!!)
        try {
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, targetLocation)
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to store file: ${file.originalFilename} in $targetDir", e)
        }
        return targetLocation
    }

}