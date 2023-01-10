package org.dionthorn.isekairpg.items;

public interface Stackable {

    boolean stack(Stackable other); // stack one object with the other if same item returns true and stacks
    int getAmount(); // return the size of the stack

}
