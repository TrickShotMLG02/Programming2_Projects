package raytracer.Scenes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import raytracer.core.Camera;
import raytracer.core.LightSource;
import raytracer.core.Obj;
import raytracer.core.PerspectiveCamera;
import raytracer.core.Renderer;
import raytracer.core.Scene;
import raytracer.core.Shader;
import raytracer.core.def.Accelerator;
import raytracer.core.def.BVH;
import raytracer.core.def.PointLightSource;
import raytracer.core.def.StandardObj;
import raytracer.core.def.StandardScene;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Vec3;
import raytracer.shade.ShaderFactory;
import raytracer.shade.SingleColor;

/*
 * File provided by Marcel Ulrich
 */
public class CornellBox {

    private static final boolean usePhong = true;

    private static class MyPanel extends JPanel {

        private final BufferedImage img;

        public MyPanel(final int w, final int h) {
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(img.getWidth(), img.getHeight());
        }

        public void drawPacket(final int x, final int y, final int w, final int h, final int[] data) {
            img.setRGB(x, y, w, h, data, 0, w);
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
        }
    }

    private static Point matVecProd(
            float[][] mat, Point vec) {
        float[] res = new float[3];
        for (int i = 0; i < 3; i++) {
            res[i] = 0;
            for (int j = 0; j < 3; j++) {
                res[i] += mat[i][j] * vec.get(j);
            }
        }
        return new Point(res[0], res[1], res[2]);
    }

    private static void createCube(
            Point pos, Vec3 size, Shader shader, Accelerator accel) {
        createCube(pos, size, shader, accel, new Vec3(0, 0, 0));
    }

    private static void createCube(
            Point pos, Vec3 size, Shader shader, Accelerator accel,
            Vec3 rot) {
        // use 2*6 triangles to create a box
        // similar to obj format
        Point[] points = new Point[8];
        Point[][] triangles = new Point[12][3];
        // points
        for (int xb = 0; xb < 2; xb++) {
            for (int yb = 0; yb < 2; yb++) {
                for (int zb = 0; zb < 2; zb++) {
                    points[xb * 4 + yb * 2 + zb] = new Point(
                            xb * size.get(0) + pos.get(0),
                            yb * size.get(1) + pos.get(1),
                            zb * size.get(2) + pos.get(2));
                }
            }
        }
        // apply rotation
        float a = rot.get(0); // alpha = x axis rotation
        float b = rot.get(1); // beta = y axis rotation
        float c = rot.get(2); // gamma = z axis rotation
        float[][] rotMat = new float[][] {
                new float[] {
                        (float) (Math.cos(b) * Math.cos(c)),
                        (float) (Math.sin(a) * Math.sin(b) * Math.cos(c) - Math.cos(a) * Math.sin(c)),
                        (float) (Math.cos(a) * Math.sin(b) * Math.cos(c) + Math.sin(a) * Math.sin(c))
                },
                new float[] {
                        (float) (Math.cos(b) * Math.sin(c)),
                        (float) (Math.sin(a) * Math.sin(b) * Math.sin(c) + Math.cos(a) * Math.cos(c)),
                        (float) (Math.cos(a) * Math.sin(b) * Math.sin(c) - Math.sin(a) * Math.cos(c))
                },
                new float[] {
                        (float) -Math.sin(b),
                        (float) (Math.sin(a) * Math.cos(b)),
                        (float) (Math.cos(a) * Math.cos(b))
                }
        };

        Point center = new Point(
                pos.get(0) + size.get(0) / 2,
                pos.get(1) + size.get(1) / 2,
                pos.get(2) + size.get(2) / 2);
        for (int i = 0; i < 8; i++) {
            // move to origin
            points[i].sub(center);
            points[i] = matVecProd(rotMat, points[i]);
            // move back
            points[i].sub(center.neg());
        }

        // triangles
        // left
        triangles[0] = new Point[] { points[2], points[1], points[0] };
        triangles[1] = new Point[] { points[1], points[2], points[3] };
        // top
        triangles[2] = new Point[] { points[7], points[3], points[2] };
        triangles[3] = new Point[] { points[2], points[6], points[7] };
        // right
        triangles[4] = new Point[] { points[4], points[5], points[7] };
        triangles[5] = new Point[] { points[7], points[6], points[4] };
        // bottom
        triangles[6] = new Point[] { points[5], points[1], points[0] };
        triangles[7] = new Point[] { points[0], points[4], points[5] };
        // front
        triangles[8] = new Point[] { points[6], points[2], points[0] };
        triangles[9] = new Point[] { points[0], points[4], points[6] };
        // back
        triangles[10] = new Point[] { points[1], points[3], points[7] };
        triangles[11] = new Point[] { points[7], points[5], points[1] };
        // create triangles
        for (

                int i = 0; i < triangles.length; i++) {
            final Primitive tri = GeomFactory.createTriangle(triangles[i][0], triangles[i][1], triangles[i][2]);
            final Obj obj = new StandardObj(tri, shader);
            accel.add(obj);
        }

    }

    public static void main(final String[] args) {
        final int xRes = 640, yRes = 480, packet = 16;
        final MyPanel panel = new MyPanel(xRes, yRes);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame f = new JFrame("Prog2 Raytracer");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(panel);
                f.pack();
                f.setVisible(true);
            }
        });

        // final Color ambient = Color.WHITE.scale(0.05f);
        final Color ambient = Color.WHITE.scale(0.05f);

        final Camera cam = new PerspectiveCamera(
                new Point(0, 5, -7),
                new Point(0, 5, 0),
                new Vec3(0, 5, 0),
                5, 10, 10);
        final Accelerator accel = new BVH();

        // Floor
        {
            final Primitive plane = GeomFactory.createPlane(Vec3.Y, Point.ORIGIN);
            final Shader white = new SingleColor(Color.WHITE);
            final Shader shader = ShaderFactory.createPhong(white, ambient, 0.4f, 1.0f, 15);

            Obj floor;
            if (usePhong)
                floor = new StandardObj(plane, shader);
            else
                floor = new StandardObj(plane, white);
            accel.add(floor);
        }

        // Left wall
        {
            final Primitive plane = GeomFactory.createPlane(Vec3.X, new Point(-5, 0, 0));
            final Shader blue = new SingleColor(Color.BLUE);
            final Shader shader = ShaderFactory.createPhong(blue, ambient, 0.7f, 1.0f, 15);
            Obj leftWall;
            if (usePhong)
                leftWall = new StandardObj(plane, shader);
            else
                leftWall = new StandardObj(plane, blue);

            accel.add(leftWall);
        }

        // Right wall
        {
            final Primitive plane = GeomFactory.createPlane(Vec3.X.neg(), new Point(5, 0, 0));
            final Shader red = new SingleColor(Color.RED);
            final Shader shader = ShaderFactory.createPhong(red, ambient, 0.8f, 0.4f, 1);

            Obj rightWall;
            if (usePhong)
                rightWall = new StandardObj(plane, shader);
            else
                rightWall = new StandardObj(plane, red);
            // accel.add(rightWall);
        }

        // Back wall
        {
            final Primitive plane = GeomFactory.createPlane(Vec3.Z.neg(), new Point(0, 0, 5));
            final Shader green = new SingleColor(Color.GREEN);
            final Shader shader = ShaderFactory.createPhong(green, ambient, 0.5f, 0.0f, 1);

            Obj backWall;

            if (usePhong)
                backWall = new StandardObj(plane, shader);
            else
                backWall = new StandardObj(plane, shader);
            accel.add(backWall);
        }

        // Ceiling wall
        {
            final Primitive plane = GeomFactory.createPlane(Vec3.Y.neg(), new Point(0, 10, 0));
            final Shader white = new SingleColor(Color.WHITE);
            final Shader shader = ShaderFactory.createPhong(white, ambient, 0.4f, 1.0f, 15);

            Obj ceiling;
            if (usePhong)
                ceiling = new StandardObj(plane, shader);
            else
                ceiling = new StandardObj(plane, white);
            // accel.add(ceiling);
        }

        // yellow cube
        {
            final Shader yellow = new SingleColor(Color.YELLOW);
            final Shader shader = ShaderFactory.createPhong(yellow, ambient, 1.0f, 0.2f, 5);

            if (usePhong)
                createCube(
                        new Point(1, 0, 0),
                        new Vec3(3, 3, 3),
                        shader, accel);
            else
                createCube(
                        new Point(1, 0, 0),
                        new Vec3(3, 3, 3),
                        yellow, accel);
        }

        // white cube
        {
            final Shader white = new SingleColor(Color.WHITE);
            final Shader shader = ShaderFactory.createPhong(white, ambient, 1.0f, 5.2f, 5);

            if (usePhong)
                createCube(
                        new Point(-2.0f, 0, 2),
                        new Vec3(3, 6, 3),
                        shader, accel,
                        new Vec3(0, (float) (-45 * Math.PI / 180), 0));
            else
                createCube(
                        new Point(-2.0f, 0, 2),
                        new Vec3(3, 6, 3),
                        white, accel,
                        new Vec3(0, (float) (-45 * Math.PI / 180), 0));
        }

        final List<LightSource> lights = new ArrayList<LightSource>();

        // light pane => grid of lights
        {
            int resolution = 2;
            float dimension = 2.0f;
            float height = 9.5f;
            float intensity = 1.5f;
            for (int i = 0; i < resolution; i++) {
                for (int j = 0; j < resolution; j++) {
                    float x = -dimension / 2 + dimension * i / resolution;
                    float z = -dimension / 2 + dimension * j / resolution;
                    Point p = new Point(x, height, z);
                    LightSource ls = new PointLightSource(p, Color.WHITE.scale(intensity / resolution / resolution));
                    lights.add(ls);
                }
            }
        }

        final Scene scene = new StandardScene(cam, lights, accel);
        final Renderer r = new Renderer(scene, xRes, yRes, 2);

        final Executor exe = Executors.newFixedThreadPool(2);
        final CompletionService<Renderer.Work> ecs = new ExecutorCompletionService<Renderer.Work>(exe);
        int num = 0;
        for (int x = 0; x < xRes; x += packet) {
            for (int y = 0; y < yRes; y += packet) {
                ecs.submit(r.render(x, y, packet, packet));
                num = num + 1;
            }
        }

        for (int i = 0; i < num; i++) {
            try {
                final Renderer.Work w = ecs.take().get();
                panel.drawPacket(w.x, w.y, packet, packet, w.pixels);
                if (i % 100 == 0)
                    panel.repaint();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final ExecutionException e) {
                e.printStackTrace();
            }
        }

        panel.repaint();
        System.out.println("done");
    }

}