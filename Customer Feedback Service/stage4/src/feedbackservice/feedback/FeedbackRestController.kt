package feedbackservice.feedback

import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun getAllSorted(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) perPage: Int?,
        @RequestParam(required = false) rating: Int?,
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) product: String?,
        @RequestParam(required = false) vendor: String?,
    ): FeedbackPageResponse {
        val resolvedPage = resolvePage(page)
        val resolvedPageSize = resolvePerPage(perPage)

        val sortBy = Sort.by("id").descending()

        val probe = FeedbackDocument(
            rating = rating,
            customer = customer,
            product = product,
            vendor = vendor,
            feedback = null
        )

        val example = Example.of(probe)
        val pageRequest = PageRequest.of(resolvedPage - 1, resolvedPageSize, sortBy)
        val fetchedPage = feedbackRepository.findAll(example, pageRequest)

        return FeedbackPageResponse(
            totalDocuments = fetchedPage.totalElements,
            firstPage = fetchedPage.isFirst,
            lastPage = fetchedPage.isLast,
            documents = fetchedPage.content
        )
    }

    @GetMapping("/feedback/{id}")
    fun getOne(@PathVariable id: String): ResponseEntity<FeedbackDocument> {
        return ResponseEntity.of(feedbackRepository.findById(id))
    }

    private fun resolvePage(page: Int?): Int {
        return if (page == null || page < 1) {
            DEFAULT_PAGE
        } else {
            page
        }
    }

    private fun resolvePerPage(perPage: Int?): Int {
        return if (perPage == null || perPage < MIN_PER_PAGE || perPage > MAX_PER_PAGE) {
            DEFAULT_PER_PAGE
        } else {
            perPage
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 1
        private const val DEFAULT_PER_PAGE = 10
        private const val MIN_PER_PAGE = 5
        private const val MAX_PER_PAGE = 20
    }
}
