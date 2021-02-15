package by.academy.lesson7.part4;

import by.academy.lesson7.part4.data.InfoEntity;

public interface InfoEntityMatcher<T extends InfoEntity> {

    boolean isMatches(T r, String lowerCase);

}
