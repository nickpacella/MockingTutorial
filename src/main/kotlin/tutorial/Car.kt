// Example class for MockK tutorial

package tutorial

enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
}

class Car {
    fun drive(direction: Direction, speed: Long = 30): String {
        return "Driving $direction at $speed km/h"
    }
}


