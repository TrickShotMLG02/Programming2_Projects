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

        // chosen point
        this.m = a;

        // vector 1
        this.u = b.sub(a);

        // vector 2
        this.v = c.sub(a);

        // normalized normal vector of the plane
        this.n = v.cross(u).normalized();
    }

    public Plane(final Vec3 n, final Point supp) {
        // create 2d box which is infinitely large
        super(BBox.INF);

        // set m to support point
        this.m = supp;

        this.n = n;
        // create orthogonal vector by setting one coordinate to 0, switching two others
        // and multiplying one with -1
        this.u = new Vec3(0, -n.z(), n.y());

        // create second orthogonal vector from crossproduct
        this.v = n.cross(u);
    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {

            private Point point = null;
            // r is the distance of the ray
            private float r;

            @Override
            public float getParameter() {
                return r;
            }

            @Override
            public Point getPoint() {
                // TODO Auto-generated method stub
                if (point == null)
                    point = ray.eval(getParameter()).add(n.scale(0.0001f));
                return point;
            }

            @Override
            protected boolean calculateHit() {
                // get direction of ray
                final Vec3 /* normalized */ dir = ray.dir();

                Vec3 p_e = new Vec3(m.x(), m.y(), m.z());
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

}
