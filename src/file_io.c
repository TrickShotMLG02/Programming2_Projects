#include "file_io.h"

#include <assert.h>
#include <stdlib.h>

#include "util.h"

int image_read(image_t *img, FILE *fin) {
    NOT_IMPLEMENTED;
    UNUSED(img);
    UNUSED(fin);
    return -1;
}

void image_write(const image_t *img, FILE *fout) {
    NOT_IMPLEMENTED;
    UNUSED(img);
    UNUSED(fout);
}

void image_free(image_t *img) {
    if (img->img != NULL) {
        free(img->img);
    }
    img->w = img->h = 0;
    img->img = NULL;
}
