package by.academy.lesson8.part2.adapter;

import by.academy.lesson8.part2.entity.InfoEntity;

interface InfoEntityMatcher<T extends InfoEntity> {

    boolean isMatches(T r, String lowerCase);

}
