package feedbackservice.feedback

import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class FeedbackRestController(
    private val feedbackRepository: FeedbackRepository
) {
    @PostMapping("/feedback")
    fun uploadFeedback(@RequestBody request: FeedbackUploadRequest): ResponseEntity<Void> {
        val document = FeedbackDocument(
            rating = request.rating,
            feedback = request.feedback,
            customer = request.customer,
            product = request.product,
            vendor = request.vendor
        )

        val id = feedbackRepository.save(document).id

        return ResponseEntity.created(URI.create("/feedback/$id")).build()
    }

    @GetMapping("/feedback")
    fun getAllSorted(): List<FeedbackDocument> {
        return feedbackRepository.findAll(Sort.by("id").descending())
    }

    @GetMapping("/feedback/{id}")
    fun getOne(@PathVariable id: String): ResponseEntity<FeedbackDocument> {
        return ResponseEntity.of(feedbackRepository.findById(id))
    }

}
