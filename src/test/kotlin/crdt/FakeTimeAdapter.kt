package crdt

class FakeTimeAdapter : TimeAdapter {
    private var now = 0L
    override fun now() = now

    fun tick() {
        now++
    }
}