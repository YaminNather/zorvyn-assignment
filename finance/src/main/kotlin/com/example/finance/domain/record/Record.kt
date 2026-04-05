package com.example.finance.domain.record

import com.example.finance.domain.record.exceptions.InvalidCategoryException
import java.time.Instant
import java.util.UUID

/**
 * Represents a single financial record belonging to a user.
 * Amount is stored in Indian Paisa as a [Long] to avoid precision errors.
 * Positive amount indicates INCOME, negative amount indicates EXPENSE.
 * Encapsulates financial data and enforces domain rules.
 */
internal class Record(
    val id: UUID,
    amount: Long,
    category: String,
    date: Instant,
    description: String? = null
) {
    var amount: Long = amount
        private set
    var category: String = category
        private set
    var date: Instant = date
        private set
    var description: String? = description
        private set

    init {
        validate(category)
    }

    /**
     * Changes the record's amount.
     * Positive for income, negative for expense.
     */
    fun changeAmount(newAmount: Long) {
        this.amount = newAmount
    }

    /**
     * Changes the record's category after validation.
     */
    fun changeCategory(newCategory: String) {
        if (newCategory.isBlank()) throw InvalidCategoryException(newCategory)
        this.category = newCategory
    }

    /**
     * Changes the record's date.
     */
    fun changeDate(newDate: Instant) {
        this.date = newDate
    }

    /**
     * Changes the record's description.
     */
    fun changeDescription(newDescription: String?) {
        this.description = newDescription
    }

    private fun validate(category: String) {
        if (category.isBlank()) throw InvalidCategoryException(category)
    }

    companion object {
        /**
         * Factory method to create a new financial Record.
         * Auto-generates a unique ID and validates initial state.
         */
        fun create(
            amount: Long,
            category: String,
            date: Instant,
            description: String? = null
        ): Record {
            // Validate domain rules before entity creation
            if (category.isBlank()) throw InvalidCategoryException(category)
            
            return Record(
                id = UUID.randomUUID(),
                amount = amount,
                category = category,
                date = date,
                description = description
            )
        }
    }
}
