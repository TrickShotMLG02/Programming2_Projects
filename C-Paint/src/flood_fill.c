#include "flood_fill.h"
#include <stdbool.h>
#include "structures.h"
#include "util.h"
#include <string.h>


// function signatures
int convertToIndex(int, int, int);
void checkNeighbours(image_t*, int, int, pixel_t*, pixel_t*);


void flood(image_t *img, int x, int y, pixel_t *newColor) {
    int width = img->w;
    int height = img->h;

    // stop recursion if new position would be outside of dimensions of image
    if (x < 0 || y < 0 || x >= width || y >= height)
        return;

    // Get target color of original pixel
    pixel_t originalColor = img->img[convertToIndex(x, y, width)];

    // Check all neighbors recursively
    checkNeighbours(img, x, y, &originalColor, newColor);
}


// convert x and y to the associated array index
int convertToIndex(int x, int y, int width) { return x + y * width; }


// check and fill neighboured pixels with new color if they have the same original color
void checkNeighbours(image_t *img, int x, int y, pixel_t *orignalColor, pixel_t *newColor) {
    int width = img->w;
    int height = img->h;

    // If position is outside of the image dimensions, stop recusion since there is nothing left to do
    if (x < 0 || y < 0 || x >= width || y >= height)
        return;

    // If pixel has the same color as the new one, stop recusion otherwhise we would have an infinite loop
    if (memcmp(&img->img[convertToIndex(x, y, width)], newColor, sizeof(pixel_t)) == 0)
        return;

    // If pixel has a different color than the target color, return since we only apply the new color to pixels with same color as the starting one
    if (memcmp(&img->img[convertToIndex(x, y, width)], orignalColor, sizeof(pixel_t)) != 0)
        return;

    // save new color to pixel at current x|y position
    img->img[convertToIndex(x, y, width)] = *newColor;

    // check all neighbour pixels for same color and repaint them if needed
    checkNeighbours(img, x + 1, y, orignalColor, newColor);
    checkNeighbours(img, x - 1, y, orignalColor, newColor);
    checkNeighbours(img, x, y + 1, orignalColor, newColor);
    checkNeighbours(img, x, y - 1, orignalColor, newColor);
}