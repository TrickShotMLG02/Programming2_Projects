#include "flood_fill.h"

#include <stdbool.h>
#include <stdlib.h>

#include "util.h"

void flood(image_t *img, int x, int y, pixel_t *target_color) {
    if (x < 0 || y < 0 || x >= img->w || y >= img->h) return;

    NOT_IMPLEMENTED;
    UNUSED(img);
    UNUSED(x);
    UNUSED(y);
    UNUSED(target_color);
}
