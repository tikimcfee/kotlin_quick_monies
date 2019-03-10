package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import org.joda.time.DateTime
import java.util.*

class SortedList<E>(
    private val comparator: Comparator<E>
) : LinkedList<E>() {
    
    override fun add(index: Int, element: E) {
        add(element)
    }
    
    override fun add(element: E): Boolean {
        val result = super.add(element)
        if (result) {
            Collections.sort(this, comparator)
        }
        return result
    }
}

abstract class SortedMultiMap<K, V> : Comparator<V> {
    
    //(oct 1 -> oct 31) -> [t1, t2, t3, t4, t5, t5]
    private val entryMap: MutableMap<K, SortedList<V>> = mutableMapOf()
    
    private fun makeSortedList() = SortedList(this)
    
    fun addToMap(key: K, value: V) {
        val targetList = entryMap[key] ?: makeSortedList().apply { entryMap[key] = this }
        targetList.add(value)
    }
    
    fun allEntries() = entryMap.asSequence()
    
    operator fun get(key: K) = entryMap[key]
    
    operator fun set(key: K, value: SortedList<V>) = entryMap.put(key, value)
}

enum class SortOption {
    Date,
    Amount,
}

fun SortOption.applyTo(st1: Transaction, st2: Transaction): Int =
    when (this) {
        SortOption.Date -> when {
            st1.date isBefore st2.date -> -1
            st1.date isAfter st2.date -> 1
            else -> 0
        }
        SortOption.Amount -> when {
            st1.amount < st2.amount -> -1
            st1.amount > st2.amount -> 1
            else -> 0
        }
    }

private const val WEEK_START = 1
private const val WEEK_END = 7

private infix fun Long.isBefore(date: Long) = this - date < 0

private infix fun Long.isAfter(date: Long) = this - date > 0

fun Long.asStartOfWeek() =
    DateTime(this).withTimeAtStartOfDay().withDayOfWeek(WEEK_START)

fun Long.asEndOfWeek() =
    asStartOfWeek().plusDays(WEEK_END)

fun Long.asStartOfMonth() =
    DateTime(this).withTimeAtStartOfDay().withDayOfMonth(1)

fun Long.asEndOfMonth() =
    asStartOfMonth().plusMonths(1).minusDays(1)

fun Long.asStartOfYear() =
    DateTime(this).withTimeAtStartOfDay().withMonthOfYear(1)

fun Long.asEndOfYear() =
    DateTime(this).withTimeAtStartOfDay().withMonthOfYear(12)
