// This will be our Retrofit service for connecting to backend
interface VQAApiService {
    @Multipart
    @POST("http://localhost:8000/answer")
    fun askQuestion(
        @Part image: MultipartBody.Part,
        @Part question: RequestBody
    ): Call<VQAResponse>
}

data class VQAResponse(
    val answer: String,
    val status: String,
    val question: String
)