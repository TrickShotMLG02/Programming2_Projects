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
    ListIterator it = mkIterator(s);
    int elementCount = 1;

    // check if list is not empty
    if (!isEmpty(s)) {
        // while there is a next element, grab it, increment counter and recheck
        while (it.current->next != NULL) {
            next(&it);
            elementCount++;
        }

        // check if there are any elements left
        while (elementCount > 0) {
            // check if there is a next element, if so grab it and check again
            while (it.current->next != NULL) {
                next(&it);
            }
            // free last element
            free(it.current);
            // recreate iterator to start from beginning of list
            it = mkIterator(s);
            // decrement counter
            elementCount--;
        }
    }
}

void push(List* s, void* data) {
    ListIterator it = mkIterator(s);

    // check if there are any elements in list
    if (!isEmpty(s)) {
        // get last element
        while (it.current->next != NULL) {
            next(&it);
        }

        // allocate space for next element
        next(&it);
        it.current = malloc(sizeof(ListItem));
        // store data and null for next element
        it.current->next = NULL;
        it.current->data = data;
    } else {
        // allocate space for element
        s->head = malloc(sizeof(ListItem));
        // store data and null to head
        s->head->next = NULL;
        s->head->data = data;
    }
}

void* peek(List* s) {
    ListIterator it = mkIterator(s);
    if (!isEmpty(s)) {
        // get last element
        while (it.current->next != NULL) {
            next(&it);
        }
    }
    // return content of last element
    return getCurr(&it);
}

void pop(List* s) {
    ListIterator it = mkIterator(s);
    ListItem* sl_element = it.current;

    if (!isEmpty(s)) {
        // grab last element which has no next element
        while (it.current->next != NULL) {
            sl_element = it.current;
            next(&it);
        }

        // check if there is only one element in list
        if (sl_element == it.current) {
            // free element and set head to NULL
            free(sl_element);
            s->head = NULL;
        } else {
            // free last element
            free(it.current);

            // reset iterator to beginning of list
            it = mkIterator(s);

            // grab last element which has no next element
            while (it.current->next != NULL) {
                next(&it);
            }

            // set next element to NULL
            it.current->next = NULL;
        }
    }
}

char isEmpty(List* s) {
    // check if head is NULL
    return (s->head == NULL) ? 1 : 0;
}

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
