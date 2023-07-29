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
    int height = img->h;

    // switch width and height
    img->h = width;
    img->w = height;

    // create new image to store rotated pixels
    pixel_t *rotated_img = malloc(width * height * sizeof(pixel_t));

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            // calculate current pos from row and col
            int pos = column + width * row;

            // calculate the new index based on the rotated coordinates
            int new_row = column;
            int new_column = height - 1 - row;
            int new_pos = new_column + height * new_row;

            // copy the pixel to the new position
            rotated_img[new_pos] = img->img[pos];
        }
    }

    // deallocate old memory of image
    free(img->img);

    // set pointer to the rotated image
    img->img = rotated_img;

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
    // grab smaller value of height and width and divide it by 2 to get iterations needed to switch pixels per vertical line
    int iterations = (img->w < img->h) ? img->w : img->h;
    iterations /= 2;
    int width = img->w;

    pixel_t tmp;

    for (int row = 0; row < iterations; row++) {
        for (int column = 0; column < width; column++)
        {
            // store pixel in column to tmp, copy pixel from (width - column) to column and store tmp to (width - column)
            // store pixel of column to tmp
            tmp = img->img[column + row * width];
            // copy pixel from width - column to column
            img->img[column + row * width] = img->img[column + (img->h - 1 - row) * width];
            // store tmp to width - column
            img->img[column + (img->h - 1 - row) * width] = tmp;
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


