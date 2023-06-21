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

        // shoot shadow rays to all light sources and check for obstacles
        for (LightSource light : trace.getScene().getLightSources()) {

            // calculate expected ray length for light source
            float lightDistance = hit.getPoint().sub(light.getLocation()).norm();

            // shoot shadow ray to light source and compare lengths
            Ray ray = new Ray(hit.getPoint(), light.getLocation().sub(hit.getPoint()).normalized());

            // TODO
            // check if ray was successful

        }

        Color diffusion = null;
        phong.add(diffusion);

        Color specularity = null;
        phong.add(specularity);

        return phong;
    }

}
