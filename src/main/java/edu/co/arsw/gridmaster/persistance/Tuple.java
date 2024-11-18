package edu.co.arsw.gridmaster.persistance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Tuple<T1, T2> {

    T1 o1;
    T2 o2;

    @JsonCreator
    public Tuple(@JsonProperty("o1") T1 o1,
                 @JsonProperty("o2") T2 o2) {
        super();
        this.o1 = o1;
        this.o2 = o2;
    }

    public T1 getFirst() {
        return o1;
    }

    public T2 getSecond() {
        return o2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.o1);
        hash = 17 * hash + Objects.hashCode(this.o2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (!Objects.equals(this.o1, other.o1)) {
            return false;
        }
        if (!Objects.equals(this.o2, other.o2)) {
            return false;
        }
        return true;
    }


}
