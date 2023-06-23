package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Vec3;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *                    The file to read the data from
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the filename is null or the empty string,
	 *                                  the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *                    The InputStream of the data to be read.
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the InputStream is null, the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {

		if (accelerator == null || shader == null || translate == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		if (translate == Vec3.INF) {
			throw new IllegalArgumentException("translation not finite");
		}

		if (Float.isNaN(scale) || Float.isInfinite(scale) || scale < -Constants.EPS) {
			throw new IllegalArgumentException("invalid scale");
		}

		// list for storing vertices
		List<Point> vertices = new ArrayList<>();
		// set first index to origin since it is not used
		vertices.add(Point.ORIGIN);

		// create scanner to read file stream
		Scanner scanner = new Scanner(in);
		// set locale to english as specified in project description
		scanner.useLocale(Locale.ENGLISH);

		// lopp over whole file
		while (scanner.hasNextLine()) {

			// current öine
			String line = scanner.nextLine();
			String[] tokens = line.split(" ");

			switch (tokens[0]) {
				// line contains a vertice
				case "v":
					// create point from coordinates
					Point p = new Point(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]),
							Float.valueOf(tokens[3]));

					// scale point
					p = p.scale(scale);

					// translate point in space
					p = p.add(translate);

					// add point to vertices list
					vertices.add(p);
					break;

				// line contains a face
				case "f":

					// grab points from vertices list
					Point a = vertices.get(Integer.parseInt(tokens[1]));
					Point b = vertices.get(Integer.parseInt(tokens[2]));
					Point c = vertices.get(Integer.parseInt(tokens[3]));

					// create Triangle
					Primitive triangle = GeomFactory.createTriangle(a, b, c);
					// create object of triangle and shader
					Obj obj = new StandardObj(triangle, shader);

					// add object to accelerator structure
					accelerator.add(obj);

					break;

				// line contains something else
				default:
					// skip line
					break;

			}

		}

		// close scanner
		scanner.close();
	}
}
