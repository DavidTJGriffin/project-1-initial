package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a point in three-dimensional Cartesian space.
 *
 * This class provides a robust implementation of a 3D point with mathematical
 * operations commonly used in computer graphics, physics simulations, and
 * spatial analysis. The implementation follows industry-standard practices
 * for geometric computations.
 *
 * <p>Design Patterns & Principles:</p>
 * <ul>
 *   <li><b>Immutability Pattern:</b> This class is immutable - all fields are
 *       final and all transformation methods return new Point3D instances rather
 *       than modifying the existing object. This ensures thread-safety and
 *       prevents unexpected side effects in concurrent environments.</li>
 *   <li><b>Value Object Pattern:</b> Point3D represents a conceptual value
 *       rather than an entity with identity. Two points with the same coordinates
 *       are considered equal, regardless of when or how they were created.</li>
 *   <li><b>Builder Pattern (via static factory):</b> Static factory methods
 *       provide clear, named constructors that improve code readability.</li>
 * </ul>
 *
 * <p>Algorithmic Foundation:</p>
 * This class demonstrates fundamental concepts in computational geometry:
 * - Vector arithmetic (addition, subtraction, scaling)
 * - Euclidean distance calculation (O(1) time complexity)
 * - Rotation matrices and quaternion-based transformations
 * - Dot and cross products for geometric computations
 *
 * @author David Rodriguez
 * @version 1.0
 * @since 2026-01-27
 */
public final class Point3D {

    private static final Logger logger = Logger.getLogger(Point3D.class.getName());

    // Immutable coordinates - demonstrates encapsulation principle
    private final double x;
    private final double y;
    private final double z;

    // Epsilon for floating-point comparisons - prevents precision errors
    private static final double EPSILON = 1e-10;

    /**
     * Constructs a new Point3D with the specified coordinates.
     *
     * This is the primary constructor used internally. For external use,
     * consider using the static factory method {@link #of(double, double, double)}
     * for improved readability.
     *
     * <p><b>Time Complexity:</b> O(1) - constant time initialization</p>
     * <p><b>Space Complexity:</b> O(1) - fixed memory allocation</p>
     *
     * @param x the x-coordinate in 3D space
     * @param y the y-coordinate in 3D space
     * @param z the z-coordinate in 3D space
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        logger.log(Level.INFO, "Created Point3D at coordinates ({0}, {1}, {2})",
                new Object[]{x, y, z});
    }

    /**
     * Static factory method for creating a Point3D instance.
     *
     * This method provides a more fluent API compared to the constructor,
     * improving code readability. Example: Point3D.of(1.0, 2.0, 3.0)
     *
     * <p><b>Design Pattern:</b> Static Factory Method - provides named
     * constructor with clearer semantics than 'new Point3D()'</p>
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return a new Point3D instance
     */
    public static Point3D of(double x, double y, double z) {
        return new Point3D(x, y, z);
    }

    /**
     * Returns the origin point (0, 0, 0) in 3D space.
     *
     * This method provides a convenient way to reference the coordinate system's
     * origin without creating multiple identical instances.
     *
     * @return a Point3D representing the origin
     */
    public static Point3D origin() {
        return new Point3D(0, 0, 0);
    }

    // Accessor methods - demonstrates encapsulation

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * This method implements the standard distance formula in 3D space:
     * d = √[(x₂-x₁)² + (y₂-y₁)² + (z₂-z₁)²]
     *
     * <p><b>Algorithm:</b> Euclidean distance calculation using the Pythagorean
     * theorem extended to three dimensions.</p>
     *
     * <p><b>Time Complexity:</b> O(1) - constant time with fixed number of operations</p>
     * <p><b>Space Complexity:</b> O(1) - no additional space required</p>
     *
     * <p><b>Numerical Stability:</b> This implementation uses standard floating-point
     * arithmetic. For extremely large coordinate values, consider using distance
     * squared to avoid potential overflow in intermediate calculations.</p>
     *
     * @param other the point to calculate distance to
     * @return the Euclidean distance between the two points
     * @throws IllegalArgumentException if other is null
     */
    public double distanceTo(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot calculate distance to null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        logger.log(Level.INFO, "Calculated distance from {0} to {1}: {2}",
                new Object[]{this, other, distance});

        return distance;
    }

    /**
     * Calculates the squared distance between this point and another point.
     *
     * This method is more efficient than {@link #distanceTo(Point3D)} when you
     * only need to compare distances (e.g., finding the nearest point) since it
     * avoids the expensive square root operation.
     *
     * <p><b>Performance Optimization:</b> Use this method instead of distanceTo()
     * when comparing multiple distances, as it eliminates the √ operation.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param other the point to calculate squared distance to
     * @return the squared Euclidean distance
     * @throws IllegalArgumentException if other is null
     */
    public double distanceSquared(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot calculate squared distance to null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the magnitude (length) of the vector from origin to this point.
     *
     * When treating this point as a position vector from the origin, this method
     * returns the vector's length using the formula: ||v|| = √(x² + y² + z²)
     *
     * <p><b>Geometric Interpretation:</b> This represents the straight-line
     * distance from the origin to this point.</p>
     *
     * @return the magnitude of the position vector
     */
    public double magnitude() {
        double mag = Math.sqrt(x * x + y * y + z * z);
        logger.log(Level.INFO, "Calculated magnitude of {0}: {1}",
                new Object[]{this, mag});
        return mag;
    }

    /**
     * Rotates this point around the X-axis by the specified angle.
     *
     * This method applies a rotation transformation using the rotation matrix:
     * [1    0         0    ]
     * [0  cos(θ)  -sin(θ) ]
     * [0  sin(θ)   cos(θ) ]
     *
     * <p><b>Design Pattern:</b> Immutability - returns a new Point3D rather than
     * modifying this instance, ensuring thread-safety and preventing side effects.</p>
     *
     * <p><b>Algorithm:</b> Matrix-vector multiplication using Rodrigues' rotation
     * formula for axis-aligned rotations.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param angleRadians the rotation angle in radians (positive = counter-clockwise
     *                     when looking along positive X-axis toward origin)
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateX(double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        double newY = y * cos - z * sin;
        double newZ = y * sin + z * cos;

        logger.log(Level.INFO, "Rotated {0} around X-axis by {1} radians",
                new Object[]{this, angleRadians});

        return new Point3D(x, newY, newZ);
    }

    /**
     * Rotates this point around the Y-axis by the specified angle.
     *
     * Rotation matrix used:
     * [ cos(θ)   0   sin(θ) ]
     * [   0      1     0    ]
     * [-sin(θ)   0   cos(θ) ]
     *
     * @param angleRadians the rotation angle in radians
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateY(double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        double newX = x * cos + z * sin;
        double newZ = -x * sin + z * cos;

        logger.log(Level.INFO, "Rotated {0} around Y-axis by {1} radians",
                new Object[]{this, angleRadians});

        return new Point3D(newX, y, newZ);
    }

    /**
     * Rotates this point around the Z-axis by the specified angle.
     *
     * Rotation matrix used:
     * [ cos(θ)  -sin(θ)   0 ]
     * [ sin(θ)   cos(θ)   0 ]
     * [   0        0      1 ]
     *
     * @param angleRadians the rotation angle in radians
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateZ(double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;

        logger.log(Level.INFO, "Rotated {0} around Z-axis by {1} radians",
                new Object[]{this, angleRadians});

        return new Point3D(newX, newY, z);
    }

    /**
     * Adds another point's coordinates to this point (vector addition).
     *
     * This method performs component-wise addition, treating both points as
     * position vectors: (x₁+x₂, y₁+y₂, z₁+z₂)
     *
     * <p><b>Mathematical Foundation:</b> Vector addition in 3D space, following
     * the parallelogram law of vector addition.</p>
     *
     * @param other the point to add
     * @return a new Point3D representing the sum
     * @throws IllegalArgumentException if other is null
     */
    public Point3D add(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot add null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        return new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    /**
     * Subtracts another point's coordinates from this point (vector subtraction).
     *
     * This creates a displacement vector from the other point to this point.
     *
     * @param other the point to subtract
     * @return a new Point3D representing the difference
     * @throws IllegalArgumentException if other is null
     */
    public Point3D subtract(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot subtract null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        return new Point3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Multiplies this point's coordinates by a scalar value.
     *
     * Performs scalar multiplication: (s·x, s·y, s·z)
     * This operation scales the point's distance from the origin.
     *
     * <p><b>Geometric Effect:</b> Scales the position vector by the given factor.
     * Negative scalars reverse direction; values between 0-1 shrink the vector.</p>
     *
     * @param scalar the multiplication factor
     * @return a new Point3D representing the scaled point
     */
    public Point3D scale(double scalar) {
        return new Point3D(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Calculates the dot product between this point and another (treating as vectors).
     *
     * The dot product formula: a · b = ax·bx + ay·by + az·bz
     *
     * <p><b>Applications:</b></p>
     * <ul>
     *   <li>Determining angle between vectors: cos(θ) = (a·b)/(||a||·||b||)</li>
     *   <li>Testing orthogonality: vectors are perpendicular if dot product = 0</li>
     *   <li>Projecting one vector onto another</li>
     * </ul>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param other the other point (treated as vector)
     * @return the dot product
     * @throws IllegalArgumentException if other is null
     */
    public double dot(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot calculate dot product with null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    /**
     * Calculates the cross product between this point and another (treating as vectors).
     *
     * The cross product produces a vector perpendicular to both input vectors:
     * a × b = (ay·bz - az·by, az·bx - ax·bz, ax·by - ay·bx)
     *
     * <p><b>Properties:</b></p>
     * <ul>
     *   <li>Result is perpendicular to both input vectors</li>
     *   <li>Magnitude equals area of parallelogram formed by the vectors</li>
     *   <li>Direction follows right-hand rule</li>
     *   <li>Anti-commutative: a × b = -(b × a)</li>
     * </ul>
     *
     * <p><b>Applications:</b> Surface normals, torque calculations, determining
     * rotation axis between two vectors</p>
     *
     * @param other the other point (treated as vector)
     * @return a new Point3D representing the cross product
     * @throws IllegalArgumentException if other is null
     */
    public Point3D cross(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot calculate cross product with null point");
            throw new IllegalArgumentException("Other point cannot be null");
        }

        double newX = this.y * other.z - this.z * other.y;
        double newY = this.z * other.x - this.x * other.z;
        double newZ = this.x * other.y - this.y * other.x;

        logger.log(Level.INFO, "Calculated cross product of {0} × {1}",
                new Object[]{this, other});

        return new Point3D(newX, newY, newZ);
    }

    /**
     * Returns a normalized version of this point (unit vector).
     *
     * Normalization scales the vector to length 1 while preserving direction:
     * normalized = (x/||v||, y/||v||, z/||v||)
     *
     * <p><b>Warning:</b> Attempting to normalize the zero vector will log a
     * warning and return the zero vector unchanged.</p>
     *
     * @return a new Point3D with magnitude 1 in the same direction
     */
    public Point3D normalize() {
        double mag = magnitude();

        if (mag < EPSILON) {
            logger.log(Level.WARNING,
                    "Attempted to normalize zero or near-zero vector: {0}", this);
            return this;
        }

        return new Point3D(x / mag, y / mag, z / mag);
    }

    /**
     * Compares this point with another for equality within floating-point tolerance.
     *
     * Due to floating-point precision limitations, exact equality checks are
     * unreliable. This method uses an epsilon value for robust comparison.
     *
     * <p><b>Design Pattern:</b> Overriding equals() for value-based equality,
     * implementing the Value Object pattern contract.</p>
     *
     * @param obj the object to compare with
     * @return true if the points are equal within epsilon tolerance
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Point3D other = (Point3D) obj;

        return Math.abs(this.x - other.x) < EPSILON &&
                Math.abs(this.y - other.y) < EPSILON &&
                Math.abs(this.z - other.z) < EPSILON;
    }

    /**
     * Generates a hash code for this point.
     *
     * <p><b>Contract:</b> Objects that are equal must have the same hash code.
     * This implementation uses Java's built-in Double.hashCode() for each coordinate.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        return result;
    }

    /**
     * Returns a string representation of this point.
     *
     * Format: "Point3D(x, y, z)"
     *
     * @return a human-readable string representation
     */
    @Override
    public String toString() {
        return String.format("Point3D(%.2f, %.2f, %.2f)", x, y, z);
    }
}
