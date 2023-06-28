package tutorial

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
    fun otherFunctions() {
        TODO("Not yet implemented")
    }
}