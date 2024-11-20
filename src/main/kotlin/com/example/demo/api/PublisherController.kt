package com.example.demo.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.ResponseEntity
import java.io.IOException
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource

@RestController
@RequestMapping("/publishers")
class PublisherController(private val publisherService: PublisherService) {

    @PostMapping("/upload")
    fun uploadPublisher(
            @RequestPart("name") name: String,
            @RequestPart("file") file: MultipartFile
    ): ResponseEntity<PublisherService.Publisher> {
        return try {
            val publisher = publisherService.savePublisher(name, file)
            ResponseEntity(publisher, HttpStatus.CREATED)
        } catch (e: IOException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR) // Błąd, np. niepoprawny plik
        }
    }

    @GetMapping("/{id}")
    fun getPublisher(@PathVariable id: String): ResponseEntity<PublisherService.Publisher> {
        val publisher = publisherService.getPublisherById(id)
        return if (publisher != null) {
            ResponseEntity(publisher, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/{id}/file")
    fun getPublisherFile(@PathVariable id: String): ResponseEntity<Resource> {
        val fileData = publisherService.getPublisherFileById(id) ?: return ResponseEntity.notFound().build()
        val resource = ByteArrayResource(fileData)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file.pdf\"") // Sugeruje pobranie pliku
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Określa typ MIME jako PDF
                .contentLength(fileData.size.toLong()) // Ustawia długość pliku
                .body(resource)
    }
}