package crdt

/*
* Implement this class and provide current milliseconds or other applicable time value for the project
*/
interface TimeAdapter {
    fun now():  Long
}
