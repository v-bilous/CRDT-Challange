package crdt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LWWElementDictionaryTest {

    private val fakeTime = FakeTimeAdapter()
    /**
     * Creates the LWWElementDictionary and checks that no element with key `key1`, and iterator is null as well.
     */
    @Test
    fun `create dict and get iterator`() {
        val dict = LWWElementDictionary<String, String>(fakeTime)

        assertNull(dict.get("key"))
        assertFalse(dict.iterator().hasNext())
    }

    /**
     * This test is for scenario: add an element and get a non-empty iterator
     */
    @Test
    fun `add element and get iterator`() {
        val dict = LWWElementDictionary<String, Int>(fakeTime)

        dict.add("key", 8000)
        assertEquals(8000, dict.get("key"))

        val iterator = dict.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(Pair("key", 8000), iterator.next())
    }

    /**
     * Add an element to the dictionary and then remove it.
     * Test should validate scenario of successful deletion.
     */
    @Test
    fun `add and remove element from dict`() {
        val dict = LWWElementDictionary<String, String>(fakeTime)

        dict.add("key", "value")
        fakeTime.tick()
        dict.remove("key")

        assertNull(dict.get("key"))
        assertFalse(dict.iterator().hasNext())
    }

    /**
     * Check the scenario: remove then get, and iterator.
     * Get should return null.
     * Iterator should return an empty iterator.
     */
    @Test
    fun `remove and get element from dict`() {
        val dict = LWWElementDictionary<String, String>(fakeTime)

        dict.remove("key")

        assertNull(dict.get("key"))
        assertFalse(dict.iterator().hasNext())
    }

    /**
     * This test evaluates the scenario: add, add get/iterator.
     * Call to get should return the value set by the second add.
     * Call to iterator should return an iterator containing the value set by the second add.
     */
    @Test
    fun `add and add and get element`() {
        val dict = LWWElementDictionary<String, Int>(fakeTime)

        dict.add("key", 8000)
        fakeTime.tick()
        dict.add("key", 10000)

        assertEquals(10000, dict.get("key"))
        val iterator = dict.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(Pair("key", 10000), iterator.next())
    }

    /**
     * This test evaluates the scenario: add || merge get/iterator.
     * Call to get should return the value set by the add registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the add registered in the first replica.
     */
    @Test
    fun `validate merge dictionaries`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict1.add("key", "value")

        dict1.merge(dict2)
        dict2.merge(dict1)

        assertEquals("value", dict1.get("key"))
        val iterator1 = dict1.iterator()
        assertTrue(iterator1.hasNext())
        assertEquals(Pair("key", "value"), iterator1.next())

        assertEquals("value", dict2.get("key"))
        val iterator2 = dict2.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(Pair("key", "value"), iterator2.next())
    }

    /**
     * This test evaluates the scenario: add || merge addLWW get/iterator.
     * Call to get should return the value set by add registered in the second replica.
     * Call to iterator should return an iterator containing the value set by the add registered in the second replica.
     */
    @Test
    fun `add merge add element in dictionary`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict1.add("key", "value1")
        dict2.merge(dict1)
        fakeTime.tick()
        dict2.add("key", "value2")

        assertEquals("value2", dict2.get("key"))

        val iterator2 = dict2.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(Pair("key", "value2"), iterator2.next())
        assertFalse(iterator2.hasNext())
    }

    /**
     * This test evaluates the scenario: add || addLWW merge get/iterator.
     * Call to get should return the value set by add registered in the second replica.
     * Call to iterator should return an iterator containing the value set by add registered in the second replica.
     */
    @Test
    fun `dict1add dict2add merge dict1 to dict2`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict1.add("key", "value1")
        fakeTime.tick()
        dict2.add("key", "value2")
        dict2.merge(dict1)

        assertEquals("value2", dict2.get("key"))

        val iterator2 = dict2.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(Pair("key", "value2"), iterator2.next())
        assertFalse(iterator2.hasNext())
    }

    /**
     * This test evaluates the scenario: addLWW || add merge get/iterator.
     * Call to get should return the value set by add registered in the first replica.
     * Call to iterator should return an iterator containing the value set by the add registered in the first replica.
     */
    @Test
    fun `dict1add dict2add merge dict2 to dict1`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict1.add("key", "value1")
        fakeTime.tick()
        dict2.add("key", "value2")
        dict1.merge(dict2)

        assertEquals("value2", dict1.get("key"))

        val iterator1 = dict1.iterator()
        assertTrue(iterator1.hasNext())
        assertEquals(Pair("key", "value2"), iterator1.next())
        assertFalse(iterator1.hasNext())
    }

    /**
     * This test evaluates the scenario: add delLWW || add merge get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    @Test
    fun `dict1 add remove and dict2 add merge`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict2.add("key1", "value2")
        fakeTime.tick()
        dict1.add("key1", "value1")
        fakeTime.tick()
        dict1.remove("key1")
        dict2.merge(dict1)

        assertNull(dict2.get("key1"))
        assertFalse(dict2.iterator().hasNext())
    }

    /**
     * This test evaluates the scenario: add delLWW || add merge(before del) merge(after del) get/iterator.
     * Call to get should return null.
     * Call to iterator should return an empty iterator.
     */
    @Test
    fun `dict1 add remove and dict2 add merge before remove merge after remove`() {
        val dict1 = LWWElementDictionary<String, String>(fakeTime)
        val dict2 = LWWElementDictionary<String, String>(fakeTime)

        dict2.add("key1", "value2")
        fakeTime.tick()
        dict1.add("key1", "value1")
        dict2.merge(dict1)
        fakeTime.tick()
        dict1.remove("key1")
        dict2.merge(dict1)

        assertNull(dict2.get("key1"))
        assertFalse(dict2.iterator().hasNext())
    }
 }