#include "file_io.h"

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "util.h"

int image_read(image_t *img, FILE *fin) {

    char format_expected [3] = "P3\0";                // define string variable for 2 chars and terminator
    char format_grabbed [4];
    int width;
    int height;
    //int memory_Space_Needed;        // calculated from image size multiplied by struct size for each pixel


    // read first line which should contain filetype, compare it to format[]
    fscanf(fin, "%3s\n", format_grabbed);
    if (strncmp(format_expected, format_grabbed, 3))
        return -1;
    else
    {
        // read second line and grab the two integer numbers for width and height
        fscanf(fin, "%d %d\n", &width ,&height);

        // Store height and width in struct
        img->w = width;
        img->h = height;

        // allocate space for (width * height) pixels
        img->img = malloc(width * height * sizeof(pixel_t));

        int maxColorValue;

        // Store max color value from file in variable
        fscanf(fin, "%d\n", &maxColorValue);

        // loop over all pixels
        for(int i = 0; i < width * height; i++)
        {    
            if (img->img != NULL)
            {
                // read color values from file and store to r,g,b of pixel i in struct
                fscanf(fin, "%d %d %d", &img->img[i].r, &img->img[i].g, &img->img[i].b);
            }
        }

        return 0;
    }

    //NOT_IMPLEMENTED;
    //UNUSED(img);
    //UNUSED(fin);
    //return -1;
}

void image_write(const image_t *img, FILE *fout) {

    // write format, width, height and color resolution to file
    fprintf(fout, "%s", "P3\n\0");
    fprintf(fout, "%d %d %s", img->w, img->h, "\n\0");
    fprintf(fout, "%d %s", 255, "\n\0");


    // write all pixels to file by iterating all pixels in img struct
    for (int i = 0; i < img->h * img->w; i++)
    {
        // Check if there might be a null pointer exception
        if (img->img != NULL)
            fprintf(fout, "%d %d %d ", img->img[i].r, img->img[i].g, img->img[i].b);
    }

    // write terminator
    fprintf(fout, "%s", "\0");

    // save and close file
    fclose(fout);


    //NOT_IMPLEMENTED;
    //UNUSED(img);
    //UNUSED(fout);
}

void image_free(image_t *img) {
    if (img->img != NULL) {
        free(img->img);
    }
    img->w = img->h = 0;
    img->img = NULL;
}
