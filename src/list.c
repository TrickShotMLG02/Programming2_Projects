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
    // create new pointer
    ListItem* next;
    // while list not empty
    while (!isEmpty(s)) {
        // store pointer to next element
        next = s->head->next;
        // free current element
        free(s->head);
        // set head to next element
        s->head = next;
    }
}

void push(List* s, void* data) {
    if (isEmpty(s)) {
        s->head = malloc(sizeof(ListItem));
        s->head->next = NULL;
        s->head->data = data;
    } else {
        ListItem* elem = s->head->next;
        s->head->data = data;
        s->head->next = elem;
    }
}

void* peek(List* s) { return (isEmpty(s)) ? NULL : s->head->data; }

void pop(List* s) {
    // create new pointer and set it to next element
    ListItem* element = s->head->next;
    // free head of list
    free(s->head);
    // set head of list to new top element
    s->head = element;
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
