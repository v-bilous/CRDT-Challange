# Dictionary CRDT Challenge
## Deliverable

Study LWW-Element-Set and implement a state-based LWW-Element-Dictionary with test cases. Similar to LWW-Element-Set, the dictionary variant you are going to implement will:  
● Store a timestamp for each key-value pair.  
● Lookup,add,and remove operations,  
● Allow updating the value of a key.  
● There should be a function to merge two dictionaries.  

Test cases should be clearly written and document what aspect of CRDT they test.
We recommend you to spend no more than 4 hours on this challenge. The provided readings should be sufficient to understand LWW-Element-Set and CRDT on a high level. You are welcome to dig deeper on those but we expect you to come up with the implementation yourself without any help from other open-sourced implementations

### Operations

Get:
```kotlin
    /**
     * Returns element by its key
     */
    fun get(key: K): V?
```
Add:
```kotlin
    /*
    * Add-or-updates a key-value Pair, and generates a new timestamp.
    * Timestamps are assumed unique, totally ordered, consistent with causal order, and monotonically increasing.
    */
    fun add(key: K, value: V)
```
Remove:
```kotlin
    /**
    * Removes an existing key-value Pair, and generates a new timestamp.
    * Timestamps are assumed unique, totally ordered, consistent with causal order, and monotonically increasing.
    */
    fun remove(key: K)
```
Lookup:
```kotlin
    /**
     * Lookup key-value Pair by its key.
     * The lookup is biased towards addition.
     */
     private fun lookup(key: K)
```
Iterator:
```kotlin
    /**
    * Returns iterator of key-value pairs without their underlying add-and-remove timestamps.
    */
    fun iterator(): Iterator<Pair<K, V?>>
```
Merge:
```kotlin
    /**
     * Merge commutes the values with the highest timestamp.
    */
    fun merge(other: LWWElementDictionary<K, V>)
```

### Usage

Run on your local machine:

```
./gradlew test
```