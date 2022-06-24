package ru.anr.base.samples.services;

import ru.anr.base.samples.domain.Samples;

/**
 * Testing several extensions.
 *
 * @author Alexey Romanchuk
 * @created May 11, 2022
 */
public interface TestDataService {
    Object doExtension(String ext);

    Samples doInTransaction(Samples o, boolean reload);

    void deleteInTransaction(Samples s);
}
