package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Sphere extends BBoxedPrimitive {

    private final Point m;
    private final Vec3 u, v, n;
    private final float r;

    /**
     * Creates a new Sphere
     * 
     * @param c  the point of the center of the sphere
     * @param s1 point 1 which is on the surface of the sphere
     * @param s2 point 2 which is on the surface of the sphere
     */
    public Sphere(final Point c, final Point s1, final Point s2) {
        // create bounding box with dimensions of diameter the sphere determined by
        // center and a point (s1) which is on the surface of the sphere
        super(BBox.create(c, s1));

        // set radius of sphere by calculating length of vector from center to surface
        // point
        r = c.sub(s1).norm();

        // set m to center point
        this.m = c;

        // set u to vector from center to surface point 1
        this.u = s1.sub(c);

        // set v to vector from center to surface point 2
        this.v = s2.sub(c);

        // calculate normal vector of the two vectos pointing from center to surface
        this.n = u.cross(v).normalized();
    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        /*
         * We use an anonymous class:
         * instead of implementing a class that extends LazyHitTest
         * we implement all abstract methods of LazyHitTest on-the-fly.
         * This is possible since we don't need the class anywhere else and don't need
         * to refer to it anywhere by name.
         */
        return new LazyHitTest(obj) {
            /*
             * The abstract class LazyHitTest itself is not specific to the geometric object
             * triangle.
             * This implementation of the class on the other hand is specific to it.
             */
            private Point point = null;
            // r is the distance of the ray
            private float r;

            @Override
            public float getParameter() {
                return r;
            }

            @Override
            public Point getPoint() {
                if (point == null)
                    point = ray.eval(getParameter()).add(n.scale(0.0001f));
                return point;
            }

            @Override
            protected boolean calculateHit() {
                // get direction of ray
                final Vec3 /* normalized */ dir = ray.dir();

                // a = dir^2 = 1 (calculate scalar product of normalized direction vector)
                float a = dir.sdot();

                // p_s = starting point of ray
                // c_k = center of sphere

                // p_s - c_k
                Vec3 ps_ck = ray.base().sub(m);

                // b = 2 * dir * (ps_ck)
                float b = 2 * dir.dot(ps_ck);

                // c = (p_s - c_k)^2 - r_k^2
                float c = (float) ((ps_ck.sdot()) - Math.pow(Sphere.this.r, 2));

                float discriminant = (float) Math.pow(b, 2) - 4 * a * c;

                // no solution for equation -> no intersection
                if (discriminant < 0) {
                    return false;
                } else {
                    // abc-formula
                    float lambda1 = (float) (-b - Math.sqrt(discriminant)) / 2;
                    float lambda2 = (float) (-b + Math.sqrt(discriminant)) / 2;

                    // ignore larger solution
                    float solution = Math.min(lambda1, lambda2);

                    // check if solution is smaller than 0 -> intersection point would be behind ray
                    // starting position
                    if (solution < 0) {
                        return false;
                    }

                    // check that hit was good
                    if (solution <= tmax && solution >= tmin) {
                        // store solution as raycast hit distance
                        r = solution;

                        // return true, since hit was valid
                        return true;
                    } else {
                        return false;
                    }
                }

            }

            @Override
            public Vec2 getUV() {
                // TODO: computeSphereUV from impactPoint as vector
                return Util.computeSphereUV(m.sub(getPoint()));
            }

            @Override
            public Vec3 getNormal() {
                // calculate normal vector of hit point and center of sphere;
                return getPoint().sub(m).normalized();
            }

        };
    }

    @Override
    public int hashCode() {
        return m.hashCode() ^ u.hashCode() ^ v.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Sphere) {
            final Sphere cobj = (Sphere) other;
            return cobj.m.equals(m) && cobj.u.equals(u) && cobj.v.equals(v);
        }
        return false;
    }

}
