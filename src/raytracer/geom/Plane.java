package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Plane extends BBoxedPrimitive {

    private final Point m;
    private final Vec3 u, v, n;

    public Plane(final Point a, final Point b, final Point c) {
        // create 2d box which is infinitely large
        super(BBox.INF);

        this.m = a;

        // vector 1
        this.u = b.sub(a);

        // vector 2
        this.v = c.sub(a);

        // normalized normal vector of the plane
        this.n = u.cross(v).normalized();
    }

    public Plane(final Vec3 n, final Point supp) {
        // create 2d box which is infinitely large
        super(BBox.INF);

        this.n = n.normalized();
        // create orthogonal vector by setting one coordinate to 0, switching two others
        // and multiplying one with -1
        this.u = new Vec3(0, -n.z(), n.y());

        // create second orthogonal vector from crossproduct
        this.v = n.cross(u);

        // set m to support point

        this.m = supp;

    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {

            private Point point = null;
            // r is the distance of the ray
            private float r = -1;

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
                // create copy of support point since we should not modify original support
                // point
                Point m2 = m;
                // if support point is the origin, we need to move, because we will otherwise
                // run into a division by 0 error in the angle calculation
                if (m.equals(Point.ORIGIN)) {
                    m2 = m.add(u).add(v);
                }

                // get direction of ray
                final Vec3 /* normalized */ dir = ray.dir();

                Vec3 p_e = new Vec3(m2.x(), m2.y(), m2.z());
                float angle = p_e.angle(n);
                float distance = (float) (p_e.norm() * angle);

                // check if scalar product is 0 => not valid
                if (dir.dot(n) == 0) {
                    return false;
                }

                float solution = (distance - ray.base().dot(n)) / dir.dot(n);

                if (solution < 0) {
                    return false;
                }

                // check that hit was good
                if (solution <= tmax && solution >= tmin) {
                    // store solution as raycast hit distance
                    this.r = solution;
                    // return true, since hit was valid
                    return true;
                } else {
                    return false;
                }

            }

            @Override
            public Vec2 getUV() {
                return Util.computePlaneUV(n, m, getPoint());
            }

            @Override
            public Vec3 getNormal() {
                return n;
            }

        };
    }

    @Override
    public int hashCode() {
        return m.hashCode() ^ u.hashCode() ^ v.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Plane) {
            final Plane cobj = (Plane) other;
            return cobj.m.equals(m) && cobj.u.equals(u) && cobj.v.equals(v);
        }
        return false;
    }

}
