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
    private final float r;

    /**
     * Creates a new Sphere
     * 
     * @param c the point of the center of the sphere
     * @param r the radius of the sphere
     */
    public Sphere(final Point c, final float r) {
        super(BBox.create(c.sub(new Vec3(r, r, r)), c.add(new Vec3(r, r, r))));
        this.r = r;
        this.m = c;
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
            private float d;

            @Override
            public float getParameter() {
                return d;
            }

            @Override
            public Point getPoint() {
                if (point == null)
                    // point = ray.eval(getParameter()).add(n.scale(0.0001f));
                    point = ray.eval(getParameter());
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
                if (r <= 0 || discriminant < 0) {
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
                        d = solution;

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
                return Util.computeSphereUV(getPoint().sub(m));
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
        return m.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Sphere) {
            final Sphere cobj = (Sphere) other;
            return cobj.m.equals(m) && this.r == r;
        }
        return false;
    }

}
