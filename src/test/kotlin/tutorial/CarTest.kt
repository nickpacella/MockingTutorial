package tutorial

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContains
import kotlin.test.assertEquals

/**
 * This test class provides a tutorial for some of the simpler functions of MockK. Shows how to
 * use mocking with basic objects (see Car class).
 */
@ExtendWith(MockKExtension::class)
class CarTest {

    // Create mock/spy
    // Mocks simulate dependency behavior and control the results returned.
    val car1 = mockk<Car>()

    // Create mocks using annotations. Must use lateinit prefix when using annotations.
    @MockK
    lateinit var car2: Car

    // Set up mock made with annotations
    @BeforeEach
    fun setup() {
        car2 = mockk<Car>()
    }


//    // Relaxed mocks return some simple value for ALL functions (no need to specify behavior)
//    @RelaxedMockK
//    lateinit var car3: Car
//
//    // Another way to make it relaxed. Set the variable "relaxUnitFun" to true.
//    @MockK(relaxUnitFun = true)
//    lateinit var car4: Car
//
//    // SpyK allows you to call specific parts of an objects functionality
//    @SpyK
//    lateinit var car5: Car

    @Test
    fun `some verify uses`() {

        // Stub call (simulate the function of car)
        every {car1.drive(Direction.NORTH)} returns "Driving north!"

        // Call function on mock object
        car1.drive(Direction.NORTH)

        // Ensures that the function was called
        verify {car1.drive(Direction.NORTH)}

        // Verify amount of calls that happened, parameter does not matter
        verify(atLeast = 1) {car1.drive(any())}

        // Verify the amount of calls does not exceed a certain amount
        verify(atMost = 3) {car1.drive(any())}

        // Verify the amount of calls is exactly equal to a certain number
        verify(exactly = 1) {car1.drive(any())}

        // Simple assert equals statement
        assertEquals(car1.drive(Direction.NORTH), "Driving north!")

    }

    @Test
    fun groupVerify() {

        // Must stub calls before attempting to use verify
        every {car2.drive(Direction.NORTH)} returns "Driving north!"
        every {car2.drive(Direction.SOUTH)} returns "Driving south!"
        every {car2.drive(Direction.EAST)} returns "Driving east!"
        every {car2.drive(Direction.WEST)} returns "Driving west!"

        // Call some functions on car
        car2.drive(Direction.NORTH)
        car2.drive(Direction.SOUTH)
        car2.drive(Direction.EAST)
        car2.drive(Direction.WEST)

        // Group verify calls
        verify {
            car2.drive(Direction.NORTH)
            car2.drive(Direction.SOUTH)
        }

        // Confirm whether all calls on object were actually verified throughout testing
        // this will fail at this point because east and west have not been verified
//        confirmVerified(car2)

        // verify order of calls (exact sequence does not matter, just that a call was made after the other)
        verifyOrder {
            car2.drive(Direction.NORTH)
            car2.drive(Direction.EAST)
        }

        // verify sequence of calls (must be EXACTLY in sequence)
        verifySequence {
            car2.drive(Direction.NORTH)
            car2.drive(Direction.SOUTH)
            car2.drive(Direction.EAST)
            car2.drive(Direction.WEST)
        }

        // All calls on the mock car2 were verified at some point
        confirmVerified(car2)

        // Use this to exclude less significant calls (you don't care whether they have been verified)
        excludeRecords{car2.drive(Direction.WEST)}
    }

    @Test
    fun otherFunction() {

        // Allows you to be more broad with specifying what functions should return
        every {
            car2.drive(
                direction = or(Direction.EAST, Direction.WEST),
                30
            )
        } returns("Driving somewhere at speed 30!")

        // Now both of these functions return the same value
        assertEquals(car2.drive(Direction.EAST, 30), "Driving somewhere at speed 30!")
        assertEquals(car2.drive(Direction.WEST, 30), "Driving somewhere at speed 30!")

    }

    @Test
    fun `single and multiple argument capture`() {

        // SINGLE VARIABLE ARGUMENT CAPTURE
        val car3 = mockk<Car>()
        val slot = slot<Long>()

        // specify capture variable (slot)
        every {car3.drive(any(), capture(slot))} returns "Driving!"

        // 55 is now captured into the "slot" value
        car3.drive(Direction.EAST, 55)

        // Retrieve the captured variable
        assertEquals(slot.captured, 55L)


        // MULTIPLE VARIABLE ARGUMENT CAPTURE
        val list = mutableListOf<Long>()

        every {car3.drive(any(), capture(list))} returns "Driving..."

        car3.drive(Direction.EAST, 10)
        car3.drive(Direction.WEST, 20)
        car3.drive(Direction.NORTH, 30)
        car3.drive(Direction.SOUTH, 40)

        // Make sure all speeds have been captured
        assertContains(list, 10)
        assertContains(list, 20)
        assertContains(list, 30)
        assertContains(list, 40)
    }
}