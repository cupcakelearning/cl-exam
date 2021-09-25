package com.cupcake.learning.exam.base.repository.dynamo;


import com.cupcake.learning.exam.base.model.entity.dynamo.PublishedExam;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PublishedExamRepository extends PagingAndSortingRepository<PublishedExam, UUID> {
    @NotNull
    @Override
    List<PublishedExam> findAllById(@NotNull Iterable<UUID> iterable);
}
