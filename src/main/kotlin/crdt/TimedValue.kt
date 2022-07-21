package crdt

/*
* A class to associate time with corresponding value
*/
data class TimedValue<T>  (
    val value: T?,
    val timestamp: Long
) : Comparable<TimedValue<T>> {

    /*
    * Implements Comparable interface
    */
    override fun compareTo(other: TimedValue<T>): Int {
        return when {
            timestamp == other.timestamp -> 0
            timestamp < other.timestamp -> -1
            else -> +1
        }
    }
}