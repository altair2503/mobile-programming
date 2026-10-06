interface Borrowable {
    val title: String
    val available: Boolean
    fun borrow(): Boolean
    fun describe(): String
}
