package com.example.finance.domain.record

import com.example.finance.domain.record.exceptions.InvalidCategoryException
import java.time.Instant
import java.util.UUID
import kotlin.test.*

/**
 * Unit tests for the Record domain entity.
 * Validates entity construction, business rules, and field updates.
 * Covers newly introduced integer-based Paisa amounts and sign-signaling.
 */
internal class RecordTest {

    private val testAmount = 10000L // Rs 100.00
    private val testCategory = "Food"
    private val testDate = Instant.now()

    @Test
    fun `should create record with generated id and handle positive and negative amounts`() {
        val income = Record.create(testAmount, testCategory, testDate)
        assertTrue(income.amount > 0, "Amount should be positive for income")

        val expense = Record.create(-5000L, testCategory, testDate)
        assertTrue(expense.amount < 0, "Amount should be negative for expense")

        assertNotNull(income.id)
        assertEquals(testAmount, income.amount)
        assertEquals(testCategory, income.category)
        assertEquals(testDate, income.date)
    }

    @Test
    fun `should fail to create record with blank category`() {
        assertFailsWith<InvalidCategoryException> {
            Record.create(testAmount, " ", testDate)
        }
    }

    @Test
    fun `should allow changing amount to both positive and negative values`() {
        val record = Record.create(testAmount, testCategory, testDate)
        record.changeAmount(-5000L)
        assertEquals(-5000L, record.amount)

        record.changeAmount(150L)
        assertEquals(150L, record.amount)
    }

    @Test
    fun `should allow changing category with validation`() {
        val record = Record.create(testAmount, testCategory, testDate)
        record.changeCategory("Salary")
        assertEquals("Salary", record.category)

        assertFailsWith<InvalidCategoryException> {
            record.changeCategory("")
        }
    }

    @Test
    fun `should allow changing date`() {
        val record = Record.create(testAmount, testCategory, testDate)
        val newDate = Instant.now().plusSeconds(3600)
        record.changeDate(newDate)
        assertEquals(newDate, record.date)
    }

    @Test
    fun `should allow changing description`() {
        val record = Record.create(testAmount, testCategory, testDate, "Old")
        record.changeDescription("New")
        assertEquals("New", record.description)
        
        record.changeDescription(null)
        assertNull(record.description)
    }

    @Test
    fun `should support reconstitution through direct constructor`() {
        val id = UUID.randomUUID()
        val record = Record(
            id = id,
            amount = 99999L,
            category = "External",
            date = testDate,
            description = "Legacy data"
        )
        
        assertEquals(id, record.id)
        assertEquals(99999L, record.amount)
        assertEquals("External", record.category)
    }
}
