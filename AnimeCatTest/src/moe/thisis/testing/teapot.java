package moe.thisis.testing;

import java.applet.Applet;
import java.awt.*;
import java.text.*;
import go.*;
import goText.*;

class GoTeapot extends GoInterface
{
    // Data & text.
    GoTriangles data;
    GoText      frameRateText;

    // View variables.
    double  eyeX;
    double  eyeY;
    double  eyeZ;
    double  centerX;
    double  centerY;
    double  centerZ;
    double  upX;
    double  upY;
    double  upZ;
    double  radius;
    boolean perspective;

    // Timing variables.
    long  time;
    long  startTime;
    long  lastTime;
    long  secondsPassed;
    int   frameCount;

    // Coordinate reference.
    final static int X = 0;
    final static int Y = 1;
    final static int Z = 2;
    final static int W = 3;
    
    // Used to "start" and "stop" applet animation.
    boolean doBenchmark = true;
    
    GoTeapot()
    {   
        //
        // Background and foreground colors.
        //
        go.background(0.078, 0.361, 0.753);
        go.color(1.0, 0.5, 0.1);
        
        //
        // Enable a light. (Use the default values)
        //
        go.light(Go.LIGHT_0, true);

        //
        // Create the teapot data.
        //
        createTeapot();

        //
        // Initialize the frames/sec variables.
        //
        frameRateText = new GoText(4.9, 0.9, 0.0,                           // position
                                   1.0, 0.0,                                // path
                                   0.58, 0.58,                              // scale
                                   GoText.LEFT_CENTER,                      // alignment
                                   new GoHershey("hersheyFonts/futura.l"),  // font
                                   "");                                     // text (nothing for now)
        startTime = getTimeMillis();
        lastTime = startTime;
        frameCount = 0;
        secondsPassed = 0;

        //
        // Calculate the teapot's bounding box.
        //
        go.renderMode(Go.BOUND_BOX);
        go.render(data);
        go.renderMode(Go.IMAGE);
        GoBoundBox boundBox = go.boundBox();
        double xMin = boundBox.xMin();
        double yMin = boundBox.yMin();
        double zMin = boundBox.zMin();
        double xMax = boundBox.xMax();
        double yMax = boundBox.yMax();
        double zMax = boundBox.zMax();

        //
        // Define the initial view.
        //

        eyeX        = 4.86;
        eyeY        = 7.2;
        eyeZ        = 7.4;
        centerX     = (xMin + xMax) / 2.0;
        centerY     = (yMin + yMax) / 2.0;
        centerZ     = (zMin + zMax) / 2.0;
        upX         = 0.0;
        upY         = 0.0;
        upZ         = 1.0;
        radius      = 4.3;

        perspective = true;
        
        go.lookAt(eyeX, eyeY, eyeZ,
                  centerX, centerY, centerZ,
                  upX, upY, upZ);
    }

    //
    // Returns the current time in milliseconds.
    //
    long getTimeMillis()
    {
        return System.currentTimeMillis();
    }

    //
    // Sets the frame rate text displayed in the lower left corner.
    //
    void setFrameRateText()
    {
        NumberFormat nf = NumberFormat.getInstance();
        
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        frameRateText.text("frames/sec = " +
                           nf.format((double)frameCount/((double)(time - lastTime)/1000.0)));
    }
    
    //
    // Interface render method.
    //
    public void render()
    {
        //
        // Calculate the frames per second.
        //

        frameCount++;

        time = getTimeMillis();
        
        if(secondsPassed != (time - startTime) / 1000) {
            setFrameRateText();
            lastTime = time;
            secondsPassed = (time - startTime) / 1000;
            frameCount = 0;
        }
        
        //
        // Clear the image.
        //
        go.clear(Go.IMAGE);

        //
        // Draw the teapot.
        //
        go.push(Go.MODELVIEW);
        go.translate(centerX, centerY, centerZ);
        go.rotate((time - startTime) / 1000.0 * 30.0, -0.4, -0.4, 1);     // Rotate at 30 degrees/sec
        go.translate(-centerX, -centerY, -centerZ);
        go.render(data);
	go.pop(Go.MODELVIEW);

        //
        // Draw the frames/sec text.
        //
	go.push(Go.COLOR);
	go.color(1.0, 1.0, 0.0);
        frameRateText.render(go);
	go.pop(Go.COLOR);

        //
        // Display all that has been drawn, and do it again.
        //
        swap();
        if(doBenchmark) {
            rerender();
        }
    }

    //
    // Interface resized method.
    //
    public void resized(int width, int height)
    {
        double dx = eyeX - centerX;
        double dy = eyeY - centerY;
        double dz = eyeZ - centerZ;
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double fovy = 2.0 * 180.0/Math.PI * Math.atan(radius / d);
        double near = d - radius;
        double far  = d + radius;

        double xUnits, yUnits;

        if(width >= height) {
            xUnits = 2 * radius * width / height;
            yUnits = 2 * radius;
        }
        else {
            xUnits = 2 * radius;
            yUnits = 2 * radius * height / width;
        }

        // setup projection matrix
        go.matrixMode(Go.PROJECTION);
        go.identity();
        if(perspective) {
            if(width >= height) {
                go.perspective(fovy, xUnits / yUnits, near, far);
            }
            else {
                double r = radius * yUnits / xUnits;
                double fovyModified = Math.atan(r / d) * 180.0 / Math.PI * 2.0;
                go.perspective(fovyModified, xUnits / yUnits, near, far);
            }
        }
        else {
            go.ortho(-xUnits / 2.0, xUnits / 2.0,
                     -yUnits / 2.0, yUnits / 2.0,
                     near, far);
        }
        go.matrixMode(Go.MODELVIEW);
    }

    //
    // Creates the teapot data.
    //
    void createTeapot()
    {
        // Resolution of teapot.
        int SIZE_FACTOR = 5;
        
        // Number of spline surfaces that define the teapot.
        int NUM_PATCHES = 32;

        int patches[][][] = {
            /* rim */
            {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}},
            {{3, 16, 17, 18}, {7, 19, 20, 21}, {11, 22, 23, 24}, {15, 25, 26, 27}},
            {{18, 28, 29, 30}, {21, 31, 32, 33}, {24, 34, 35, 36}, {27, 37, 38, 39}},
            {{30, 40, 41, 0}, {33, 42, 43, 4}, {36, 44, 45, 8}, {39, 46, 47, 12}},
            /* body */
            {{12, 13, 14, 15}, {48, 49, 50, 51}, {52, 53, 54, 55}, {56, 57, 58, 59}},
            {{15, 25, 26, 27}, {51, 60, 61, 62}, {55, 63, 64, 65}, {59, 66, 67, 68}},
            {{27, 37, 38, 39}, {62, 69, 70, 71}, {65, 72, 73, 74}, {68, 75, 76, 77}},
            {{39, 46, 47, 12}, {71, 78, 79, 48}, {74, 80, 81, 52}, {77, 82, 83, 56}},
            {{56, 57, 58, 59}, {84, 85, 86, 87}, {88, 89, 90, 91}, {92, 93, 94, 95}},
            {{59, 66, 67, 68}, {87, 96, 97, 98}, {91, 99, 100, 101}, {95, 102, 103, 104}},
            {{68, 75, 76, 77}, {98, 105, 106, 107}, {101, 108, 109, 110}, {104, 111, 112, 113}},
            {{77, 82, 83, 56}, {107, 114, 115, 84}, {110, 116, 117, 88}, {113, 118, 119, 92}},
            /* handle */
            {{120, 121, 122, 123}, {124, 125, 126, 127}, {128, 129, 130, 131}, {132, 133, 134, 135}},
            {{123, 136, 137, 120}, {127, 138, 139, 124}, {131, 140, 141, 128}, {135, 142, 143, 132}},
            {{132, 133, 134, 135}, {144, 145, 146, 147}, {148, 149, 150, 151}, {68, 152, 153, 154}},
            {{135, 142, 143, 132}, {147, 155, 156, 144}, {151, 157, 158, 148}, {154, 159, 160, 68}},
            /* spout */
            {{161, 162, 163, 164}, {165, 166, 167, 168}, {169, 170, 171, 172}, {173, 174, 175, 176}},
            {{164, 177, 178, 161}, {168, 179, 180, 165}, {172, 181, 182, 169}, {176, 183, 184, 173}},
            {{173, 174, 175, 176}, {185, 186, 187, 188}, {189, 190, 191, 192}, {193, 194, 195, 196}},
            {{176, 183, 184, 173}, {188, 197, 198, 185}, {192, 199, 200, 189}, {196, 201, 202, 193}},
            /* lid */
            {{203, 203, 203, 203}, {204, 205, 206, 207}, {208, 208, 208, 208}, {209, 210, 211, 212}},
            {{203, 203, 203, 203}, {207, 213, 214, 215}, {208, 208, 208, 208}, {212, 216, 217, 218}},
            {{203, 203, 203, 203}, {215, 219, 220, 221}, {208, 208, 208, 208}, {218, 222, 223, 224}},
            {{203, 203, 203, 203}, {221, 225, 226, 204}, {208, 208, 208, 208}, {224, 227, 228, 209}},
            {{209, 210, 211, 212}, {229, 230, 231, 232}, {233, 234, 235, 236}, {237, 238, 239, 240}},
            {{212, 216, 217, 218}, {232, 241, 242, 243}, {236, 244, 245, 246}, {240, 247, 248, 249}},
            {{218, 222, 223, 224}, {243, 250, 251, 252}, {246, 253, 254, 255}, {249, 256, 257, 258}},
            {{224, 227, 228, 209}, {252, 259, 260, 229}, {255, 261, 262, 233}, {258, 263, 264, 237}},
            /* bottom */
            {{265, 265, 265, 265}, {266, 267, 268, 269}, {270, 271, 272, 273}, {92, 119, 118, 113}},
            {{265, 265, 265, 265}, {269, 274, 275, 276}, {273, 277, 278, 279}, {113, 112, 111, 104}},
            {{265, 265, 265, 265}, {276, 280, 281, 282}, {279, 283, 284, 285}, {104, 103, 102, 95}},
            {{265, 265, 265, 265}, {282, 286, 287, 266}, {285, 288, 289, 270}, {95, 94, 93, 92}}
        };

        double verts[][] = {
            {1.4, 0, 2.4}, {1.4, -0.784, 2.4}, {0.784, -1.4, 2.4}, {0, -1.4, 2.4},
            {1.3375, 0, 2.53125}, {1.3375, -0.749, 2.53125}, {0.749, -1.3375, 2.53125},
            {0, -1.3375, 2.53125}, {1.4375, 0, 2.53125}, {1.4375, -0.805, 2.53125},
            {0.805, -1.4375, 2.53125}, {0, -1.4375, 2.53125}, {1.5, 0, 2.4}, {1.5, -0.84, 2.4},
            {0.84, -1.5, 2.4}, {0, -1.5, 2.4}, {-0.784, -1.4, 2.4}, {-1.4, -0.784, 2.4},
            {-1.4, 0, 2.4}, {-0.749, -1.3375, 2.53125}, {-1.3375, -0.749, 2.53125},
            {-1.3375, 0, 2.53125}, {-0.805, -1.4375, 2.53125}, {-1.4375, -0.805, 2.53125},
            {-1.4375, 0, 2.53125}, {-0.84, -1.5, 2.4}, {-1.5, -0.84, 2.4}, {-1.5, 0, 2.4},
            {-1.4, 0.784, 2.4}, {-0.784, 1.4, 2.4}, {0, 1.4, 2.4}, {-1.3375, 0.749, 2.53125},
            {-0.749, 1.3375, 2.53125}, {0, 1.3375, 2.53125}, {-1.4375, 0.805, 2.53125},
            {-0.805, 1.4375, 2.53125}, {0, 1.4375, 2.53125}, {-1.5, 0.84, 2.4}, {-0.84, 1.5, 2.4},
            {0, 1.5, 2.4}, {0.784, 1.4, 2.4}, {1.4, 0.784, 2.4}, {0.749, 1.3375, 2.53125},
            {1.3375, 0.749, 2.53125}, {0.805, 1.4375, 2.53125}, {1.4375, 0.805, 2.53125},
            {0.84, 1.5, 2.4}, {1.5, 0.84, 2.4}, {1.75, 0, 1.875}, {1.75, -0.98, 1.875},
            {0.98, -1.75, 1.875}, {0, -1.75, 1.875}, {2, 0, 1.35}, {2, -1.12, 1.35},
            {1.12, -2, 1.35}, {0, -2, 1.35}, {2, 0, 0.9}, {2, -1.12, 0.9}, {1.12, -2, 0.9},
            {0, -2, 0.9}, {-0.98, -1.75, 1.875}, {-1.75, -0.98, 1.875}, {-1.75, 0, 1.875},
            {-1.12, -2, 1.35}, {-2, -1.12, 1.35}, {-2, 0, 1.35}, {-1.12, -2, 0.9}, {-2, -1.12, 0.9},
            {-2, 0, 0.9}, {-1.75, 0.98, 1.875}, {-0.98, 1.75, 1.875}, {0, 1.75, 1.875},
            {-2, 1.12, 1.35}, {-1.12, 2, 1.35}, {0, 2, 1.35}, {-2, 1.12, 0.9}, {-1.12, 2, 0.9},
            {0, 2, 0.9}, {0.98, 1.75, 1.875}, {1.75, 0.98, 1.875}, {1.12, 2, 1.35}, {2, 1.12, 1.35},
            {1.12, 2, 0.9}, {2, 1.12, 0.9}, {2, 0, 0.45}, {2, -1.12, 0.45}, {1.12, -2, 0.45},
            {0, -2, 0.45}, {1.5, 0, 0.225}, {1.5, -0.84, 0.225}, {0.84, -1.5, 0.225},
            {0, -1.5, 0.225}, {1.5, 0, 0.15}, {1.5, -0.84, 0.15}, {0.84, -1.5, 0.15},
            {0, -1.5, 0.15}, {-1.12, -2, 0.45}, {-2, -1.12, 0.45}, {-2, 0, 0.45},
            {-0.84, -1.5, 0.225},{-1.5, -0.84, 0.225}, {-1.5, 0, 0.225}, {-0.84, -1.5, 0.15},
            {-1.5, -0.84, 0.15}, {-1.5, 0, 0.15}, {-2, 1.12, 0.45}, {-1.12, 2, 0.45}, {0, 2, 0.45},
            {-1.5, 0.84, 0.225}, {-0.84, 1.5, 0.225}, {0, 1.5, 0.225}, {-1.5, 0.84, 0.15},
            {-0.84, 1.5, 0.15}, {0, 1.5, 0.15}, {1.12, 2, 0.45}, {2, 1.12, 0.45}, {0.84, 1.5, 0.225},
            {1.5, 0.84, 0.225}, {0.84, 1.5, 0.15}, {1.5, 0.84, 0.15}, {-1.6, 0, 2.025},
            {-1.6, -0.3, 2.025}, {-1.5, -0.3, 2.25}, {-1.5, 0, 2.25}, {-2.3, 0, 2.025},
            {-2.3, -0.3, 2.025}, {-2.5, -0.3, 2.25}, {-2.5, 0, 2.25}, {-2.7, 0, 2.025},
            {-2.7, -0.3, 2.025}, {-3, -0.3, 2.25}, {-3, 0, 2.25}, {-2.7, 0, 1.8}, {-2.7, -0.3, 1.8},
            {-3, -0.3, 1.8}, {-3, 0, 1.8}, {-1.5, 0.3, 2.25}, {-1.6, 0.3, 2.025}, {-2.5, 0.3, 2.25},
            {-2.3, 0.3, 2.025}, {-3, 0.3, 2.25}, {-2.7, 0.3, 2.025}, {-3, 0.3, 1.8}, {-2.7, 0.3, 1.8},
            {-2.7, 0, 1.575}, {-2.7, -0.3, 1.575}, {-3, -0.3, 1.35}, {-3, 0, 1.35}, {-2.5, 0, 1.125},
            {-2.5, -0.3, 1.125}, {-2.65, -0.3, 0.9375}, {-2.65, 0, 0.9375}, {-2, -0.3, 0.9},
            {-1.9, -0.3, 0.6}, {-1.9, 0, 0.6}, {-3, 0.3, 1.35}, {-2.7, 0.3, 1.575},
            {-2.65, 0.3, 0.9375}, {-2.5, 0.3, 1.125}, {-1.9, 0.3, 0.6}, {-2, 0.3, 0.9}, {1.7, 0, 1.425},
            {1.7, -0.66, 1.425}, {1.7, -0.66, 0.6}, {1.7, 0, 0.6}, {2.6, 0, 1.425}, {2.6, -0.66, 1.425},
            {3.1, -0.66, 0.825}, {3.1, 0, 0.825}, {2.3, 0, 2.1}, {2.3, -0.25, 2.1}, {2.4, -0.25, 2.025},
            {2.4, 0, 2.025}, {2.7, 0, 2.4}, {2.7, -0.25, 2.4}, {3.3, -0.25, 2.4}, {3.3, 0, 2.4},
            {1.7, 0.66, 0.6}, {1.7, 0.66, 1.425}, {3.1, 0.66, 0.825}, {2.6, 0.66, 1.425},
            {2.4, 0.25, 2.025}, {2.3, 0.25, 2.1}, {3.3, 0.25, 2.4}, {2.7, 0.25, 2.4}, {2.8, 0, 2.475},
            {2.8, -0.25, 2.475}, {3.525, -0.25, 2.49375}, {3.525, 0, 2.49375}, {2.9, 0, 2.475},
            {2.9, -0.15, 2.475}, {3.45, -0.15, 2.5125}, {3.45, 0, 2.5125}, {2.8, 0, 2.4},
            {2.8, -0.15, 2.4}, {3.2, -0.15, 2.4}, {3.2, 0, 2.4}, {3.525, 0.25, 2.49375},
            {2.8, 0.25, 2.475}, {3.45, 0.15, 2.5125}, {2.9, 0.15, 2.475}, {3.2, 0.15, 2.4},
            {2.8, 0.15, 2.4}, {0, 0, 3.15}, {0.8, 0, 3.15}, {0.8, -0.45, 3.15}, {0.45, -0.8, 3.15},
            {0, -0.8, 3.15}, {0, 0, 2.85}, {0.2, 0, 2.7}, {0.2, -0.112, 2.7}, {0.112, -0.2, 2.7},
            {0, -0.2, 2.7}, {-0.45, -0.8, 3.15}, {-0.8, -0.45, 3.15}, {-0.8, 0, 3.15},
            {-0.112, -0.2, 2.7}, {-0.2, -0.112, 2.7}, {-0.2, 0, 2.7}, {-0.8, 0.45, 3.15},
            {-0.45, 0.8, 3.15}, {0, 0.8, 3.15}, {-0.2, 0.112, 2.7}, {-0.112, 0.2, 2.7}, {0, 0.2, 2.7},
            {0.45, 0.8, 3.15}, {0.8, 0.45, 3.15}, {0.112, 0.2, 2.7}, {0.2, 0.112, 2.7}, {0.4, 0, 2.55},
            {0.4, -0.224, 2.55}, {0.224, -0.4, 2.55}, {0, -0.4, 2.55}, {1.3, 0, 2.55},
            {1.3, -0.728, 2.55}, {0.728, -1.3, 2.55}, {0, -1.3, 2.55}, {1.3, 0, 2.4}, {1.3, -0.728, 2.4},
            {0.728, -1.3, 2.4}, {0, -1.3, 2.4}, {-0.224, -0.4, 2.55}, {-0.4, -0.224, 2.55},
            {-0.4, 0, 2.55}, {-0.728, -1.3, 2.55}, {-1.3, -0.728, 2.55}, {-1.3, 0, 2.55},
            {-0.728, -1.3, 2.4}, {-1.3, -0.728, 2.4}, {-1.3, 0, 2.4}, {-0.4, 0.224, 2.55},
            {-0.224, 0.4, 2.55}, {0, 0.4, 2.55}, {-1.3, 0.728, 2.55}, {-0.728, 1.3, 2.55},
            {0, 1.3, 2.55}, {-1.3, 0.728, 2.4}, {-0.728, 1.3, 2.4}, {0, 1.3, 2.4}, {0.224, 0.4, 2.55},
            {0.4, 0.224, 2.55}, {0.728, 1.3, 2.55}, {1.3, 0.728, 2.55}, {0.728, 1.3, 2.4},
            {1.3, 0.728, 2.4}, {0, 0, 0}, {1.425, 0, 0}, {1.425, 0.798, 0}, {0.798, 1.425, 0},
            {0, 1.425, 0}, {1.5, 0, 0.075}, {1.5, 0.84, 0.075}, {0.84, 1.5, 0.075}, {0, 1.5, 0.075},
            {-0.798, 1.425, 0}, {-1.425, 0.798, 0}, {-1.425, 0, 0}, {-0.84, 1.5, 0.075},
            {-1.5, 0.84, 0.075}, {-1.5, 0, 0.075}, {-1.425, -0.798, 0}, {-0.798, -1.425, 0},
            {0, -1.425, 0}, {-1.5, -0.84, 0.075}, {-0.84, -1.5, 0.075}, {0, -1.5, 0.075},
            {0.798, -1.425, 0}, {1.425, -0.798, 0}, {0.84, -1.5, 0.075}, {1.5, -0.84, 0.075}
        };

        /* bezier form */
        double ms[][] = { {-1.0,  3.0, -3.0,  1.0},
                          { 3.0, -6.0,  3.0,  0.0},
                          {-3.0,  3.0,  0.0,  0.0},
                          { 1.0,  0.0,  0.0,  0.0} } ;
        int          surf, i, r, c, sstep, tstep, num_tri, num_vert, num_tri_vert;
        double[]     s = new double[3];
        double[]     t = new double[3];
        double[][]   vert = new double[4][3];
        double[][]   norm = new double[4][3];
        double[][]   mst = new double[4][4];
        double[][]   g = new double[4][4];
        double[][][] mgm = new double[3][4][4];
        double[][]   tmtx = new double[4][4];

        int count = 0;

        data = new GoTriangles(NUM_PATCHES * SIZE_FACTOR * SIZE_FACTOR * 6, Go.NORMAL);
    
        transpose_matrix( mst, ms ) ;
    
        for ( surf = 0 ; surf < NUM_PATCHES ; surf++ ) {
        
            /* get M * G * M matrix for x,y,z */
            for ( i = 0 ; i < 3 ; i++ ) {
                /* get control patches */
                for ( r = 0 ; r < 4 ; r++ ) {
                    for ( c = 0 ; c < 4 ; c++ ) {
                        g[r][c] = verts[patches[surf][r][c]][i] ;
                    }
                }
                
                matrix_multiply( tmtx, ms, g ) ;
                matrix_multiply( mgm[i], tmtx, mst ) ;
            }
        
            /* step along, get points, and output */
            for ( sstep = 0 ; sstep < SIZE_FACTOR ; sstep++ ) {
                for ( tstep = 0 ; tstep < SIZE_FACTOR ; tstep++ ) {
                    for ( num_tri = 0 ; num_tri < 2 ; num_tri++ ) {
                        for ( num_vert = 0 ; num_vert < 3 ; num_vert++ ) {
                            num_tri_vert = ( num_vert + num_tri * 2 ) % 4 ;
                            /* trickiness: add 1 to sstep if 1 or 2 */
                            s[num_vert] = (double)(sstep + (num_tri_vert/2 != 0 ? 1:0) )
                                / (double)SIZE_FACTOR ;
                            /* trickiness: add 1 to tstep if 2 or 3 */
                            t[num_vert] = (double)(tstep + ((num_tri_vert%3) != 0 ? 1:0) )
                                / (double)SIZE_FACTOR ;
                        }

                        points_from_basis( 3, s, t, mgm, vert, norm );

                        /* don't output degenerate triangles */
                        if ( check_for_cusp( 3, vert, norm ) ) {
                            for (i = 0; i < 3; ++i) {
                                data.xyz(count+i, vert[i][X], vert[i][Y], vert[i][Z]);
                                data.ijk(count+i, norm[i][X], norm[i][Y], norm[i][Z]);
                            }
                            count += 3;
                        }
                    }
                }
            }
        }

        /* remove vertices not needed due to degenerate triangles */
        data.remove(count, data.vertexNumber() - count);
    }

    //
    // Normalize the vector (X,Y,Z) so that X*X + Y*Y + Z*Z = 1.
    //
    // The normalization divisor is returned.  If the divisor is zero, no
    // normalization occurs.
    //
    double normalize_vector(double cvec[])
    {
        double divisor;
        
        divisor = Math.sqrt( (double)DOT_PRODUCT(cvec, cvec) );
        if (divisor > 0.0) {
            cvec[X] /= divisor;
            cvec[Y] /= divisor;
            cvec[Z] /= divisor;
        }
        return divisor;
    }

    //
    // Multiply a 4 element vector by a matrix.  Typically used for
    // homogenous transformation from world space to screen space.
    //
    void transform_coord(double vres[], double vec[], double mx[][])
    {
        double[] vtemp = new double[4];

        vtemp[X] = vec[X]*mx[0][0]+vec[Y]*mx[1][0]+vec[Z]*mx[2][0]+vec[W]*mx[3][0];
        vtemp[Y] = vec[X]*mx[0][1]+vec[Y]*mx[1][1]+vec[Z]*mx[2][1]+vec[W]*mx[3][1];
        vtemp[Z] = vec[X]*mx[0][2]+vec[Y]*mx[1][2]+vec[Z]*mx[2][2]+vec[W]*mx[3][2];
        vtemp[W] = vec[X]*mx[0][3]+vec[Y]*mx[1][3]+vec[Z]*mx[2][3]+vec[W]*mx[3][3];
        vres[X] = vtemp[X];
        vres[Y] = vtemp[Y];
        vres[Z] = vtemp[Z];
        vres[W] = vtemp[W];
    }

    //
    // Compute transpose of matrix.
    //
    void transpose_matrix( double mxres[][], double mx[][] )
    {
        int i, j;
        
        for (i=0;i<4;i++)
            for (j=0;j<4;j++)
                mxres[j][i] = mx[i][j];
    }

    //
    // Multiply two 4x4 matrices.  Note that mxres had better not be
    // the same as either mx1 or mx2 or bad results will be returned.
    //
    void matrix_multiply(double mxres[][], double mx1[][], double mx2[][])
    {
        int i, j;
        
        for (i=0;i<4;i++)
            for (j=0;j<4;j++)
                mxres[i][j] = mx1[i][0]*mx2[0][j] + mx1[i][1]*mx2[1][j] +
                    mx1[i][2]*mx2[2][j] + mx1[i][3]*mx2[3][j];
    }
    
    //
    // At the center of the lid's handle and at bottom are cusp points -
    // check if normal is (0 0 0), if so, check that polygon is not degenerate.
    // If degenerate, return FALSE, else set normal.
    //
    boolean check_for_cusp(int tot_vert, double vert[][], double norm[][])
    {
        int  count, i, nv = 0;
        
        for ( count = 0, i = tot_vert ; i-- != 0 ; ) {
            /* check if vertex is at cusp */
            if ( IS_VAL_ALMOST_ZERO( vert[i][X], 0.0001 ) &&
                 IS_VAL_ALMOST_ZERO( vert[i][Y], 0.0001 ) ) {
                count++ ;
                nv = i ;
            }
        }
        
        if ( count > 1 ) {
            /* degenerate */
            return( false ) ;
        }
        if ( count == 1 ) {
            /* check if point is somewhere above the middle of the teapot */
            if ( vert[nv][Z] > 1.5 ) {
                /* cusp at lid */
                SET_COORD3( norm[nv], 0.0, 0.0, 1.0 ) ;
            } else {
                /* cusp at bottom */
                SET_COORD3( norm[nv], 0.0, 0.0, -1.0 ) ;
            }
        }
        return( true ) ;
    }

    void points_from_basis(int tot_vert, double s[], double t[], double mgm[][][],
                           double vert[][], double norm[][])
    {
        int       i, num_vert, p;
        double    sval, tval, dsval = 1.0, dtval = 1.0, sxyz, txyz;
        double[]  sdir = new double[3];
        double[]  tdir = new double[3];
        double[]  sp = new double[4];
        double[]  tp = new double[4];
        double[]  dsp = new double[4];
        double[]  dtp = new double[4];
        double[]  tcoord = new double[4];
        
        for ( num_vert = 0 ; num_vert < tot_vert ; num_vert++ ) {
                
            sxyz = s[num_vert] ;
            txyz = t[num_vert] ;
                
            /* get power vectors and their derivatives */
            for ( p = 4, sval = tval = 1.0 ; p-- != 0; ) {
                sp[p] = sval ;
                tp[p] = tval ;
                sval *= sxyz ;
                tval *= txyz ;
                        
                if ( p == 3 ) {
                    dsp[p] = dtp[p] = 0.0 ;
                    dsval = dtval = 1.0 ;
                } else {
                    dsp[p] = dsval * (double)(3-p) ;
                    dtp[p] = dtval * (double)(3-p) ;
                    dsval *= sxyz ;
                    dtval *= txyz ;
                }
            }
                
            /* do for x,y,z */
            for ( i = 0 ; i < 3 ; i++ ) {
                /* multiply power vectors times matrix to get value */
                transform_coord( tcoord, sp, mgm[i] ) ;
                vert[num_vert][i] = DOT4( tcoord, tp ) ;
                        
                /* get s and t tangent vectors */
                transform_coord( tcoord, dsp, mgm[i] ) ;
                sdir[i] = DOT4( tcoord, tp ) ;
                        
                transform_coord( tcoord, sp, mgm[i] ) ;
                tdir[i] = DOT4( tcoord, dtp ) ;
            }
                
            /* find normal */
            CROSS( norm[num_vert], tdir, sdir ) ;
            normalize_vector( norm[num_vert] ) ;
        }
    }

    final void CROSS(double[] r, double[] a, double[] b)
    {
        r[X] = a[Y] * b[Z] - a[Z] * b[Y];
        r[Y] = a[Z] * b[X] - a[X] * b[Z];
        r[Z] = a[X] * b[Y] - a[Y] * b[X];
    }

    final double DOT4(double[] a, double[] b)
    {
        return a[X] * b[X] + a[Y] * b[Y] + a[Z] * b[Z] + a[W] * b[W];
    }

    final boolean IS_VAL_ALMOST_ZERO(double A, double E)
    {
        return ( (A) < 0 ? -(A) : (A) ) <= (E);
    }

    final void SET_COORD3(double r[], double A, double B, double C)
    {
        r[X] = A;
        r[Y] = B;
        r[Z] = C;
    }

    final double DOT_PRODUCT(double a[], double b[])
    {
        return a[X] * b[X] +
            a[Y] * b[Y] +
            a[Z] * b[Z];
    }
};

public class teapot extends Applet
{
    GoTeapot teapot;

    public void init()
    {
        // Fonts will be found relative to the html document
        // that includes this applet.
        GoFont.path(getDocumentBase());

        setLayout(new GridLayout(1, 1));
        add(teapot = new GoTeapot());
    }

    public void start()
    {
        teapot.doBenchmark = true;
        teapot.rerender();
    }

    public void stop()
    {
        teapot.doBenchmark = false;
    }
}
