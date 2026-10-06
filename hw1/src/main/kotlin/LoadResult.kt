sealed class LoadResult {
    data class Success(val added: Int) : LoadResult()
    data class Failure(val message: String) : LoadResult()
}
