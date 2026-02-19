package org.valdon.abtests.mappers;

import java.util.List;

public interface Mappable<E, D> {

    E toEntity(D dto);
    D toDto(E entity);
    List<D> toDto(List<E> entity);
    List<E> toEntity(List<D> dto);

}
