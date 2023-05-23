#include "list.h"

#include <assert.h>
#include <stdlib.h>

#include "err.h"
#include "util.h"

/**
 * Struct for encapsulating a single list element.
 */
typedef struct ListItem {
    struct ListItem* next;  // pointer to the next element (NULL if last)
    void* data;             // pointer to the data
} ListItem;

List mkList(void) {
    List res;
    res.head = NULL;
    return res;
}

void clearList(List* s) {
    // s->head = NULL;
    // TODO Implement me !
    NOT_IMPLEMENTED;
    UNUSED(s);
}

void push(List* s, void* data) {
    ListIterator it = mkIterator(s);

    if (!isEmpty(s)) {
        while (it.current->next != NULL) {
            next(&it);
        }

        next(&it);
        it.current = malloc(sizeof(ListItem));
        it.current->next = NULL;
        it.current->data = data;
    } else {
        // it.current = malloc(sizeof(ListItem));
        // ListItem* element = malloc(sizeof(ListItem));
        // element->next = NULL;
        // element->data = data;
        // s->head = element;
        // free(element);
    }
}

void* peek(List* s) {
    ListIterator it = mkIterator(s);
    if (!isEmpty(s)) {
        while (it.current->next != NULL) {
            next(&it);
        }
    }
    return getCurr(&it);
}

void pop(List* s) {
    // TODO Implement me!
    NOT_IMPLEMENTED;
    UNUSED(s);
}

char isEmpty(List* s) { return (s->head == NULL) ? 1 : 0; }

ListIterator mkIterator(List* list) {
    ListIterator res;
    res.list = list;
    res.prev = NULL;
    res.current = list->head;

    return res;
}

void* getCurr(ListIterator* it) {
    assert(it->current != NULL);
    return it->current->data;
}

void next(ListIterator* it) {
    assert(isValid(it));
    it->prev = it->current;
    it->current = it->current->next;
}

char isValid(ListIterator* it) { return it->current != NULL; }
