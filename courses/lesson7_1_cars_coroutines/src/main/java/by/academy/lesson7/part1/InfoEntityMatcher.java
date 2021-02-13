package by.academy.lesson7.part1;

import java.util.Objects;

import by.academy.lesson7.part1.data.InfoEntity;

public interface InfoEntityMatcher<T extends InfoEntity> {

    boolean isMatches(T r, String lowerCase);

}
