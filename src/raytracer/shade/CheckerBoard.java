package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec2;

public class CheckerBoard implements Shader {

    final Shader shaderEven;
    final Shader shaderOdd;
    final float scale;

    public CheckerBoard(final Shader a, final Shader b, final float scale) {

        if (a == null || b == null) {
            throw new IllegalArgumentException("Shader is null");
        }

        if (scale < 0) {
            throw new IllegalArgumentException("invalid scale");
        }

        if (scale == 0) {
            throw new UnsupportedOperationException("scale is 0");
        }

        this.shaderEven = a;
        this.shaderOdd = b;
        this.scale = scale;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        Vec2 uv = hit.getUV();

        int index = (int) (Math.floor((double) (uv.x() / scale)) + Math.floor((double) (uv.y() / scale)));

        if (index % 2 == 0) {
            return shaderEven.shade(hit, trace);
        } else {
            return shaderOdd.shade(hit, trace);
        }

    }

}
