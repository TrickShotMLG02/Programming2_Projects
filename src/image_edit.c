#include "image_edit.h"

#include <stdlib.h>

#include "structures.h"
#include "util.h"

void rotate_counterclockwise(image_t *img) {
    // rotate counterclockwise by rotating 3 times clockwise
    rotate_clockwise(img);
    rotate_clockwise(img);
    rotate_clockwise(img);
}



void rotate_clockwise(image_t *img) {
    int width = img->w;
    int iterations = width - 1;

    pixel_t tmp;

    int row = 0;
    int column = 0;
    int ctr = 0;

    for (row = row; row < img->h / 2; row++) {
        for (column = ctr; column < iterations; column++) {
            // calculate current pos from row and col
            int pos0 = column + width * row;
            // store current element in tmp
            tmp = img->img[pos0];

            // calculate next array index in pos1 to grab data from and store in current position
            int c1 = width - 1 - row;
            int r1 = column;
            int pos1 = c1 + width * r1;

            // calculate next array index in pos2 to grab data from and store in current position
            int c2 = width - 1 - r1;
            int r2 = c1;
            int pos2 = c2 + width * r2;

            // calculate next array index in pos2 to grab data from and store in current position
            int c3 = width - 1 - r2;
            int r3 = c2;
            int pos3 = c3 + width * r3;


            // Now we need to copy pos0 to pos1, pos1 to pos2, pos2 to pos3, pos3 to pos0
            // Thus save pos3 to tmp
            tmp = img->img[pos3];

            //store pos 2 to pos 3
            img->img[pos3] = img->img[pos2];

            //store pos 1 to pos 2
            img->img[pos2] = img->img[pos1];

            //store pos 0 to pos 1
            img->img[pos1] = img->img[pos0];

            //store tmp to pos 0
            img->img[pos0] = tmp;
        }
        ctr++;
        iterations -= 1;
        column = ctr;
    }
}


void mirror_horizontal(image_t *img) {

    // grab image width and divide it by 2 to get iterations needed to switch pixels per horizontal line
    int iterations = img->w / 2;
    int width = img->w;

    pixel_t tmp;

    for (int row = 0; row < img->h; row++) {
        for (int column = 0; column < iterations; column++)
        {
            // store pixel in column to tmp, copy pixel from (width - column) to column and store tmp to (width - column)
            // store pixel of column to tmp
            tmp = img->img[row * width + column];
            // copy pixel from width - column to column
            img->img[row * width + column] = img->img[row * width + (width - 1 - column)];
            // store tmp to width - column
            img->img[row * width + (width - 1 - column)] = tmp;
        }
    }
}

void mirror_vertical(image_t *img) {
    // grab image width and divide it by 2 to get iterations needed to switch pixels per horizontal line
    int iterations = img->h / 2;
    int width = img->h;

    pixel_t tmp;

    for (int column = 0; column < img->w; column++) {
        for (int row = 0; row < iterations; row++)
        {
            // store pixel in column to tmp, copy pixel from (width - column) to column and store tmp to (width - column)
            // store pixel of column to tmp
            tmp = img->img[column + row * width ];
            // copy pixel from width - column to column
            img->img[row * width + column] = img->img[column + (width - 1 - row) * width];
            // store tmp to width - column
            img->img[column + (width - 1 - row) * width] = tmp;
        }
    }
}


void resize(image_t *img, int new_width, int new_height) {
    int orig_width = img->w;
    int orig_height = img->h;

    // create black pixel
    pixel_t black_pxl = {0, 0, 0};

    // create new pixel array of new image size to store pixels there to make things a lot easier since arrays are static sized
    pixel_t *resized_img = malloc(new_width * new_height * sizeof(pixel_t));

    for (int row = 0; row < new_height; row++) {
        for (int col = 0; col < new_width; col++) {
            
            // calculate current pos
            int pos = row * new_width + col;
            // copy pixel from original image to resized one if the col and row is not outside dimensions of original image
            if (col < orig_width && row < orig_height) {
                resized_img[pos] = img->img[col + row * orig_width];
            }
            else {
                // copy black pixel to position which is outside dimensions of original image
                resized_img[pos] = black_pxl;
            }
        }
    }

    // Replace width and height of image with new values
    img->w = new_width;
    img->h = new_height;

    // deallocate old memory of image
    free(img->img);

    // set pointer to the resized image
    img->img = resized_img;
}


