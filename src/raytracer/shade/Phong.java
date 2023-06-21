package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Ray;

public class Phong implements Shader {

    final Shader inner;
    final Color ambient;
    final float diffuse;
    final float specular;
    final float smoothness;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float smoothness) {

        if (diffuse < 0 || specular < 0 || smoothness < 0) {
            throw new IllegalArgumentException("invalid value");
        }

        if (ambient == null || inner == null) {
            throw new IllegalArgumentException("instance null");
        }

        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.smoothness = smoothness;

    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        Color phong = ambient;

        /*
         * 
         * Maybe already implemented somewhere in the project
         * 
         */
        // shoot shadow rays to all light sources and check for obstacles
        for (LightSource light : trace.getScene().getLightSources()) {

            // calculate expected ray length for light source
            float lightDistance = hit.getPoint().sub(light.getLocation()).norm();

            // shoot shadow ray to light source and compare lengths
            Ray ray = new Ray(hit.getPoint(), light.getLocation().sub(hit.getPoint()).normalized());

            // maybe reflect the ray back to the inverse direction of the original ray
            trace.getRay().reflect(hit.getPoint(), ray.dir().inv());

            // TODO
            // check if ray reaches the light source after a specific distance, or if it
            // hits something else on its way

        }

        Color diffusion = null;
        // color of light source dot color of underlying shader * konstant of material
        // diffuse * max (0, reflection vector, vector of light source) ^ gloss factor

        // sum up for all lights

        phong.add(diffusion);

        // vector from light source to hit point on surface

        Color specularity = null;
        phong.add(specularity);

        return phong;
    }

}
