interface Borrowable {
    val title: String
    val available: Boolean
    fun borrow(): Boolean
    fun returnItem(): Boolean
    fun describe(): String
}
