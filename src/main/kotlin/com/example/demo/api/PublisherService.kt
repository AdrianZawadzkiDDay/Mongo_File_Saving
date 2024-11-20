package com.example.demo.api

import org.bson.types.Binary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.repository.MongoRepository

interface PublisherRepository : MongoRepository<PublisherService.Publisher, String>
@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    fun savePublisher(name: String, file: MultipartFile): Publisher {
        val fileBinary = Binary(file.bytes)
        val publisher = Publisher(name = name, file = fileBinary)
        return publisherRepository.save(publisher)
    }

    fun getPublisherFileById(id: String): ByteArray? {
        val publisher = publisherRepository.findById(id).orElse(null)
        return publisher?.file?.data // Odczytuje dane binarne pliku
    }

    fun getPublisherById(id: String): Publisher? {
        return publisherRepository.findById(id).orElse(null)
    }


    @Document(collection = "publishers") // Mapujemy tę klasę na kolekcję publishers w MongoDB
    data class Publisher(
            @Id val id: String? = null,  // Automatyczne generowanie ID w MongoDB
            @Field("name") val name: String, // Nazwa wydawcy
            @Field("file") val file: Binary  // Plik w formacie binarnym
    )
}