package by.academy.lesson8.part2;

import java.util.Objects;

import by.academy.lesson8.part2.data.InfoEntity;

public interface InfoEntityMatcher<T extends InfoEntity> {

    boolean isMatches(T r, String lowerCase);

}
