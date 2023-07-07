package raytracer.shade;

import java.util.Collection;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.core.Scene;
import raytracer.math.Color;
import raytracer.core.LightSource;
import raytracer.math.Vec3;
import raytracer.math.Point;

public class Mirror implements Shader {
    private float specular, shininess;

    public Mirror(final float specular,
            final float shininess) {

        this.specular = specular;
        this.shininess = shininess;

    }

    @Override
    public Color shade(Hit hit, Trace trace) {

        Scene scene;
        Trace tempTrace;
        Hit tempHit;
        Collection<LightSource> lightlist;
        Point hitpoint, point_of_ray;
        Vec3 n, v, r;

        // sets the color of I_spec to Black
        Color ispecular = Color.BLACK;

        // gets the coords of the hitpoint
        hitpoint = hit.getPoint();

        // gets the normal vector of the hitpoint
        n = hit.getNormal().normalized();

        // to get the scene
        scene = trace.getScene();

        // to get all lightsources in the scene
        lightlist = scene.getLightSources();

        // for I_diffuse
        for (LightSource lightSource : lightlist) {

            // gets the support point of the light
            point_of_ray = lightSource.getLocation();

            float length = point_of_ray.sub(hitpoint).norm();

            // creates a vector that shows from the hitpoint to the support point of the
            // light
            v = point_of_ray.sub(hitpoint).normalized();

            // calculates the reflected vector r = Reflect (Ray_vec, normal)
            r = trace.getRay().dir().reflect(n).normalized();

            // create a new trace that has ray and points to the source of the current light
            tempTrace = trace.spawn(hitpoint, v);

            // gets the hit of the last trace
            tempHit = tempTrace.getHit();

            // checks if there is nothing between the hitpoint and the lightsource
            if (!tempHit.hits() || length < tempHit.getParameter()) {

                // updates the I_spec = Color_light * material constant * max (0, (r.v) ^ n)
                ispecular = ispecular
                        .add(lightSource.getColor()
                                .scale(specular).scale(Float.max(0, (float) Math.pow(r.dot(v), shininess))));
            }

        }

        return (trace.spawn(hit.getPoint(), hit.getNormal().normalized()).shade()).add(ispecular);
    }

}
