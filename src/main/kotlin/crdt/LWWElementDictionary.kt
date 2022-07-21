package crdt

class LWWElementDictionary<K, V>(
    private val time: TimeAdapter,
    private val adds: MutableMap<K, TimedValue<V>> = mutableMapOf(),
    private val removes: MutableMap<K, TimedValue<V>> = mutableMapOf()) {

    /**
     * Lookup key-value Pair by its key.
     * The lookup is biased towards addition.
     */
    private fun lookup(key: K): Boolean {
        val add = adds[key] ?: return false
        val remove = removes[key]
        return !(remove != null && remove > add)
    }

    /**
     * Returns element by its key
     */
    fun get(key: K): V? {
        return if (lookup(key)) {
            adds[key]?.value
        } else null
    }

    /**
     * Returns iterator of key-value pairs without their underlying add-and-remove timestamps.
     */
    fun iterator(): Iterator<Pair<K, V?>> {
        return adds.asSequence()
            .filter { lookup(it.key) }
            .map { (k, v) -> Pair(k, v.value) }
            .iterator()
    }

    /*
    * Add-or-updates a key-value Pair, and generates a new timestamp.
    * Timestamps are assumed unique, totally ordered, consistent with causal order, and monotonically increasing.
    */
    fun add(key: K, value: V) {
        val timedValue = TimedValue(value, time.now())
        val add = adds[key]
        if (add == null || add < timedValue) {
            adds[key] = timedValue
        }
    }

    /**
     * Removes an existing key-value Pair, and generates a new timestamp.
     * Timestamps are assumed unique, totally ordered, consistent with causal order, and monotonically increasing.
     */
    fun remove(key: K) {
        if (!lookup(key)) {
            return
        }
        val timedValue = TimedValue<V>(null, time.now())
        val remove = removes[key]
        if (remove == null || remove < timedValue) {
            removes[key] = timedValue
        }
    }

    /**
     * Merge commutes the values with the highest timestamp.
     */
    fun merge(other: LWWElementDictionary<K, V>) {
        mergeElements(adds, other.adds)
        mergeElements(removes, other.removes)
    }

    private fun mergeElements(first: MutableMap<K, TimedValue<V>>, second: Map<K, TimedValue<V>>) {
        for ((key, secondValue) in second) {
            val firstValue = first[key]
            if (firstValue != null && firstValue >= secondValue) {
                continue
            }
            first[key] = secondValue
        }
    }
}