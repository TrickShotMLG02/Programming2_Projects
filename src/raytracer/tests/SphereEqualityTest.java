package raytracer.tests;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import raytracer.geom.Sphere;
import raytracer.math.Point;

/**
 * Within this class and/or package you can implement your own tests that will
 * be run with the reference implementation.
 *
 * Note that no classes or interfaces will be available, except those initially
 * provided.
 */
public class SphereEqualityTest {

	@Test
	public void test() {
		Sphere s1 = new Sphere(new Point(1, 1, 1), 5f);
		Sphere s2 = new Sphere(new Point(1, 1, 1), 5.5f);

		assertFalse(s1.equals(s2));

	}

}
