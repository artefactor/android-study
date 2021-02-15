package by.academy.lesson7.part3;

import by.academy.lesson7.part3.data.InfoEntity;

public interface InfoEntityMatcher<T extends InfoEntity> {

    boolean isMatches(T r, String lowerCase);

}
