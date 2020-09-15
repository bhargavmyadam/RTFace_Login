package com.example.iris.login1;

import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.random;

public class AnimalIcon{
    private String Name;
    private int count;


    public AnimalIcon(String name, int Count) {
        Name = name;
        count = Count;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "name  " + getName() + "  count  " + getCount();
    }
}
class iconComparator implements Comparator<AnimalIcon> {
    Random random = new Random();

    public int Hash()
    {
        // e.g.
        int x = random.nextInt( 2);
        if (x==1){
            return 1;
        }
        else{
            return -1;
        }

    }

    @Override
    public int compare(AnimalIcon o1, AnimalIcon o2) {
//       int result =  o1.getCount() <  o2.getCount() ? -1 : o1.getCount() == o2.getCount() ? Hash() : 1;
        int result =  o1.getCount() <  o2.getCount() ? -1 : o1.getCount() == o2.getCount() ? 0 : 1;

        return result;
    }
}
